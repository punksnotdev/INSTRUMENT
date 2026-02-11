# INSTRUMENT — Project Progress

## Active Work

### Sequencer Timing Overhaul
- **Plan**: [SEQFIX.md](../SEQFIX.md)
- **Branch**: `audit`
- **Status**: Plan complete, implementation pending
- **Goal**: Replace 500Hz tick-loop polling with event-scheduled Routines +
  server.latency bundling for sample-accurate timing
- **Phases**: (1) .collect->.do hygiene [partial], (2) event-scheduled ParameterTrack,
  (3) simplify Sequencer, (4) cleanup

---

## Decision Log

Decisions, tradeoffs, and observations recorded during development.
Append new entries at the bottom with date headers.

### 2026-02-11 — Sequencer timing audit

**Context**: Live performance system needs sample-accurate or near-sample-accurate
timing. Current implementation uses a 500Hz Tdef polling loop with 2-7ms jitter.

**Decisions:**

1. **Chose event-scheduled Routines + server.latency** over:
   - Increasing tick rate (optimizing a dead end)
   - Skip-early optimization (reduces CPU but doesn't fix quantization)
   - Server-side Demand UGens (sample-accurate but loses live-coding flexibility)

2. **server.makeBundle wrapping goes in ParameterTrack**, not per-instrument type.
   ParameterTrack calls `main.server.makeBundle(latency, { instrument.trigger() })`.
   This way any instrument type (I8TSynthPlayer, InstrumentGroup, Proxy, etc.)
   gets sample-accurate timing without needing a `triggerBundled` method each.

3. **server.latency = 0.05** (50ms) as starting point. Increase to 0.1-0.2 if
   sclang jitter exceeds this under heavy load. Lower values reduce perceived
   input lag for live playing.

4. **Phase 1 hygiene (.collect -> .do)** applied partially to I8TSequencer.sc
   before the main refactor as a safety net and to reduce GC pressure.

**Findings:**

- `main.tempo` returns `clock.tempo * 120` (BPM), not beats-per-second.
  At 120 BPM, clock.tempo = 1.0. At 130 BPM, clock.tempo = 1.0833.
  The beat detection formula `ticks % ((60/main.tempo)*tickTime)` produces
  irrational numbers at non-round tempos, causing beat drift.

- The binary search in `SequenceableCollectionFindNearest.sc` is O(log n),
  not O(n) as initially suspected. Still unnecessary once polling is removed.

- `.collect` used where `.do` suffices creates ~3000 throwaway arrays/second
  at 500Hz tick rate, increasing GC pressure and jitter spikes.

- `server.bind` (currently used) timestamps OSC bundles at execution time.
  `server.makeBundle(latency, ...)` timestamps them in the future — the key
  to decoupling sclang scheduling jitter from synth creation precision.

- `beats % 1 == 0` (line 115) is always true since beats is integer.
  `ticks % tickTime == 0` (line 141) fires once per second, which gates
  singleFunctions/repeatFunctions in a way that may not match intent.

---

## Backlog

Issues identified during the 2026-02-11 audit that are NOT part of the sequencer
timing refactor. These should be addressed separately.

### Bugs

#### `Sequenceable.pause` sets `playing = true` (should be `false`)
- **File**: `I8TSequenceable.sc:240`
- **Severity**: Real bug — pausing doesn't actually pause
- `pause` method sets `playing = true` instead of `false`. Copy-paste from `play`.

#### Parser event copy bug — repeated events share same object
- **File**: `I8TParser.sc`, `getEventsList` line 626
- **Severity**: Real bug — causes unexpected mutation of repeated events
- `var newEvent = event` does NOT copy the Event. Every "repeated" event is the
  same object in memory. If anything downstream mutates one (e.g., setting `played = true`),
  all repetitions change.

#### Nil-safety gaps in SequencerTrack
- **File**: `I8TSequencerTrack.sc`
- Lines 104, 114, 132: `parameterTracks[parameter].removePattern(key)`,
  `.clear`, `.setPatternParameters(...)` — no nil check on `parameterTracks[parameter]`.
  Crashes if parameter doesn't exist.

#### Sequencer.createTrack indexes by wrong key for Sequenceable inputs
- **File**: `I8TSequencer.sc:386`
- `sequencerTracks[instrument].play` — for Sequenceable objects, the track is stored
  under `instrument.name`, but this line indexes by `instrument` (the object itself).
  Will be nil for Sequenceable inputs.

### Architecture Issues

#### I8TMain : Event — confused inheritance
- `I8TMain` inherits from `Event` (a dictionary) so `i[\kick] = synthDef` works via `put`.
  But this means the central orchestrator is also a key-value store. Every Event method
  (play, stop, etc.) is available on I8TMain, creating ambiguity about which `play` is called.

#### I8TMixer : Sequenceable — wrong abstraction
- The mixer inherits from Sequenceable to get `setupSequencer`, but this gives it
  `seq`, `trigger`, `note`, `fx`, `play`, `stop`, etc. A mixer shouldn't be sequenceable.

#### `doesNotUnderstand` in I8TNode swallows errors
- **File**: `I8TNode.sc:115`
- Catches **any** unknown message and treats it as a parameter get/set.
  Typos silently set parameters instead of throwing errors. Debugging nightmare.

#### Classvar coupling prevents multi-instance
- `SequencerTrack.classSequencer` and `SequencerEvent.classSequencer` are classvars
  set in `Sequencer.init()`. A second `I8TMain` instance would stomp the first's
  sequencer reference. The `createNew` parameter suggests multi-instance was intended.

### Performance

#### Parser doesn't cache results
- `I8TPattern.init` calls `I8TParser.parse()` every time. Calling
  `.seq(\trigger, "1 0 1 0")` repeatedly with the same string re-parses every time.
  Cache parsed patterns by input string.

#### `removePatternEvents` is O(n^2)
- **File**: `I8TParameterTrack.sc:389`
- `sequence.reverseDo` to find an event (O(n)), then `removeAt` (O(n) on a List).
  O(n^2) per removal.

#### `createSynth` scans all synths on every note
- **File**: `I8TSynthPlayer.sc:120`
- Iterates all existing synths with `.collect` to find dead ones, every time a new
  note is triggered. For fast polyphonic sequences, this adds up.

#### `updateSequenceInfo` rebuilds everything on every pattern change
- **File**: `I8TParameterTrack.sc:450-529`
- Rebuilds the entire `newSequenceInfo` Order from scratch. O(n*m) where
  n = patterns and m = events per pattern. Should only update the changed portion.

### Dead Code & Cruft

#### `I8TPattern.parseEventString` — dead old parser
- **File**: `I8TPattern.sc:133-220`
- Entire old parser still present, replaced by `I8TParser`. Delete it.

#### Double `var <tdef` declaration
- **File**: `I8TSequencer.sc` lines 15 and 36 — `tdef` declared twice.

#### Commented-out blocks throughout
- Large commented-out sections in: `I8TSynthInstrument.sc:59-110`,
  `I8TParameterTrack.sc:92-102`, `I8TSequencer2.sc` (most of the v2 code).
  Git history preserves them; delete for readability.

#### Debug postln left in production
- **File**: `I8TSequencerTrack.sc:134-137` — `["key,play_parameters", key,play_parameters].postln`

### Parser Issues (non-blocking, future improvement)

#### Parser should be tokenizer + parser (two-pass)
- Current implementation is a hand-rolled character-by-character state machine
  (~220 lines of nested `if`). Should be: (1) tokenize string into tokens
  (value, operator, space, paren), then (2) walk tokens to build events.
  Would roughly halve the code and enable nesting.

#### Subsequence handling doesn't nest
- `validateMatching` and `getSubsequences` explicitly reject nested brackets.
  `(1 2 (3 4))` breaks. TODO acknowledged in code at line 738.

#### Operator extraction is fragile
- `extractParameters` finds operators by scanning for single characters (`$p`, `$f`, etc.)
  but these can appear in values. Ambiguous inputs (e.g. `"fp3"`) have undefined behavior.

#### `applyAmplitudeModifiers` has redundant string conversion
- Converts numbers to strings and back for no reason (`ampRange[0].asString.asInteger`).
  Lines 720-721 compute identical expression twice — copy-paste artifact.

### Code Style (low priority, do opportunistically)

#### `== nil` vs `.isNil` — inconsistent
- Mixed throughout. In SC, use `.isNil` / `.notNil` consistently.

#### `== true` / `== false` — redundant
- `if( playing == true )` throughout. Idiomatic SC: `if(playing, { ... })`.

#### `.collect` used for side effects — project-wide
- Beyond the sequencer tick loop (fixed in SEQFIX Phase 1), `.collect` is used
  where `.do` should be used throughout the codebase. Examples:
  `I8TParameterTrack.sc:334,348`, `I8TSequencerTrack.sc:59,70,77,143,149`,
  and dozens more. Quick find-replace opportunity.

---

## Completed

### 2026-02-11 — Audit documentation
- Wrote initial audit docs (commit `ecd7bfc`)
- Created SEQFIX.md with full implementation plan (commit `655faec`)
- Applied partial Phase 1 .collect->.do fix in I8TSequencer.sc
- Created docs/architecture.md, docs/sequencing.md, docs/progress.md
