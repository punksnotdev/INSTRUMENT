# SEQFIX — Sequencer Timing Precision Overhaul

## Overview

The INSTRUMENT sequencer currently uses a 500 Hz polling loop (Tdef) that ticks every 2ms,
checks every track on every tick, and counts beats via floating-point modulo arithmetic.
This creates 2-7ms of jitter — audible on transients and hi-hats.

This document describes a refactor to **event-scheduled Routines + server.latency bundling**,
which achieves sample-accurate synth triggers (~0.023ms at 44.1kHz) while preserving
live-codeable hot-swapping of patterns.

---

## Current Architecture (What Exists)

### Files involved

| File | Role |
|------|------|
| `Classes/Core/Sequencing/I8TSequencer.sc` | Main tick loop, beat counting, queue, looper coordination |
| `Classes/Core/Sequencing/v2/I8TSequencer2.sc` | Extension: queue system (`addToQueue`, `queueDo`), `initV2`, `setTimeSignature` |
| `Classes/Core/Sequencing/I8TSequencerTrack.sc` | Per-instrument track: holds ParameterTracks, delegates `fwd` |
| `Classes/Core/Sequencing/I8TParameterTrack.sc` | Per-parameter sequencing: event lookup, triggering instrument |
| `Classes/Core/Sequencing/I8TSynthPlayer.sc` | Synth creation/triggering (poly/mono), note parsing |
| `Classes/Core/Sequencing/I8TPatternEvent.sc` | Pattern wrapper: speed, repeat, transformations |
| `Classes/Core/Sequencing/I8TPattern.sc` | Pattern data: parsed string -> array of Events with val/duration/amplitude/rel |
| `Classes/Core/I8TMain.sc` | Framework init; creates TempoClock at line 130: `clock = TempoClock.new(TempoClock.default.tempo)` |
| `Classes/Core/SCextensions/SequenceableCollectionFindNearest.sc` | Binary search extension used by ParameterTrack |

### How the tick loop works (I8TSequencer.sc:95-203)

1. A `Tdef` runs an `inf.do` loop (line 102)
2. Every iteration waits `(1/tickTime).wait` = `1/500` = 0.002 seconds (line 192)
3. Each tick:
   - Checks if current beat is a bar boundary -> processes queue, looper states (lines 103-166)
   - Forwards ALL sequencer tracks: `sequencerTracks.do({|track| track.fwd(ticks)})` (lines 170-172)
   - Increments `ticks` (line 176), resets at `maxTicks` = 1,800,000 (line 177-179)
   - Detects beat boundaries via `ticks % ((60/main.tempo)*tickTime) < 1` (line 181)

### How events fire (I8TParameterTrack.sc:89-126, 591-637)

1. `SequencerTrack.fwd(i)` calls `parameterTracks.collect({|p| p.fwd(i)})` (I8TSequencerTrack.sc:59)
2. `ParameterTrack.fwd` calls `durationSequencer.value()` every tick (line 108)
3. `durationSequencer` calls `getCurrentEventNew()` (line 71)
4. `getCurrentEventNew()`:
   - Converts ticks to beat position: `patternPosition = (currentTick / tickTime) * speed` (line 599-600)
   - Wraps around: `patternPosition % sequenceDuration` (line 604)
   - Binary-searches `newSequenceInfo.indices` for nearest event (lines 604-606)
   - If position has passed the event and it hasn't been played, returns it (lines 625-628)
5. If event returned, calls `track.instrument.trigger(name, currentEvent)` (line 80)

### How synths are created (I8TSynthPlayer.sc:120-197)

- `createSynth` wraps Synth.new in `main.server.bind {}` (lines 162-168, 173-181)
- `server.bind` bundles OSC with a timestamp, but the timestamp is "now" — the jitter
  from the tick loop is already baked in

### The sequence timeline (I8TParameterTrack.sc:450-527)

- `updateSequenceInfo()` builds `newSequenceInfo` (an `Order`, which is a sorted dictionary)
- Keys = beat positions (floats), values = Events
- Accounts for pattern repetitions and speed scaling
- This is rebuilt when patterns are added/removed, not on every tick

### The binary search (SequenceableCollectionFindNearest.sc)

