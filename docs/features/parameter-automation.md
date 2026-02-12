# Feature: Continuous Parameter Automation

## Concept

Pattern-driven LFO-style automation and parameter interpolation for synth controls.
Unlike note sequencing (which fires discrete events), this provides smooth continuous
modulation of any synth parameter — filter cutoff sweeps, panning movement, amplitude
fades, FX wet/dry morphing, etc.

## Why the new architecture enables this

The old 500Hz tick loop couldn't support this well — adding continuous parameter
updates to the same loop that handled event detection would have increased per-tick
cost and worsened timing jitter for everything.

With the event-scheduled architecture, there is no global tick loop. A parameter
automation Routine would be an independent, dedicated Routine on `main.clock` running
at whatever rate makes sense for that specific modulation — completely isolated from
event scheduling. It adds zero overhead to note timing.

## Design sketch

Each automation would be its own Routine with a configurable update rate:

```supercollider
// User API concept:
i[\bass].automate(\cutoff, shape: \sine, freq: 0.25, range: [200, 8000]);
i[\bass].automate(\pan, shape: \triangle, freq: 0.5, range: [-1, 1]);
i[\bass].automate(\cutoff, envelope: [0, 1, 0.3, 0], times: [2, 1, 4]);
```

Internally, each automation spawns a Routine:

```supercollider
AutomationRoutine {
    var routine, instrument, parameter, shape, freq, range, updateRate;

    play {
        routine = Routine({
            var phase = 0;
            loop {
                var value = this.computeValue(phase);
                // Use server.makeBundle for sample-accurate parameter changes
                main.server.makeBundle(main.server.latency, {
                    instrument.synth.set(parameter, value);
                });
                phase = phase + (updateRate * freq);
                updateRate.reciprocal.wait;  // e.g., 1/60 = ~16ms for 60Hz updates
            };
        }).play(main.clock);
    }

    computeValue {|phase|
        var normalized = switch(shape,
            \sine,     { (phase * 2pi).sin * 0.5 + 0.5 },
            \triangle, { phase.fold(0, 1) * 2 - 1 * 0.5 + 0.5 },
            \saw,      { phase % 1 },
            \square,   { if(phase % 1 < 0.5, 1, 0) },
            \random,   { 1.0.rand },
            \envelope, { envelope.at(phase) }  // interpolated envelope
        );
        ^normalized.linlin(0, 1, range[0], range[1]);
    }
}
```

## Update rate considerations

- **30-60 Hz** (~16-33ms): Good for smooth filter/pan sweeps. Inaudible stepping
  for most parameters. Low CPU cost.
- **100-200 Hz** (~5-10ms): For parameters where stepping might be audible
  (fast filter modulation, rapid panning).
- **Control rate** (server-side): For truly smooth modulation, use server-side
  LFOs (`SinOsc.kr`, `LFEnvGen.kr`, etc.) instead of language-side Routines.
  This gives block-rate updates (~689 Hz at 64-sample blocks, 44.1kHz) with
  zero language overhead.

The choice between language-side and server-side depends on whether the
modulation needs to be pattern-aware (synced to beats, responsive to live
code changes) or purely continuous.

## Integration with sequencing

Automations should be:
- **Beat-synced**: LFO phase resets on bar boundaries (using `quant`)
- **Tempo-synced**: LFO freq can be expressed in beats (`freq: 1/4` = one cycle per bar)
- **Sequenceable**: Automation parameters themselves could be pattern-driven:
  ```supercollider
  i[\bass].seq(\cutoff_lfo_freq, "0.25:4 0.5:4 1:4");  // change LFO speed per bar
  ```
- **Killable**: `.stopAutomate(\cutoff)` or `.clearAutomate` to remove

## Server-side alternative (higher precision)

For modulation that doesn't need live-code flexibility, use a dedicated modulation
synth on the server:

```supercollider
SynthDef(\lfo, {|out, freq=1, lo=0, hi=1, shape=0|
    var sig = Select.kr(shape, [
        SinOsc.kr(freq),
        LFTri.kr(freq),
        LFSaw.kr(freq),
        LFPulse.kr(freq),
    ]);
    Out.kr(out, sig.range(lo, hi));
}).add;

// Map a control bus to the target parameter:
var lfoBus = Bus.control(server, 1);
Synth(\lfo, [\out, lfoBus, \freq, 0.25, \lo, 200, \hi, 8000]);
instrument.synth.map(\cutoff, lfoBus);
```

This runs at control rate with zero language involvement. Combine with the
language-side approach: use Routines to schedule when LFOs start/stop and
what parameters they map to, while the actual modulation runs on the server.

## Depends on

- Sequencer timing refactor (SEQFIX.md) — the Routine isolation model
- `server.latency` being set (for sample-accurate parameter updates)
