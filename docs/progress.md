# INSTRUMENT — Project Progress

## Active Work

### Sequencer Timing Overhaul
- **Plan**: [SEQFIX.md](../SEQFIX.md)
- **Branch**: `audit`
- **Status**: Complete
- **Goal**: Replace 500Hz tick-loop polling with event-scheduled Routines +
  server.latency bundling for sub-millisecond timing precision
- **Phases**:
  - [x] Phase 1a: `.collect` -> `.do` hygiene (11 call sites)
  - [x] Phase 1b: Remove cosmetic `Task` in `addPattern`
  - [x] Phase 2: Event-scheduled ParameterTrack with Routine + `server.makeBundle`
  - [x] Phase 3+4: Remove tick loop, replace with barRoutine/beatRoutine, clean up dead code
- **Result**: Zero polling, zero binary searches, three-layer timing precision
  (TempoClock ~0.2-1ms -> server.latency absorption -> scsynth execution)
- **Tests**: See [TESTSEQ.md](../TESTSEQ.md) and `Tests/Core/Sequencing/TestSequencerTiming.scd`

---

## Decision Log

Decisions, tradeoffs, and observations recorded during development.
Append new entries at the bottom with date headers.

### 2026-02-11 — Sequencer timing audit

**Context**: Live performance system needs sub-millisecond timing precision. Current implementation uses a 500Hz Tdef polling loop with 2-7ms jitter.

**Decisions:**

