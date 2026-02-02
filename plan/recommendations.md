# INSTRUMENT — Audit & Assessment

## 1. SCHEDULING (The Main Problem)

This is your core concern, and you're right to be worried. The scheduling architecture has fundamental issues.

### The Problem: Language-Side Tick Loop

Your entire timing system runs in a `Tdef` with `(1/tickTime).wait` — that's `(1/500).wait` = 0.002 seconds per tick, running in the **SuperCollider language** (sclang), not on the audio server.

**Why this is bad:**

- **sclang is not real-time.** It runs in a cooperative multitasking environment with garbage collection pauses. Every GC cycle introduces jitter. Your comment at `I8TSequencer.sc:71` confirms you've already hit this: *"500 is a safe limit, higher times have resulted in seq inaccuracy."*
- **You're polling 500 times per beat** to check if events should fire. This is a busy-loop pattern. It burns CPU doing nothing useful 499 out of 500 iterations for beat-level events.
- **Beat calculation drifts.** At `I8TSequencer.sc:181`: `if( ticks % ((60/main.tempo)*tickTime) < 1 )` — this is a floating-point modulo comparison. Over time, rounding errors accumulate. Beats will skip or double-fire depending on tempo.
- **Tick overflow reset** at `I8TSequencer.sc:177-179`: when `ticks > maxTicks`, you reset to 0. This causes a discontinuity — any event tracking that depends on tick values will break at that moment (after 30 minutes at the current maxTicks).

### The Problem in ParameterTrack

`I8TParameterTrack.sc` compounds this. `getCurrentEventNew()` (line 591) computes `patternPosition` from `currentTick / main.sequencer.tickTime`, then uses `findNearest()` and `indexOfNearestIrregularIndex()` to locate the right event. This is:

1. **An O(n) search every tick** for every parameter of every instrument. With 5 instruments × 3 parameters × 500 ticks/beat = 7,500 linear searches per beat.
2. **Dependent on floating-point proximity** (`findNearest`), which means events near boundaries can be missed or triggered twice.
3. **The `played` flag system** (lines 610-628) is a fragile state machine. Resetting `played = false` for all events when `currentIndex == 0` assumes the sequence loops perfectly. If a tick is missed due to GC or CPU load, the reset logic can fail, causing events to not replay on the next cycle.

### What You Should Do Instead

SuperCollider has purpose-built scheduling that's far more reliable:

- **`TempoClock.sched` / `TempoClock.schedAbs`**: Schedule events at exact beat positions. No polling, no tick loop. The clock handles timing at the C++ level.
- **`Patterns` (Pbind, Pseq, Ppar, etc.)**: SC's pattern library does exactly what your entire ParameterTrack/Pattern/Parser system does, but with sample-accurate server-side timing via `s.bind {}`.
- **Server-side `SendReply` + `OSCFunc`**: For truly tight timing, compute durations server-side and send triggers back to sclang.

The rewrite path: Replace the tick loop with beat-scheduled callbacks. When a pattern is set, calculate the absolute beat times for each event and schedule them on the TempoClock. This eliminates the polling loop, the floating-point drift, and the O(n) searches.

---

## 2. ARCHITECTURE

### Class Hierarchy is Inverted/Confused

- `I8TMain : Event` — Main inherits from `Event` (a dictionary). This means your central orchestrator is also a key-value store. This is used so `i[\kick] = someSynthDef` works via `put`, but it mixes data storage with application control. Every `Event` method (play, stop, etc.) is available on `I8TMain`, creating confusion about which `play` is being called.

- `I8TMixer : Sequenceable` — The mixer inherits from Sequenceable, which means it has `seq`, `trigger`, `note`, `fx`, `play`, `stop`, etc. A mixer shouldn't be sequenceable. This was done for convenience (to get `setupSequencer`), but it's the wrong abstraction.

