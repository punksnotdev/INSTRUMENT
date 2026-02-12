# Feature: Shuffle (Swing)

## Concept

Shuffle shifts "off-grid" events later in time to create swing/groove feel.
A shuffle amount of 0 = perfectly straight, 0.5 = triplet feel, higher = harder swing.

Applied **at runtime in `ParameterTrack.play`** — the stored sequence data stays
clean, and shuffle can be changed live with immediate effect (next event).

## User API

```supercollider
// Per-track shuffle (8th-note swing by default)
i[\kick].shuffle(0.3);

// With explicit grid resolution
i[\hihat].shuffle(0.5, 0.25);   // 16th-note shuffle

// Reset to straight
i[\kick].shuffle(0);
```

## How it works

Given:
- `shuffle`: amount (0.0 – 1.0, default 0)
- `shuffleGrid`: subdivision size in beats (default 0.5 = 8th notes)

For each event at beat position `p`:

1. Find which grid pair it belongs to:
   `pairStart = floor(p / (2 * grid)) * (2 * grid)`
2. Get position within the pair:
   `posInPair = p - pairStart`
3. If the event falls on the second half of the pair (`posInPair >= grid`),
   shift it forward: `newPos = p + (shuffle * grid)`
4. Events on the first half (downbeats) are untouched.

This is computed in the `play` Routine when calculating `waitBeats`, so the
stored `newSequenceInfo` is never modified.

## Grid tolerance

Events rarely land exactly on grid boundaries due to float math.
Use a tolerance of `grid * 0.1` when deciding if an event is "on" vs "off" grid.

## Tradeoffs: runtime vs precalculated

**Chosen: runtime (in `play`)** because:
- Change shuffle live, takes effect next event — no rebuild needed
- `newSequenceInfo` stays clean / represents actual composition
- No risk of double-shuffling on `updateSequenceInfo` re-runs
- Other code reading `newSequenceInfo` (display, export) sees original positions

Precalc (in `updateSequenceInfo`) was rejected because:
- Changing shuffle requires full timeline rebuild on every track
- Double-shuffle footgun: must always rebuild from originals, not already-shuffled data
- Live-tweaking becomes: change value -> rebuild -> wait for next cycle boundary

---

## Implementation Plan

### Step 1: ParameterTrack instance variables
- [x]Add `<>shuffle` (Float, default 0) to var declarations
- [x]Add `<>shuffleGrid` (Float, default 0.5) to var declarations
- [x]Initialize both in `init` method
- **File**: `Classes/Core/Sequencing/I8TParameterTrack.sc`

### Step 2: `applyShuffle` method on ParameterTrack
- [x]Add method after `getScaledDuration`, before `updateSequenceInfo`
- [x]Grid-pair remap logic with float tolerance
- [x]Early return when `shuffle <= 0` (no-op fast path)
- **File**: `Classes/Core/Sequencing/I8TParameterTrack.sc`

```supercollider
applyShuffle {|beatPosition|
    var pairLen, pairStart, posInPair, tolerance;
    if(shuffle <= 0) { ^beatPosition };

    pairLen = shuffleGrid * 2;
    pairStart = (beatPosition / pairLen).floor * pairLen;
    posInPair = beatPosition - pairStart;
    tolerance = shuffleGrid * 0.1;

    if(posInPair >= (shuffleGrid - tolerance)) {
        ^(beatPosition + (shuffle * shuffleGrid))
    };
    ^beatPosition
}
```

### Step 3: Modify `ParameterTrack.play` Routine
- [x]Add `var previousShuffledPos` alongside existing `previousBeatPos`
- [x]Apply `this.applyShuffle(beatPosition)` to get `shuffledPos`
- [x]Compute `waitBeats` from shuffled positions: `(shuffledPos - previousShuffledPos) / speed`
- [x]Track `previousShuffledPos = shuffledPos` after each event
- [x]Initialize `previousShuffledPos` from shuffled `startBeat` at cycle start
- [x]Adjust `remainingBeats` at cycle end to use `previousShuffledPos`
- **File**: `Classes/Core/Sequencing/I8TParameterTrack.sc`

Key change in the Routine loop:
```supercollider
// before:
waitBeats = (beatPosition - previousBeatPos) / speed;
// ...
previousBeatPos = beatPosition;

// after:
shuffledPos = this.applyShuffle(beatPosition);
waitBeats = (shuffledPos - previousShuffledPos) / speed;
// ...
previousShuffledPos = shuffledPos;
```

### Step 4: SequencerTrack passthrough
- [x]Add `shuffle` method that sets shuffle/shuffleGrid on all ParameterTracks
- [x]Follow same pattern as `speed_` setter (line 130-135)
- **File**: `Classes/Core/Sequencing/I8TSequencerTrack.sc`

```supercollider
shuffle {|amount, grid|
    parameterTracks.do({|t|
        t.shuffle = amount ? 0;
        if(grid.notNil) { t.shuffleGrid = grid };
    });
}
```

### Step 5: Sequenceable user API
- [x]Add `shuffle` method that delegates to SequencerTrack
- [x]Place near `speed` / `setClock` methods (line 316 area)
- **File**: `Classes/Core/Sequencing/I8TSequenceable.sc`

```supercollider
shuffle {|amount, grid|
    sequencer.sequencerTracks[name].shuffle(amount, grid);
}
```

### Step 6: Verify
- [x]Confirm `shuffle(0)` produces identical timing to current behavior
- [x]Confirm `shuffle(0.5)` on an 8th-note pattern produces audible triplet swing

## Depends on

- Current event-scheduled ParameterTrack architecture (Routine-based playback)

## Considerations

- Shuffle only affects timing, not note values or amplitudes
- The `remainingBeats` calculation at cycle end must also account for the
  last event's shuffled position to avoid early/late cycle wrapping
- When `speed` is active, the shuffle grid should be in pre-speed beats
  (shuffle is applied before the speed division)