- `indexOfNearestIrregularIndex` delegates to `searchIndex` which is a recursive binary search
- O(log n) per call, NOT O(n) — but still called twice per parameter track per tick
  (once for `findNearest`, once for `indexOfNearestIrregularIndex` at lines 604-606)

---

## Problems (Why It Needs Fixing)

### Problem 1: 2ms quantization floor

The tick loop runs at 500 Hz. Events can only fire on tick boundaries.
At 44.1kHz, 2ms = ~88 samples. Even in the best case, note onsets are quantized
to a 2ms grid. This is audible as "stiffness" on syncopated rhythms and fast rolls.

### Problem 2: sclang scheduling jitter on top of quantization

sclang is single-threaded. The `(1/tickTime).wait` call yields to a TempoClock,
which also handles GUI, OSC construction, and garbage collection. Actual tick intervals
vary from 1.5ms to 5ms+, with occasional 10-50ms spikes during GC pauses.

### Problem 3: Floating-point beat drift (I8TSequencer.sc:181)

```supercollider
if( ticks % ((60/main.tempo)*tickTime) < 1 )
```

`main.tempo` returns `clock.tempo * 120` (I8TMain.sc:700). At non-round tempos,
`(60/tempo)*500` is irrational. Example at 130 BPM: `(60/130)*500 = 230.769...`

The `< 1` tolerance means beats can fire one tick early at some tempos, and the
cumulative error grows over time. After enough ticks, a beat may double-fire or skip.

### Problem 4: Wasted computation per tick

Every 2ms, the loop:
- Iterates all sequencer tracks (`sequencerTracks.do`, line 170)
- Each track iterates all parameter tracks (`parameterTracks.collect`, I8TSequencerTrack.sc:59)
- Each parameter track runs two binary searches (`findNearest` + `indexOfNearestIrregularIndex`)

With 4 instruments x 2 parameters x 500 ticks/sec = 4000 binary search pairs/second,
where ~99% find nothing to trigger.

Note: `SequencerTrack.fwd` uses `.collect` instead of `.do` (I8TSequencerTrack.sc:59),
creating a throwaway Array on every call. Same at lines 70, 77, 143, 149. At 500 Hz
this generates ~3000+ garbage arrays/second, increasing GC pressure and causing jitter spikes.

### Problem 5: server.bind doesn't recover lost precision (I8TSynthPlayer.sc:162-168)

`server.bind` timestamps the OSC bundle at the moment sclang executes it. But the
decision to trigger was made by the tick loop, which already has 2-7ms of jitter.
The precise server-side execution can't compensate for imprecise scheduling.

### Problem 6: Redundant condition checks (I8TSequencer.sc:103-166)

- Line 103: `beats % timeSignature.beats == 0` — checked 500x/sec, true ~2x/sec at 120 BPM
- Line 109: Same condition as line 103 (redundant)
- Line 115: `beats % 1 == 0` — always true since beats is an integer
- Line 141: `ticks % tickTime == 0` — true once per second (tick 500, 1000, ...)
  This gates singleFunctions/repeatFunctions to fire only once per second, which may
  not match the user's intent

---

## Key SuperCollider Concepts (Reference)

### Class hierarchy / call chain

```
Sequencer (I8TSequencer.sc)
  owns: IdentityDictionary of SequencerTrack (keyed by instrument name)
    │
    ├── SequencerTrack (I8TSequencerTrack.sc)
    │     owns: IdentityDictionary of ParameterTrack (keyed by parameter name, e.g. \note, \trigger)
    │       │
    │       └── ParameterTrack (I8TParameterTrack.sc)
    │             owns: sequence (List of PatternEvent), newSequenceInfo (Order)
    │             calls: track.instrument.trigger(name, event)
    │               │
    │               └── I8TSynthPlayer.sc (or other Sequenceable)
    │                     calls: createSynth() -> Synth.new inside server.bind
```

A single `.seq(\bass, \note, 0, "60 62 64")` call flows:
`Sequencer.addPattern` -> `SequencerTrack.addPattern` -> `ParameterTrack.addPattern`
-> parses string into `I8TPattern` -> wraps in `PatternEvent` -> adds to `sequence` list
-> calls `updateSequenceInfo` to rebuild the `Order` timeline

### How `main.tempo` works