- `Sequenceable : I8TNode : I8TeventListener` — Deep inheritance chain where each layer adds responsibilities. `I8TNode` has `doesNotUnderstand` (line 115) which catches **any** unknown message and treats it as a parameter get/set. This is a debugging nightmare — typos will silently set parameters instead of throwing errors.

### God Object: I8TMain

`I8TMain.sc` is 1356 lines and does everything: node management, synth loading, MIDI auto-mapping, group creation/updating, mixer delegation, GUI updates, clear/restore state management. It should be split into smaller managers, but given this is a hobby project, the practical advice is: at minimum, extract the `put` method (lines 729-907) into a separate factory/registry class. That method alone is 180 lines of type-checking cascades.

### Classvar Coupling

`SequencerTrack.classSequencer` and `SequencerEvent.classSequencer` are set in `Sequencer.init()`. This means these classes are globally bound to a single sequencer instance. If you ever create two `I8TMain` instances (your `createNew` parameter suggests you intended to), the second one will stomp the classvar and break the first.

---

## 3. BUG-PRONE PATTERNS

### Nil-safety gaps everywhere

- `I8TSequencerTrack.sc:104`: `parameterTracks[parameter].removePattern(key)` — no nil check. If `parameter` doesn't exist, this crashes.
- `I8TSequencerTrack.sc:114`: `parameterTracks[parameter].clear` — same.
- `I8TSequencerTrack.sc:132`: `parameterTracks[parameter].setPatternParameters(...)` — same.
- `I8TSequencer.sc:386`: `sequencerTracks[instrument].play` — this is called right after a conditional that handles both `Sequenceable` and non-Sequenceable inputs, but it always indexes by `instrument`, not `instrument.name`. For Sequenceable objects, the track is stored under `instrument.name`, so this line will be nil and crash for Sequenceable inputs.

### Mutable shared state

- `I8TParser.getEventsList` (line 615-637): The repetition expansion loop does `var newEvent = event` — this is **not a copy**. Every repetition shares the same Event object. Mutating one mutates all of them. This is almost certainly causing bugs where repeated events behave unexpectedly.

### `collect` used for side effects

Throughout the codebase, `.collect` is used where `.do` should be used. `collect` builds and returns a new collection (wasting memory); `do` iterates without building a return value. Examples: `I8TSequencer.sc:117`, `I8TSequencer.sc:146`, `I8TSequencer.sc:170`, `I8TParameterTrack.sc:334`, `I8TParameterTrack.sc:348`, and dozens more. This is a pervasive habit.

### `== nil` vs `.isNil`

Inconsistent nil checking throughout. In SC, `== nil` and `.isNil` do different things in edge cases. Use `.isNil` / `.notNil` consistently.

### `== true` / `== false`

Redundant boolean comparisons everywhere (`if( playing == true )`). In SC, `if(playing, { ... })` is idiomatic. The `== true` form can behave unexpectedly if the value is a truthy non-Boolean.

---

## 4. PERFORMANCE

### Parser is rebuilt every time

`I8TPattern.init` calls `I8TParser.parse()` which does heavy string processing. If you call `.seq(\trigger, "1 0 1 0")` repeatedly with the same string, it re-parses every time. Cache parsed patterns by their input string.

### `updateSequenceInfo` rebuilds everything

Every time a pattern is added, `updateSequenceInfo` in `ParameterTrack` rebuilds the entire `newSequenceInfo` Order from scratch (lines 450-529). This is O(n*m) where n = patterns and m = events per pattern. It should only update the changed portion.

### Linear search in `removePatternEvents`

`I8TParameterTrack.sc:389`: `sequence.reverseDo` to find an event, then `removeAt` (which is O(n) on a List). This is O(n²) per removal.

### Synth creation overhead

`I8TSynthPlayer.createSynth` (line 120) iterates all existing synths with `.collect` to find dead ones, every single time a new note is triggered. For polyphonic instruments playing fast sequences, this adds up.

---

## 5. DEAD CODE & CRUFT

