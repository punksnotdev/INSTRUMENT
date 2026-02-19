# Feature: Time Signature

## Current Status

The sequencer stores a `timeSignature` event with two fields:

```supercollider
timeSignature = (
    beats: 4,   // beats per bar
    tick: 1/4   // subdivision (currently unused)
);
```

Set via `setTimeSignature(beats, tick)` in `I8TSequencer2.sc`.

## Where timeSignature is used

Only **two places** in the entire engine reference it:

1. **`I8TSequencer.sc:98,100`** — `barRoutine` waits `timeSignature.beats` and
   quantizes to `timeSignature.beats`. Controls when queue processing and looper
   state transitions fire (play/stop/go at bar boundaries).

2. **`I8TParameterTrack.sc:59`** — Default `quantize` value for aligning a
   ParameterTrack routine start to the next bar boundary.

The `tick` field is **not used anywhere**. The commented-out code in
`I8TSequencer2.sc:21-34` that would have consumed it is dead code.

## What works today

The pattern engine is **fundamentally time-signature-agnostic**. Patterns are
duration-based, not bar-based. `updateSequenceInfo` in `I8TParameterTrack.sc`
sums event durations to build a timeline and never references `timeSignature`.

For simple meters (3/4, 5/4, 7/4, 7/8, etc.), calling `setTimeSignature` before
`play` is enough to get correct bar boundaries:

```supercollider
// 3/4 waltz
i.sequencer.setTimeSignature(3, 1/4);
i.sequencer.play;

// patterns with 3-beat cycles work naturally
i.seq(\piano, \note, 0, "60:1 64:1 67:1");
```

## Known limitations

### 1. `tick` field is inert

The subdivision concept exists in the data model but has zero effect on
scheduling. Removing or populating it changes nothing.

### 2. `beatRoutine` hardcoded to quarter notes

`I8TSequencer.sc:111` always does `1.wait`, meaning `singleFunctions` and
`repeatFunctions` fire on a quarter-note grid regardless of meter. For compound
meters like 6/8 (where the pulse is a dotted quarter = 1.5 beats), the beat
counter doesn't represent the actual musical pulse.

### 3. No runtime time-signature propagation

`barRoutine` and `beatRoutine` capture `timeSignature.beats` at startup in
`play`. Calling `setTimeSignature` while playing has no effect until you
`pause` then `play` again.

### 4. Default `repeats = 4` in SequencerTrack

`I8TSequencerTrack.sc:44` defaults `repeats` to 4, which is a 4/4 assumption.
In 3/4 you'd likely want 3 repeats to fill a bar.

### 5. No meter-aware pattern validation

Nothing warns when a pattern's total duration doesn't align with the current
bar length. A 2-beat pattern in 3/4 will loop at beat 2, crossing bar
boundaries without notice.

## Possible fixes

### Fix 1: Make `beatRoutine` respect the time signature

Derive the beat duration from `tick` so compound meters get correct pulse
timing:

```supercollider
// In I8TSequencer.sc play method, replace:
//   1.wait;
// with:
//   timeSignature.tick.wait;
```

For 6/8 you'd call `setTimeSignature(6, 1/8)` and the beat counter would
tick every eighth note. Alternatively, introduce a separate `pulse` concept
that ticks at the musical beat level (dotted quarter for 6/8) while keeping
the fine-grained tick for subdivisions.

### Fix 2: Propagate time-signature changes at runtime

Restart `barRoutine` and `beatRoutine` when `setTimeSignature` is called:

```supercollider
setTimeSignature {|beats, tick|
    timeSignature = (
        beats: beats,
        tick: tick
    );
    if(playing) {
        this.pause;
        this.play;
    };
}
```

This requires moving the `playing` state check into `setTimeSignature` or
having it call a private `restartRoutines` method.

### Fix 3: Derive default `repeats` from time signature

In `I8TSequencerTrack.sc`, replace the hardcoded default:

```supercollider
// Instead of:
//   repeats = 4;
// Use:
repeats = classSequencer.timeSignature.beats;
```

This way a 3/4 signature gives 3 default repeats, 5/4 gives 5, etc.

### Fix 4: Optional bar-alignment warnings

Add a method to check whether a pattern's total duration is a multiple of
the current bar length:

```supercollider
// In I8TParameterTrack or Sequencer:
checkBarAlignment {|patternDuration|
    var barBeats = track.sequencer.timeSignature.beats;
    if((patternDuration % barBeats) != 0) {
        ("Warning: pattern duration " ++ patternDuration
         ++ " does not align with bar length " ++ barBeats).warn;
    };
}
```

This would be advisory only — polymetric patterns that intentionally cross
bar lines should still be allowed.

### Fix 5: Remove or activate the `tick` field

Either delete `tick` from the time signature (it does nothing and misleads)
or wire it into `beatRoutine` and any subdivision-aware logic. Keeping dead
fields in the data model creates confusion about what the system supports.
