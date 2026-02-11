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

## Completed

### 2026-02-11 — Audit documentation
- Wrote initial audit docs (commit `ecd7bfc`)
- Created SEQFIX.md with full implementation plan (commit `655faec`)
- Applied partial Phase 1 .collect->.do fix in I8TSequencer.sc
- Created docs/architecture.md, docs/sequencing.md, docs/progress.md
