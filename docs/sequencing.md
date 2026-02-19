# INSTRUMENT — Sequencing Architecture

## Overview

The sequencing system converts pattern strings (like `"60 62 64:0.5 67"`) into
timed synth triggers. It's the timing core of INSTRUMENT.

---

## Timing Architecture

The sequencer uses three layers for timing precision:

1. **TempoClock scheduling** (~0.2-1ms): sclang `Routine.wait` on `main.clock`
2. **server.latency absorption** (50ms buffer): `server.makeBundle(latency, func)` timestamps OSC bundles in the future
3. **scsynth bundle execution**: server executes timestamped bundles at the exact sample — but the timestamp itself carries the ~0.2-1ms jitter from layer 1

This achieves sub-millisecond timing precision (~0.2-1ms, same as SC's built-in Pbind/Pattern system), with no polling, no binary searches, and no floating-point beat detection in the hot path. This is below the human perception threshold for onset timing (~2-5ms for trained listeners) but is NOT true sample-accurate — that would require computing bundle timestamps from `clock.beats2secs(targetBeat)` rather than from sclang's wall-clock time, or using server-side Demand UGens.

### Sequencer clock callbacks

The `Sequencer` runs two lightweight Routines on `main.clock`:

- **barRoutine**: Fires every `timeSignature.beats` beats (default: 4). Processes the queue (play/stop/go commands), handles looper state machine transitions.
- **beatRoutine**: Fires every beat. Executes `singleFunctions` and `repeatFunctions`, increments the beat counter.

### ParameterTrack event scheduling

Each `ParameterTrack` runs its own `Routine` that iterates through `newSequenceInfo` (the `Order` timeline) using delta-based wait calls:

```supercollider
routine = Routine({
    loop {
        newSequenceInfo.do {|event, beatPosition|
            waitBeats = (beatPosition - previousBeatPos) / speed;
            if(waitBeats > 0) { waitBeats.wait };
            main.server.makeBundle(main.server.latency, {
                track.instrument.trigger(name, event);
            });
            previousBeatPos = beatPosition;
        };
        remainingBeats = (sequenceDuration - previousBeatPos) / speed;
        if(remainingBeats > 0) { remainingBeats.wait };
    };
}).play(main.clock, quant: main.sequencer.timeSignature.beats);
```

Key properties:
- **Delta-based waits**: Each event waits the exact duration since the previous event, avoiding cumulative float errors
- **`quant` alignment**: New Routines start on bar boundaries automatically
- **`server.makeBundle` wrapping**: All OSC messages (Synth.new, synth.set, etc.) are captured into a single bundle timestamped `server.latency` seconds in the future
- **Zero polling**: No work is done between events — the Routine simply sleeps

---

## Components

### Sequencer (`I8TSequencer.sc`)

Central coordinator. One per `I8TMain` instance.

**Responsibilities:**
- Manages all `SequencerTrack` instances (one per instrument)
- Bar-boundary operations: queue processing, looper state machine
- Beat-boundary operations: `singleFunctions`, `repeatFunctions`
- Pattern CRUD: `addPattern`, `removePattern`, `clearPatterns`
- Transport: `play`, `stop`, `pause`, `go`, `rewind`

**Queue system** (defined in `v2/I8TSequencer2.sc` as class extension):
- `addToQueue(action, q)`: Schedule play/stop/go for next bar boundary
- `queueDo(action)`: Execute all queued items matching action
- Ensures pattern changes land on bar boundaries for musical coherence

**Time signature** (also v2):
- `setTimeSignature(beats, tick)`: e.g., `(beats: 4, tick: 1/4)` for 4/4
- Bar boundary = every `timeSignature.beats` beats

**Looper coordination:**
- State machine: `awaitingRec` -> `recording` -> `awaitingStart` -> `playing` -> `awaitingStop` -> `stopped`
- State transitions happen at bar boundaries
- Actual recording/playback delegated to `I8TLooper` (Extensions/Looper/)

### SequencerTrack (`I8TSequencerTrack.sc`)

One per instrument. Container for `ParameterTrack` instances.

**Responsibilities:**
- Holds an `IdentityDictionary` of `ParameterTrack` keyed by parameter name
  (e.g., `\note`, `\trigger`, `\amp`, `\pan`)
- Delegates pattern operations to the correct `ParameterTrack`
- Has its own `speed` multiplier that propagates to all parameter tracks

### ParameterTrack (`I8TParameterTrack.sc`)

One per parameter per instrument. This is where events actually fire.

**Key state:**
- `sequence`: `List` of `PatternEvent` — the ordered patterns
- `newSequenceInfo`: `Order` (sorted dict) — the flattened timeline
  Keys = beat positions (float), values = Events with val/duration/amplitude/rel
- `sequenceDuration`: Total duration of one cycle through all patterns
- `speed`: Playback speed multiplier
- `playing`: Boolean
- `routine`: The active `Routine` (nil when stopped)

**Timeline construction** (`updateSequenceInfo`):
1. Iterates `sequence` (list of PatternEvents)
2. For each PatternEvent, gets its repetition count and speed
3. Expands each pattern's events into absolute beat positions
4. Stores in `newSequenceInfo` Order: `beatPosition -> event`
5. Calculates total `sequenceDuration`
6. If currently playing, restarts the Routine with the new timeline

Example: pattern `"60:1 62:0.5 64:0.5"` with repeat=2 produces:
```
newSequenceInfo:
  0.0   -> (val: 60, duration: 1)
  1.0   -> (val: 62, duration: 0.5)
  1.5   -> (val: 64, duration: 0.5)
  2.0   -> (val: 60, duration: 1)     // repeat 2
  3.0   -> (val: 62, duration: 0.5)
  3.5   -> (val: 64, duration: 0.5)
sequenceDuration: 4.0
```

**Event triggering:**
The Routine iterates through `newSequenceInfo` in beat order, waiting the delta
between consecutive events, then calls:
```supercollider
main.server.makeBundle(main.server.latency, {
    track.instrument.trigger(parameterName, event);
});
```

**Pattern hot-swap:**
When `addPattern` is called during playback, `updateSequenceInfo` rebuilds the
timeline and restarts the Routine. The `quant` parameter ensures the new sequence
starts on the next bar boundary.

### I8TPattern (`I8TPattern.sc`)

Parsed pattern data. Created from a string or array.

**From string:**
```supercollider
I8TPattern("60:0.5 62 r 64ff")
// -> pattern = [
//      (val: 60, duration: 0.5, amplitude: 0.5),
//      (val: 62, duration: 1, amplitude: 0.5),
//      (val: \r, duration: 1),                      // rest
//      (val: 64, duration: 1, amplitude: 0.7),      // forte
//    ]
// -> totalDuration = 3.5
```

**From array:**
```supercollider
I8TPattern([60, 62, 64])
// -> each element wrapped as (val: n, duration: 1, amplitude: 0.5)
```

`P` is a shorthand alias for `I8TPattern`.

### I8TParser (`I8TParser.sc`)

Converts pattern strings to event arrays. Handles:

| Syntax | Meaning | Example |
|--------|---------|---------|
| `value` | Note/trigger value | `60` |
| `value:dur` | Explicit duration | `60:0.5` |
| `value*amp` | Explicit amplitude | `60*0.8` |
| `ff`, `f`, `p`, `pp` | Forte/piano (relative amp) | `60ff` |
| `valuex3` | Repeat N times | `60x3` -> `60 60 60` |
| `value?` | Maybe (50% probability) | `60?` |
| `value\|alt` | Or (random choice) | `60\|62` |
| `(group):dur` | Subsequence with duration | `(60 62 64):2` |
| `value<` / `value>` | Release time shorter/longer | `60<` |
| ` ` (space) | Separator / rest | `60  64` (rest between) |

### PatternEvent (`I8TPatternEvent.sc`)

Wraps an `I8TPattern` with playback parameters and transformations.

**Parameters:**
- `\speed`: Playback speed multiplier
- `\repeat`: Number of repetitions
- `\waitBefore`: Delay before playing

**Transformations** (modify the pattern array in-place):
- `reverse`, `mirror`, `mirror1`, `mirror2`, `pyramid`
- `random` (scramble), `rotate(n)`, `shift(n)`, `permute(n)`, `lace(n)`
- `maybe(probability)`: Randomly replace events with rests
- `transport(n)`: Transpose all values by N semitones

### I8TSynthPlayer (`I8TSynthPlayer.sc`)

The main instrument that receives trigger calls from ParameterTrack.

**`trigger(parameter, value)` handles:**

| Parameter | Behavior |
|-----------|----------|
| `\note` | Parse pitch (MIDI number, note name like `C4`, `D#5`), handle chords (arrays or comma-separated), create synth in poly/mono mode |
| `\trigger` | Gate trigger with amplitude value |
| `\synthdef` | Change the active SynthDef |
| `\octave` | Set octave offset |
| `\fx` | Set effects chain |
| `\fxSet` | Set FX parameters |
| (other) | Set synth parameter directly via `.set()` |

**Poly mode** (`\poly`, default): Creates a new `Synth` for each note.
Each synth has its own lifecycle (natural release).

**Mono mode** (`\mono`): Reuses a single `Synth`. New notes update frequency
and gate. Tracks pressed keys for legato behavior.

**Synth creation** (`createSynth`):
- Manages node IDs, reuses IDs from finished synths
- Routes through `fxBus` -> `fxSynth` if FX chain exists
- Wraps `Synth.new` in `server.bind {}` (nested safely inside the outer `makeBundle`)

**Operations on values:**
- `\maybe`: Probabilistic — returns value or rest based on probability
- `\or`: Random choice from alternatives

---

## Data Flow

### Pattern Registration
```
User: i[\bass].note("60 62 64")

Sequenceable.seq(\note, "60 62 64")
  -> Sequencer.addPattern(\bass, \note, 0, "60 62 64")
    -> SequencerTrack[\bass].addPattern(\note, 0, "60 62 64")
      -> ParameterTrack[\note].addPattern(0, "60 62 64")
        -> I8TParser.parse("60 62 64")
           returns: [(val:60,dur:1,amp:0.5), (val:62,dur:1,amp:0.5), (val:64,dur:1,amp:0.5)]
        -> I8TPattern wraps parsed events
        -> PatternEvent wraps pattern with speed/repeat params
        -> Added to sequence list and patterns dictionary
        -> updateSequenceInfo() rebuilds Order timeline
        -> If playing, Routine restarts with new timeline
```

### Event Triggering
```
ParameterTrack Routine wakes at scheduled beat position
  -> server.makeBundle(0.05, {
       track.instrument.trigger(\note, (val: 60, duration: 1, amplitude: 0.5))
     })
    -> I8TSynthPlayer.trigger(\note, event)
      -> Parses note value (60 -> MIDI)
      -> Applies octave offset
      -> Computes amplitude with synth_parameters
      -> In poly mode: createSynth([\freq, 60.midicps, \amp, 0.5, ...])
        -> server.bind { Synth.new(\synthDefName, args, group) }
    -> All OSC messages bundled with timestamp = now + 0.05s
      -> scsynth executes at exact sample
```

### Pattern Hot-Swap
```
User calls i[\bass].note("72 74 76") while playing

-> ParameterTrack.addPattern()
  -> Replaces pattern in sequence
  -> updateSequenceInfo() rebuilds timeline
  -> Routine stops and restarts
  -> quant: timeSignature.beats ensures bar-boundary alignment
```

---

## Order (SuperCollider class)

`Order` is a sorted dictionary with numeric keys. Used for `newSequenceInfo`.

- Keys can be floats (beat positions: 0.0, 0.25, 1.5, ...)
- Values are Events
- `Order.indices` returns sorted array of keys
- `Order.do {|value, key| ... }` iterates in key order
- Iteration walks events in chronological order

---

## Scheduled Functions

### Single functions (fire once)
```supercollider
i.when(16, { "beat 16".postln });
// Stored in: sequencer.singleFunctions[16] = function
// Fires when beats == 16
```

### Repeat functions (fire periodically)
```supercollider
i.every(4, { "every 4".postln });
i.every(4, { "offset 2".postln }, offset: 2);
// Stored in: sequencer.repeatFunctions[period][offset] = (function:, offset:)
// Fires when (beats - offset) % period == 0
```

---

## Parameter Proxy Syntax

INSTRUMENT also supports parameter-proxy chaining as an alternative to
`seq(\parameter, pattern)`:

```supercollider
i.bass.rel.seq("2 0.2 1");
i.bass.rel.seq([2, 0.2, 1]);
// equivalent:
i.bass.seq(\rel, "2 0.2 1");
```

Clear/reset helpers on the parameter proxy:

```supercollider
i.bass.rel.clear();   // clears rel parameter patterns
i.bass.rel.reset();   // clear + restore default value when known
```

Stop/remove helpers with optional `clear` flag (`true/false` or `1/0`):

```supercollider
i.bass.rel.stop(clear: true);   // stop + clear rel patterns
i.bass.rel.stop(clear: false);  // stop only, keep patterns
i.bass.rel.rm(clear: true);     // remove patterns and stop
i.bass.rel.rm(clear: false);    // remove patterns only
```

For NodeProxy-backed instruments (`i.z = Proxy(~z)`), `reset()` restores the function
argument default when available:

```supercollider
~z = {|freq=240, fm=20| ... };
i.z = Proxy(~z);
i.z.fm.seq("20 10 1");
i.z.fm.reset();       // sets fm back to 20
```

---

## Looper Integration

The sequencer coordinates loop recording/playback via state machine:

```
recLooper(looper, layer)   -> sets state to \awaitingRec
  (at bar boundary)        -> looper.performRec(layer), state = \recording
startLooper(looper, layer) -> sets state to \awaitingStart
  (at bar boundary)        -> looper.performStart(layer), state = \playing
stopLooper(looper, layer)  -> sets state to \awaitingStop
  (at bar boundary)        -> looper.performStop(layer), state = \stopped
```

State transitions are quantized to bar boundaries for musical sync.
The actual `I8TLooper` class (in Extensions/Looper/) handles buffer
allocation, recording synths, and playback synths.