`I8TMain.sc` creates a TempoClock at line 130: `clock = TempoClock.new(TempoClock.default.tempo)`

TempoClock's `.tempo` property is in **beats per second** (not BPM). Default is 1.0 bps.

`main.tempo` (I8TMain.sc:700) returns `clock.tempo * 120` — converting to BPM.
`main.tempo_(bpm)` (I8TMain.sc:707) sets `clock.tempo = bpm / 120`.

So at 120 BPM: `clock.tempo = 1.0` (1 beat per second).
At 130 BPM: `clock.tempo = 1.0833...`

This matters for the beat detection formula: `(60 / main.tempo) * tickTime`
= `(60 / (clock.tempo * 120)) * 500` = `(0.5 / clock.tempo) * 500`

### What `Order` is

`Order` is a SuperCollider class — a sorted dictionary where keys are numeric indices
(floats or ints) and values are arbitrary. Unlike `Array`, keys can be non-contiguous.
`newSequenceInfo` uses beat positions as keys (e.g., 0.0, 0.25, 0.5, 1.0, ...) and
pattern Events as values. `Order.indices` returns the sorted array of keys.

In the new architecture, the Routine iterates `newSequenceInfo.do {|event, beatPosition| ... }`
which walks the events in chronological order.

### Why Tdef is wrong for tight timing

`Tdef` is designed for live-codeable task replacement — you can redefine its function
while it's playing, and it crossfades. This is great for improvisation but adds overhead
and indirection in the inner timing loop. More fundamentally, using any sclang-side
**polling** loop (Tdef, Task, Routine with fixed-interval .wait) for timing is fighting
SuperCollider's architecture. sclang is designed to **schedule** events on clocks, not
poll for them.

### TempoClock: how sync works

TempoClock maintains a **priority queue** of scheduled events, ordered by beat number.
When a Routine calls `0.25.wait`, the clock calculates `currentBeat + 0.25` and inserts
the Routine's continuation into the queue at that beat position. When that beat arrives,
the continuation executes.

**Multiple Routines on the same clock are automatically in sync.** If Routine A is
scheduled for beat 4.0 and Routine B is also scheduled for beat 4.0, both fire at
beat 4.0. sclang processes them sequentially (it's single-threaded), but they share
the same logical time — and crucially, both see the same `thisThread.beats` value.

**`quant` parameter**: When a Routine starts with `.play(clock, quant: 4)`, TempoClock
delays its first execution until the next beat that's a multiple of 4 (i.e., the next
bar boundary in 4/4). This is how newly added patterns align to the existing grid
without manual synchronization.

**Tempo changes**: TempoClock converts between beats and seconds internally. When tempo
changes, all scheduled events stretch/compress together. A Routine waiting for beat 8.0
will fire at beat 8.0 regardless of tempo changes between now and then.

### server.makeBundle vs server.bind

Both send OSC messages as bundles to scsynth, but they differ in timestamp handling:

- **`server.bind { ... }`** (currently used in I8TSynthPlayer.sc:162):
  Collects all OSC messages in the block and sends them as a bundle timestamped with
  the **current** server time. This means "execute now" — no lookahead.