- `I8TPattern.parseEventString` (lines 133-220): Entire old parser still present in the class, replaced by `I8TParser`. Dead code.
- `I8TSequencer.sc:36`: `var <tdef;` declared twice (line 15 and 36).
- Massive commented-out blocks throughout (I8TSynthInstrument.sc lines 59-110, I8TParameterTrack.sc lines 92-102, etc.)
- `I8TSequencer2.sc` — the V2 code is mostly commented out and the actual logic is just the queue system.
- `I8TSequencerTrack.sc:134-137`: Debug `postln` left in production code.
- `I8TP.sc` exists as a 3-line alias. Unnecessary file.
- `Sequenceable.pause` (line 238) sets `playing = true` — this is a copy-paste bug. Should be `false`.

---

## 6. STRATEGY RECOMMENDATIONS

Given this is a 1-dev hobby project:

1. **Fix scheduling first.** Replace the tick loop with `TempoClock.schedAbs`. This is the single change that would most improve reliability. You don't need to rewrite everything — just change how events get dispatched.

2. **Don't try to rewrite the pattern DSL.** Your parser is complex but works. Leave it. Just make it cache results.

3. **Fix the `collect` → `do` issue** project-wide. It's a quick find-replace that reduces GC pressure (which directly helps timing).

4. **Delete dead code.** The commented-out blocks make the codebase harder to read than it needs to be. You have git history if you need them back.

5. **The `doesNotUnderstand` pattern in I8TNode is dangerous.** It silently swallows errors. Consider removing it and adding explicit setter/getter methods for the parameters you actually use.

6. **Fix `Sequenceable.pause`** — it says `playing = true` when it should say `false`. Line 240.

---

## 7. PARSER ASSESSMENT (I8TParser.sc)

The DSL design is good for live coding — operators for duration (`:``), repetition (`x`), dynamics (`p`/`f`), probability (`?`), alternation (`|`), release (`<`/`>`), and subsequences with parentheses. Non-trivial feature set.

The implementation has problems:

### It's a hand-rolled character-by-character parser that should be a tokenizer + parser

The `*parse` method (line 19) is ~220 lines of nested `if` statements tracking `lastChar` and `buildingGroupChars`. This is a state machine written without naming the states. Identical logic (space handling, last-character handling) is duplicated across multiple branches, making it hard to reason about correctness.

### Operator extraction is fragile

`extractParameters` (line 265) finds operators by scanning for single characters (`$p`, `$f`, `$x`, etc.), but these characters can also appear in values. The method tries to disambiguate by checking `operatorIndexes` and reading characters after operators, but the logic for "is this character an operator or part of a value" is spread across multiple methods and relies on ordering assumptions. Ambiguous inputs (e.g. `"fp3"`) have undefined behavior.

### Subsequence handling doesn't nest

`validateMatching` (line 740) and `getSubsequences` (line 779) explicitly reject nested brackets — `opening[index+1] < closing[index]` fails. The TODO on line 738 acknowledges this. So `(1 2 (3 4))` would break.

### Repetition expansion has a shared-reference bug

In `getEventsList` line 626, `var newEvent = event` doesn't copy the Event. Every "repeated" event is the same object in memory. If anything downstream mutates one, all repetitions change. This is a real bug that likely causes unexpected behavior with repeated events.

### `applyAmplitudeModifiers` is confused

It computes `ampRange` from max piano/forte values across all events, then normalizes each event's amplitude relative to that range. But it converts numbers to strings and back for no reason (`ampRange[0].asString.asInteger` at lines 695-696, 720-721). Lines 720-721 also compute the identical expression twice consecutively — copy-paste artifact.

### Recommendation

Keep the DSL syntax — it works well for live coding. Rewrite the parser as a proper two-pass system: (1) tokenize the string into tokens (value, operator, space, open-paren, close-paren), then (2) walk the token list to build events. This would roughly halve the code, enable nesting, improve error messages, and make adding new operators straightforward. The current approach of scanning for character positions and retroactively determining their meaning is what makes it brittle.
