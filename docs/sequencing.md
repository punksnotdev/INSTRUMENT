# INSTRUMENT — Sequencing Architecture

## Overview

The sequencing system converts pattern strings (like `"60 62 64:0.5 67"`) into
timed synth triggers. It's the timing core of INSTRUMENT.

**Status**: Being refactored. See [SEQFIX.md](../SEQFIX.md) for the timing overhaul plan.

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
- State transitions happen at beat boundaries
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

**Timeline construction** (`updateSequenceInfo`, lines 450-527):
1. Iterates `sequence` (list of PatternEvents)
2. For each PatternEvent, gets its repetition count and speed
3. Expands each pattern's events into absolute beat positions
4. Stores in `newSequenceInfo` Order: `beatPosition -> event`
5. Calculates total `sequenceDuration`

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
When an event is due, calls `track.instrument.trigger(parameterName, event)`
where `event` is an Event with `.val`, `.duration`, `.amplitude`, `.rel`.

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
- Wraps `Synth.new` in `server.bind {}` for OSC bundling

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
```

### Event Triggering
```
ParameterTrack detects event is due
  -> track.instrument.trigger(\note, (val: 60, duration: 1, amplitude: 0.5))
    -> I8TSynthPlayer.trigger(\note, event)
      -> Parses note value (60 -> MIDI)
      -> Applies octave offset
      -> Computes amplitude with synth_parameters
      -> In poly mode: createSynth([\freq, 60.midicps, \amp, 0.5, ...])
        -> server.bind { Synth.new(\synthDefName, args, group) }
          -> OSC bundle sent to scsynth
```

### Pattern Hot-Swap
```
User calls i[\bass].note("72 74 76") while playing

-> ParameterTrack.addPattern()
  -> calculateSyncOffset() between old and new pattern durations
  -> Adjusts beat position for seamless transition
  -> Replaces pattern in sequence
  -> updateSequenceInfo() rebuilds timeline
  -> Sets startSeq flag to restart on next appropriate tick
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

## Binary Search Extension (`SequenceableCollectionFindNearest.sc`)

Adds to `SequenceableCollection`:

- `findNearest(n)`: Returns the value at the index nearest to `n`
- `indexOfNearestIrregularIndex(n)`: Returns the index nearest to `n`
- `searchIndex(x, start, end)`: Recursive binary search, O(log n)

Used by ParameterTrack to find which event corresponds to the current position
in the pattern timeline.

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

## Looper Integration

The sequencer coordinates loop recording/playback via state machine:

```
recLooper(looper, layer)   -> sets state to \awaitingRec
  (at beat boundary)       -> looper.performRec(layer), state = \recording
startLooper(looper, layer) -> sets state to \awaitingStart
  (at beat boundary)       -> looper.performStart(layer), state = \playing
stopLooper(looper, layer)  -> sets state to \awaitingStop
  (at beat boundary)       -> looper.performStop(layer), state = \stopped
```

State transitions are quantized to beat boundaries for musical sync.
The actual `I8TLooper` class (in Extensions/Looper/) handles buffer
allocation, recording synths, and playback synths.