- **`server.makeBundle(latency, { ... })`** (what we'll use):
  Same collection of OSC messages, but timestamped `latency` seconds in the **future**.
  scsynth receives the bundle early and holds it in a time-sorted queue. When the
  timestamp arrives, scsynth executes all messages in the bundle at the exact right
  **sample** — true sample-accurate timing.

  The `latency` parameter (e.g. 0.05 = 50ms) must be large enough to absorb the
  worst-case sclang scheduling jitter. If a Routine fires 3ms late but latency is 50ms,
  the bundle still arrives 47ms early — plenty of time.

### The three layers of timing precision

1. **TempoClock scheduling** (~0.2-1ms): When the Routine wakes up and sends the message.
   This is the "decision" layer — good enough for most purposes.
2. **server.latency absorption**: The lookahead window that absorbs sclang jitter.
   As long as the message arrives before its timestamp, precision is perfect.
3. **scsynth OSC execution** (~0.023ms at 44.1kHz): scsynth processes timestamped bundles
   at the exact sample boundary. This is the final output precision — sample-accurate.

The current tick loop collapses all three layers into layer 1 (sclang scheduling) and
adds a 2ms quantization grid on top. The refactor separates them properly.

---

## Rejected Alternatives

### Increasing tick rate above 500 Hz

After the Phase 1 hygiene fixes (.do, caching), the per-tick cost would drop enough
to safely push to 1000-2000 Hz. But this halves the quantization floor from 2ms to 1ms
while still leaving sclang jitter (0.5-5ms) as the dominant factor. Since we're moving
to event-scheduled Routines (which eliminate polling entirely), optimizing the tick rate
is optimizing a dead end.

### Skip-early optimization (without architecture change)

Instead of running `getCurrentEventNew()` on every tick, cache the next event's beat
position after each trigger. In `fwd()`, compare `currentTick` against the cached
threshold — if we haven't reached it, return immediately without searching.

This would cut the ~4000 binary search pairs/second down to ~20-50 (only when events
actually fire). The tick loop still runs at 500 Hz but each iteration does almost nothing.

**Why rejected**: It's a good optimization within the polling architecture, but doesn't
fix the fundamental 2ms quantization or sclang jitter. Since we're doing the event-scheduled
refactor anyway, this is unnecessary complexity.

### Server-side Demand UGens (Impulse + SendReply)

Move sequencing logic entirely into scsynth using `Impulse.kr`, `Stepper.kr`, `BufRd.kr`,
and `SendReply.kr`. The sequence lives in a Buffer; the server reads it at exact sample
rate with zero jitter.

**Why rejected**: True sample-accurate, but sequences can only be changed via buffer writes.
This loses the language-side pattern manipulation that makes INSTRUMENT a live coding
framework — hot-swapping patterns, transformations (reverse, mirror, rotate), probability
operations, etc. The event-scheduled + server.latency approach achieves effectively the
same output precision while keeping all language-side flexibility.

---

## Target Architecture

### Core principle

Replace polling with event scheduling. Instead of ticking 500x/sec and asking
"should something happen now?", calculate when each event should happen and
schedule it on the TempoClock. Use `server.latency` to achieve sample-accurate output.

### Sync mechanism

All Routines play on the same `main.clock` (TempoClock). The TempoClock maintains a single
beat timeline with a priority queue. Two events scheduled at beat 4.0 execute at beat 4.0
regardless of when they were scheduled. `quant` aligns new patterns to bar boundaries.

`server.latency` (e.g. 0.05s) means OSC bundles are sent early with future timestamps.
scsynth executes them at exactly the right sample. sclang jitter doesn't matter as long
as messages arrive before their timestamp.

---

## Implementation Plan

### Phase 1: Quick Hygiene Fixes (no architecture change)

These reduce jitter within the existing tick loop and should be done first as a safety net.
They also make the codebase cleaner for the Phase 2 refactor.

#### 1a. Replace `.collect` with `.do` where return value is unused

**I8TSequencer.sc:**
- Line 117: `loopers.collect(` -> `loopers.do(`
- Line 119: `stateArray.collect(` -> `stateArray.do(`
- Line 146: `repeatFunctions.collect(` -> `repeatFunctions.do(`
- Line 148: `f.collect(` -> `f.do(`
- Line 170: `sequencerTracks.collect(` -> `sequencerTracks.do(` (already .do in current code, verify)

**I8TSequencerTrack.sc:**
- Line 59: `parameterTracks.collect(` -> `parameterTracks.do(`
- Line 70: `parameterTracks.collect(` -> `parameterTracks.do(`
- Line 77: `parameterTracks.collect(` -> `parameterTracks.do(`
- Line 143: `parameterTracks.collect(` -> `parameterTracks.do(`
- Line 149: `parameterTracks.collect(` -> `parameterTracks.do(`

#### 1b. Remove the cosmetic Task in addPattern

**I8TSequencer.sc lines 302-317:**
Replace the `Task.new({0.1.wait; ...}).play` with a direct `.postln` call.
This removes a scheduled task that competes for clock time.

### Phase 2: Event-Scheduled ParameterTrack

This is the main refactor. Each ParameterTrack runs its own Routine that waits exact
beat durations between events, instead of being polled 500x/sec by the tick loop.

#### 2a. Refactor ParameterTrack to use its own Routine

**File: I8TParameterTrack.sc**

Remove:
- `durationSequencer` (the function called every tick, lines 68-85)
- `getCurrentEventNew()` (the tick-based position calculation, lines 591-637)
- `getCurrentEvent()` (the old version, lines 547-588)
- `currentTick`, `lastTick`, `nextDuration` instance vars
- `fwd` method (lines 89-126) — this is the tick-driven forwarding, no longer needed

Add a new `play` method that starts a Routine:

```supercollider
play {|position|
    var startBeat;

    playing = true;

    // Stop existing routine if any
    if(routine.notNil) { routine.stop };

    routine = Routine({
        // If resuming from a position, seek into the sequence
        startBeat = position ? 0;

        loop {
            // Walk through the sequence timeline
            newSequenceInfo.do {|event, beatPosition|
                var waitBeats;

                // Calculate wait time from current position to this event
                waitBeats = (beatPosition / speed) - startBeat;

                if(waitBeats > 0) {
                    waitBeats.wait;  // TempoClock handles exact scheduling
                };

                if(waitBeats >= 0) {
                    // Trigger via server bundle with latency for sample accuracy
                    main.server.makeBundle(main.server.latency, {
                        track.instrument.trigger(name, event);
                    });
                };
            };

            // After all events, wait remaining time to complete the cycle
            var remainingBeats = (sequenceDuration / speed) - (newSequenceInfo.indices.last / speed);
            if(remainingBeats > 0) { remainingBeats.wait };

            startBeat = 0;  // Reset for next cycle
        };
    }).play(main.clock, quant: main.sequencer.timeSignature.beats);
}

stop {|position|
    playing = false;
    if(routine.notNil) { routine.stop; routine = nil };
}

go {|time|
    // Restart the routine from the given position
    this.stop;
    this.play(time);
}
```

Key changes:
- `waitBeats.wait` yields to TempoClock with exact beat duration — no polling
- Events fire with TempoClock precision (~0.2-1ms), not tick quantization (2-7ms)
- `quant` parameter aligns start to bar boundary — all tracks sync automatically
- The `played` flag tracking in getCurrentEventNew is eliminated — the Routine
  naturally advances through events in order
- No binary search needed — events are iterated sequentially as they come due

#### 2b. Handle pattern hot-swapping

When `.seq()` is called during playback to replace a pattern, the new Routine must
start at the right time. In `addPattern` (I8TParameterTrack.sc:156-293):

After `this.updateSequenceInfo()` (line 275), if the track is already playing:

```supercollider
if(playing == true) {
    // Restart routine, quantized to next bar
    this.stop;
    this.play;
    // quant in play() ensures it starts on the next bar boundary
};
```

The existing `startSeq` flag mechanism (lines 279-283) is replaced by this restart.

#### 2c. Add server.makeBundle wrapping in ParameterTrack (not per-instrument)

`ParameterTrack` calls `track.instrument.trigger(name, event)` — but the instrument
could be `I8TSynthPlayer`, `InstrumentGroup`, or any `Sequenceable` subclass.
Adding `triggerBundled` to every instrument type would be fragile.

Instead, wrap the trigger call in the ParameterTrack Routine itself:

```supercollider
// In the Routine (inside ParameterTrack.play):
main.server.makeBundle(main.server.latency, {
    track.instrument.trigger(name, event);
});
```

This way, whatever OSC messages `trigger` generates (Synth.new, synth.set, etc.)
are captured into a single timestamped bundle — regardless of instrument type.

`server.makeBundle(latency, func)` captures all OSC messages generated by `func`
into a single bundle timestamped `latency` seconds in the future. scsynth receives
the bundle early and holds it in a time-sorted queue. When the timestamp arrives,
scsynth executes all messages at the exact right **sample**.

This is the key to sample accuracy: the Routine fires with ~0.2-1ms clock precision,
but the actual synth creation is timestamped for the exact beat moment.

Note: This means `server.bind {}` in `I8TSynthPlayer.createSynth` (lines 162-168,
173-181) becomes redundant — the outer `makeBundle` already bundles everything.
The `server.bind` calls can be removed or left as no-ops (they nest safely).

**Also set server.latency in I8TMain.sc init:**

```supercollider
server.latency = 0.05;  // 50ms lookahead — adjust if needed
```

50ms is a safe starting point. If sclang jitter exceeds this on heavy loads,
increase to 0.1 or 0.2. Lower values reduce perceived input lag for live playing.

### Phase 3: Simplify the Sequencer (remove tick loop)

Now that ParameterTracks schedule themselves, the Sequencer's tick loop is unnecessary.

#### 3a. Remove tick-related state from I8TSequencer.sc

Remove instance variables:
- `ticks` (line 38)
- `tickTime` (line 39)
- `maxTicks` (line 40)
- `beats` (line 20) — use `main.clock.beats` instead
- `clock` (line 17) — the internal clock counter (not the TempoClock)

Remove from `init`:
- `ticks = 0` (line 68)
- `maxTicks = (30 * 60) * 1000` (line 69)
- `tickTime = 500` (line 72)
- `beats = 0` (line 74)
- `clock = 0` (line 78)

#### 3b. Replace the tick loop with clock-scheduled callbacks

Replace the entire `play` method (lines 95-203) with:

```supercollider
play {
    playing = true;

    // Schedule bar-boundary callback on the TempoClock
    barCallback = main.clock.schedAbs(
        main.clock.nextTimeOnGrid(timeSignature.beats),
        {
            // Queue processing
            this.queueDo(\stop);
            this.queueDo(\go);
            this.queueDo(\play);

            // Looper state machine
            this.processLoopers;

            // Single functions (fire-once at specific beats)
            this.processSingleFunctions;

            // Returning the number of beats reschedules automatically
            timeSignature.beats
        }
    );

    // Schedule beat-boundary callback for repeatFunctions
    beatCallback = main.clock.schedAbs(
        main.clock.nextTimeOnGrid(1),
        {
            this.processRepeatFunctions;
            1  // reschedule every beat
        }
    );
}
```

#### 3c. Extract looper/function processing into clean methods

```supercollider
processLoopers {
    loopers.do {|stateArray, looper|
        stateArray.do {|state, stateIndex|
            switch(state,
                \awaitingRec, {
                    looper.performRec(stateIndex);
                    loopers[looper][stateIndex] = \recording;
                },
                \awaitingStart, {
                    looper.performStart(stateIndex);
                    loopers[looper][stateIndex] = \playing;
                },
                \awaitingStop, {
                    looper.performStop(stateIndex);
                    loopers[looper][stateIndex] = \stopped;
                }
            );
        };
    };
}

processSingleFunctions {
    var currentBeat = main.clock.beats.round.asInteger;
    if(singleFunctions[currentBeat].isKindOf(Function)) {
        singleFunctions[currentBeat].value();
    };
}

processRepeatFunctions {
    var currentBeat = main.clock.beats.round.asInteger;
    repeatFunctions.do {|f, k|
        f.do {|rf, l|
            var offset = 0;
            if(rf.offset.isInteger) { offset = rf.offset };
            if((currentBeat - offset) % k.asInteger == 0) {
                rf.function.value();
            };
        };
    };
}
```

#### 3d. Update `stop`, `pause`, `rewind`, `go`

```supercollider
stop {
    playing = false;
    // Cancel scheduled callbacks
    // (main.clock callbacks auto-cancel when they return nil)
    barCallback = nil;
    beatCallback = nil;
    // Stop all tracks
    sequencerTracks.do {|track| track.stop };
    this.go(0);
}

pause {
    playing = false;
    barCallback = nil;
    beatCallback = nil;
    sequencerTracks.do {|track| track.stop };
}

go {|time|
    sequencerTracks.do {|track| track.go(time) };
}

rewind {
    this.go(0);
}
```

Note: `barCallback` and `beatCallback` are stored so we can cancel them.
TempoClock callbacks auto-cancel when they return `nil` instead of a number,
but we need a way to force-cancel on stop/pause. The simplest approach is
to use a flag:

```supercollider
// In the bar callback:
barCallback = main.clock.schedAbs(
    main.clock.nextTimeOnGrid(timeSignature.beats),
    {
        if(playing) {
            this.queueDo(\stop);
            this.queueDo(\go);
            this.queueDo(\play);
            this.processLoopers;
            this.processSingleFunctions;
            timeSignature.beats  // reschedule
        } {
            nil  // stop rescheduling
        }
    }
);
```

#### 3e. Remove `fwd` from SequencerTrack

**I8TSequencerTrack.sc:**

Delete the `fwd` method (lines 56-64) entirely. It was the bridge between the
tick loop and parameter tracks — no longer needed since ParameterTracks self-schedule.

Also replace `.collect` with `.do` in `go` (line 149) and `speed_` (line 143).

### Phase 4: Clean up ParameterTrack

#### 4a. Remove tick-based methods and state

**I8TParameterTrack.sc:**

Remove instance variables:
- `currentTick` (line 29)
- `lastTick` (line 30)
- `nextDuration` (line 31)
- `startSeq` (line 33)
- `waitOffset` (line 26)
- `durationSequencer` (line 20)

Remove methods:
- `getCurrentEvent` (lines 547-588) — old beat-based lookup
- `getCurrentEventNew` (lines 591-637) — tick-based lookup, replaced by Routine iteration

The `played` flag tracking in `getCurrentEventNew` (lines 610-623) becomes unnecessary
because the Routine naturally iterates through events in order.

#### 4b. Simplify `updateSequenceInfo`

The method (lines 450-527) stays mostly the same — it builds the timeline that the
Routine will iterate through. But after rebuilding, if the track is playing, restart
the Routine:

```supercollider
updateSequenceInfo {
    // ... existing code to build newSequenceInfo ...

    // If currently playing, restart with new sequence
    if(playing == true) {
        this.stop;
        this.play;
    };
}
```

#### 4c. Handle speed changes

Currently `speed` is used in `getCurrentEventNew` to scale `patternPosition`.
In the new Routine-based approach, speed scales the wait durations:

```supercollider
// In the Routine:
waitBeats = (beatPosition / speed);
```

When speed changes mid-playback, restart the Routine (it will pick up the new speed).

---

## Migration Checklist

### What gets deleted
- [ ] `I8TSequencer.sc`: The entire `inf.do` tick loop (lines 100-198)
- [ ] `I8TSequencer.sc`: `ticks`, `tickTime`, `maxTicks`, `beats`, `clock` vars and init
- [ ] `I8TSequencerTrack.sc`: `fwd` method (lines 56-64)
- [ ] `I8TParameterTrack.sc`: `fwd` method (lines 89-126)
- [ ] `I8TParameterTrack.sc`: `durationSequencer` function (lines 68-85)
- [ ] `I8TParameterTrack.sc`: `getCurrentEvent` (lines 547-588)
- [ ] `I8TParameterTrack.sc`: `getCurrentEventNew` (lines 591-637)
- [ ] `I8TParameterTrack.sc`: `currentTick`, `lastTick`, `nextDuration`, `startSeq`, `waitOffset`

### What gets added
- [ ] `I8TParameterTrack.sc`: `routine` instance var; Routine-based `play`/`stop`/`go`
- [ ] `I8TParameterTrack.sc`: Wrap `trigger` call in `server.makeBundle` inside Routine
- [ ] `I8TSequencer.sc`: `processLoopers`, `processSingleFunctions`, `processRepeatFunctions`
- [ ] `I8TSequencer.sc`: Clock-scheduled `barCallback` and `beatCallback`
- [ ] `I8TMain.sc`: `server.latency = 0.05` in init

### What gets modified
- [ ] `I8TSequencer.sc`: `play` — from tick loop to clock callbacks
- [ ] `I8TSequencer.sc`: `stop`, `pause`, `go`, `rewind` — cancel callbacks, delegate to tracks
- [ ] `I8TParameterTrack.sc`: `play` — start Routine with `quant`
- [ ] `I8TParameterTrack.sc`: `stop` — stop Routine
- [ ] `I8TParameterTrack.sc`: `go` — restart Routine at position
- [ ] `I8TParameterTrack.sc`: `addPattern` — restart Routine on hot-swap
- [ ] `I8TSequencerTrack.sc`: Remove `.collect` -> `.do` throughout
- [ ] `I8TSequencer.sc`: Remove `.collect` -> `.do` throughout

### What stays unchanged
- `I8TPattern.sc` — pattern parsing is unrelated to timing
- `I8TParser.sc` — string parsing is unrelated to timing
- `I8TPatternEvent.sc` — pattern transformations are unrelated to timing
- `I8TSequencer2.sc` — queue system (`addToQueue`, `queueDo`) stays, just called from
  clock callbacks instead of tick loop
- `SequenceableCollectionFindNearest.sc` — no longer called in hot path, but keep for
  any future use
- All public API: `.seq()`, `.rm()`, `.play()`, `.stop()`, etc. remain identical from
  the user's perspective

---

## Timing Comparison

| Metric | Current (tick loop) | After refactor |
|--------|-------------------|----------------|
| Event scheduling precision | ~2ms quantization | TempoClock native (~0.2-1ms) |
| Typical jitter | 2-7ms | <1ms (absorbed by server.latency) |
| Worst case (GC spike) | 10-50ms+ | Irrelevant if server.latency > spike |
| Synth creation accuracy | ~2-7ms (tick jitter) | Sample-accurate via OSC timestamps |
| CPU per idle tick | 2 binary searches per param track | Zero (no polling) |
| GC pressure | ~3000 arrays/sec from .collect | Near zero |

---

## Testing Strategy

1. **Basic playback**: Single instrument, simple pattern, verify notes fire in correct order
2. **Multi-track sync**: Two instruments with complementary patterns, verify they align
3. **Hot-swap**: Replace a pattern mid-playback, verify new pattern starts on next bar
4. **Tempo change**: Change BPM while playing, verify all tracks adjust together
5. **go/rewind**: Jump to position 0 and arbitrary positions, verify tracks restart correctly
6. **Looper integration**: Record/play loops, verify they sync to bar boundaries
7. **Queue system**: Queue play/stop operations, verify they execute on bar boundaries
8. **singleFunctions/repeatFunctions**: Verify they fire at correct beats
9. **Long-running stability**: Play for 10+ minutes, verify no drift between tracks
10. **Stress test**: 8+ instruments with complex patterns, verify no jitter under load

### How to measure jitter

Use a simple SendReply-based measurement:

```supercollider
// Add to a test SynthDef:
SendReply.kr(Impulse.kr(0), '/noteOn', [timer.value]);

// In sclang, collect timestamps:
var times = List.new;
OSCdef(\jitterTest, {|msg, time|
    times.add(time);
}, '/noteOn');

// After test, analyze:
var deltas = times.differentiate.drop(1);
"Mean: %  StdDev: %  Max: %".format(deltas.mean, deltas.stdDev, deltas.maxItem).postln;
```

---

## Decision Log

All implementation decisions, tradeoffs, and relevant observations should be appended here
as dated entries. This serves as a running record of why things were done a certain way,
what alternatives were considered, and any surprises encountered during implementation.

### 2026-02-11 — Initial analysis and plan

- Identified 500 Hz tick loop as root cause of 2-7ms jitter
- Considered 4 approaches: (1) quick fixes only, (2) event-scheduled Routines,
  (3) server.latency bundling, (4) server-side Demand UGens
- Chose event-scheduled + server.latency as the target: best precision-to-flexibility ratio
  for a live coding framework
- Rejected increasing tick rate above 500 Hz — optimizing a polling architecture we're
  about to replace is wasted effort
- Rejected server-side Demand UGens — sample-accurate but loses language-side pattern
  manipulation, which is essential for live coding
- Discovered `.collect` vs `.do` issue generating ~3000 garbage arrays/sec — fix as Phase 1
- Confirmed `searchIndex` in SequenceableCollectionFindNearest.sc is O(log n) binary search,
  not O(n) linear scan — less catastrophic than initially feared, but still unnecessary
  when polling is eliminated
- Considered "skip-early" optimization (cache next event tick, short-circuit fwd) as a
  stepping stone — rejected because it doesn't fix the 2ms quantization floor and adds
  complexity to code we're about to rewrite
- Considered increasing tick rate to 1000-2000 Hz after hygiene fixes — rejected because
  it's optimizing a polling architecture we're about to delete
- Documented `main.tempo` returns `clock.tempo * 120` (BPM), not beats-per-second.
  At 120 BPM, clock.tempo = 1.0. At 130 BPM, clock.tempo = 1.0833. This is why the
  beat detection formula produces irrational numbers at non-round tempos