1. **Chose event-scheduled Routines + server.latency** over:
   - Increasing tick rate (optimizing a dead end)
   - Skip-early optimization (reduces CPU but doesn't fix quantization)
   - Server-side Demand UGens (sample-accurate but loses live-coding flexibility)

2. **server.makeBundle wrapping goes in ParameterTrack**, not per-instrument type.
   ParameterTrack calls `main.server.makeBundle(latency, { instrument.trigger() })`.
   This way any instrument type (I8TSynthPlayer, InstrumentGroup, Proxy, etc.)
   gets sub-millisecond timing without needing a `triggerBundled` method each.

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

#### Double `var <tdef` declaration — RESOLVED
- **File**: `I8TSequencer.sc` — `tdef` removed entirely in timing overhaul.

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

#### Subsequence handling doesn't nest — RESOLVED
- Fixed in commit `a060a84`. `validateMatching` now uses depth-counting,
  `getSubsequences` finds top-level pairs only, and parsing recurses into inner groups.
  `(1 2 (3 4))` now works correctly.

#### Operator extraction is fragile
- `extractParameters` finds operators by scanning for single characters (`$p`, `$f`, etc.)
  but these can appear in values. Ambiguous inputs (e.g. `"fp3"`) have undefined behavior.

#### `applyAmplitudeModifiers` has redundant string conversion
- Converts numbers to strings and back for no reason (`ampRange[0].asString.asInteger`).
  Lines 720-721 compute identical expression twice — copy-paste artifact.

### Queue System Bug

#### `queueDo` removes wrong item when queue has mixed action types
- **File**: `v2/I8TSequencer2.sc:55-78`
- `queue.select({|q| q.action==action}).do({` filters correctly, but then
  `queue.removeAt(0)` always removes the first item in the full queue — not the
  first item matching the action. If the queue has `[\stop, \play, \stop]` and
  `queueDo(\stop)` is called, it removes index 0 (the first `\stop`), then on
  the next iteration removes index 0 again (now `\play`), executing a `\play`
  as if it were a `\stop`. Fix: use `queue.remove(q)` instead of `queue.removeAt(0)`,
  or collect the filtered items first, execute them, then remove them from the queue.

### Sequencer Refactor Risks

#### Looper state machine depends on beat-boundary checks
- The looper state transitions (awaitingRec -> recording, etc.) currently happen
  inside the tick loop's beat-boundary check. After the SEQFIX refactor, these
  move to the bar-boundary callback. Verify that looper recording start/stop
  still quantizes correctly — if a user calls `recLooper` mid-bar, the state
  should hold until the next bar boundary fires `processLoopers`.

#### `calculateSyncOffset` may be unnecessary after refactor
- **File**: `I8TParameterTrack.sc:653-662`
- Currently adjusts beat position when hot-swapping patterns to maintain continuity.
  With the new Routine-based approach using `quant`, the new pattern starts on the
  next bar boundary regardless. The sync offset logic may be dead code after the
  refactor, or it may need rethinking if mid-bar pattern transitions are desired.

#### InstrumentGroup.trigger dispatches to all children
- When a ParameterTrack triggers an InstrumentGroup, the group's `trigger` calls
  `trigger` on each child. If the ParameterTrack wraps this in `server.makeBundle`,
  all children's OSC messages should be captured in the same bundle. Verify this
  works — if any child instrument does async work (e.g., scheduling its own
  deferred actions), those won't be captured by the bundle.

### Mixer & Audio

#### Per-instrument DSP modules are separate synths
- Each I8TChannel creates up to 4 separate synths: `inSynth`, EQ, compressor, locut,
  plus `outSynth`. That's 5 synths per channel just for routing and basic processing.
  With 8 instruments, that's 40 synths before any actual sound generation. Each synth
  adds one block of latency (64 samples = ~1.5ms at 44.1kHz). Consider consolidating
  the channel strip into a single SynthDef with toggleable sections for lower latency
  and reduced node overhead.

#### No server.latency set currently — RESOLVED
- **File**: `I8TMain.sc`
- `server.latency = 0.05` now set in init. Added as part of the timing overhaul.

### Reliability

#### No test infrastructure
- There are zero automated tests. For a live performance system, even minimal smoke
  tests would catch regressions:
  - Pattern parsing: verify `I8TParser.parse("60:0.5 r 62ff")` returns expected Events
  - Sequence timeline: verify `updateSequenceInfo` produces correct Order for known inputs
  - Tempo math: verify `main.tempo` / `main.tempo_` round-trip correctly at edge tempos
  - Chord construction: verify `C(60, \M7).chord` returns correct intervals
  SuperCollider has `UnitTest` built in. A `Tests/` directory with basic coverage
  would be valuable before the sequencer refactor (to verify behavior is preserved).

#### No error recovery in live performance
- If a Routine throws an error (e.g., nil event in sequence), it dies silently.
  The instrument stops sequencing with no feedback. Consider wrapping the Routine
  body in `try { ... } { |error| error.postln; 1.wait }` so a bad event skips
  rather than killing the whole track. Especially important for live coding where
  typos in pattern strings are common.

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

## Recommendations (prioritized)

1. **Fix `Sequenceable.pause` bug** — one-line fix, real impact, do immediately
2. **Fix `queueDo` removal bug** — can cause wrong actions to fire at bar boundaries
3. **Set `server.latency = 0.05`** in I8TMain.sc — quick win even before SEQFIX
4. **Implement SEQFIX** — the main timing overhaul (see [SEQFIX.md](../SEQFIX.md))
5. **Fix parser event copy bug** — `event.copy` instead of `event`, prevents mutation bugs
6. **Add basic UnitTests** — pattern parsing, timeline construction, tempo math
7. **Add error recovery to Routines** — `try` wrapper so bad events skip, not kill
8. **Delete dead code** — old parser, double tdef var, commented blocks, debug postln
9. **Fix nil-safety gaps** — add guards in SequencerTrack methods
10. **Parser caching** — cache by input string to avoid re-parsing identical patterns
11. **Consolidate channel strip synths** — reduce per-channel latency and node count
12. **Address architecture issues** — longer-term: I8TMain extraction, doesNotUnderstand

---

## Completed

### 2026-02-12 — Shuffle (swing) feature
- Runtime shuffle in `ParameterTrack.play` shifts off-grid events later in time (`7f1aec6`)
- Configurable amount (0–1) and grid resolution (default 0.5 = 8th-note swing)
- Live-tweakable: change takes effect on next event, no pattern rebuild needed
- User API: `i.kick.shuffle(0.3)`, `i.hihat.shuffle(0.5, 0.25)` (16th-note grid)
- Works on individual tracks and through SequencerTrack passthrough
- Added `TestSequencerTiming.scd` test script and `docs/features/shuffle.md`

### 2026-02-12 — Pattern preview visualization PRD
- Added product requirements doc for pattern preview visualization (`0e553dc`)
- Defines visual representation of patterns for live-coding feedback
- See `docs/features/pattern-preview.md`

### 2026-02-12 — Pattern variables PRD
- Added product requirements doc for pattern variables feature (`8a4425d`)
- Defines syntax for naming and reusing pattern fragments within pattern strings
- Using `;` delimiters, `name=value` assignments, and `=pattern` evaluation blocks
- See `docs/features/pattern-variables.md`

### 2026-02-12 — Nested parentheses support in parser
- Rewrote `validateMatching` to use depth-counting instead of position-matching (`a060a84`)
- Fixed `getSubsequences` to find only top-level bracket pairs
- Added recursive parsing so inner bracketed groups are correctly handled
- Duration modifier state now propagates across subsequences and recursion levels
- Resolves the "subsequence handling doesn't nest" issue from the backlog

### 2026-02-12 — Euclidean rhythm operator (%)
- Implements Bjorklund algorithm for distributing hits evenly across slots (`3f6778f`)
- Syntax: `value%hits/steps` (e.g. `"1%3/8"`) and `(values)%hits/steps` for subsequences
- Values inside subsequences cycle across hit positions
- Added to I8TParser.sc and documented in README.md

### 2026-02-11 — Sequencer timing overhaul
- Phase 1a: `.collect` -> `.do` in 11 call sites across SequencerTrack and ParameterTrack (`5fff76a`)
- Phase 1b: Remove cosmetic `Task.new` in `addPattern` (`be13620`)
- Phase 2: Event-scheduled ParameterTrack with Routine + `server.makeBundle` (`1f04566`)
- Phase 3+4: Remove tick loop, add barRoutine/beatRoutine, clean up dead code (`fe59381`)
- Added `server.latency = 0.05` in I8TMain.sc
- Updated unit tests and created timing measurement test scripts
- Updated docs/sequencing.md, docs/progress.md, README.md

### 2026-02-11 — Audit documentation
- Wrote initial audit docs (commit `ecd7bfc`)
- Created SEQFIX.md with full implementation plan (commit `655faec`)
- Applied partial Phase 1 .collect->.do fix in I8TSequencer.sc
- Created docs/architecture.md, docs/sequencing.md, docs/progress.md
