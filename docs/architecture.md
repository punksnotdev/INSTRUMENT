# INSTRUMENT — System Architecture

## Overview

INSTRUMENT is a SuperCollider live coding framework for electronic music performance.
It provides a high-level API for creating instruments, sequencing patterns, mixing audio,
and mapping MIDI controllers — all designed for real-time improvisation.

The system is built on SuperCollider's client/server architecture:
- **sclang** (language): Pattern scheduling, UI, MIDI, OSC routing
- **scsynth** (server): Real-time audio synthesis and DSP

---

## Entry Point

```
INSTRUMENT() or I8T()
  -> I8TMain.new(server)
```

`I8TMain` is the singleton controller. `INSTRUMENT` and `I8T` are convenience aliases.
Multiple instances are possible with `I8TMain.new(server, createNew: true)`.

---

## Class Hierarchy

```
I8TeventListener
  └── I8TNode (base for all nodes: has name, parameters, main ref)
        ├── Sequenceable (adds seq/rm/play/stop/pattern API)
        │     ├── I8TInstrument (adds group, synth, channel, outbus)
        │     │     ├── I8TSynthInstrument (adds synthdef, fxSynth, fxBus)
        │     │     │     └── I8TSynthPlayer (full poly/mono synth engine)
        │     │     └── Proxy (wraps NodeProxy)
        │     ├── InstrumentGroup (container for multiple instruments)
        │     ├── I8TSynth (low-level synth wrapper)
        │     ├── I8TChannel (mixer channel with EQ/comp/locut)
        │     ├── I8TChannelGroup (container for channels)
        │     └── I8TMixer (master mixer)
        └── Sequencer (clock callbacks, tracks, queue, loopers)

I8Tevent (event data with action callback)
I8TPattern / P (pattern data: parsed string -> Events)
PatternEvent : SequencerEvent (pattern wrapper with speed/repeat/transforms)
SequencerTrack (per-instrument: holds ParameterTracks)
ParameterTrack (per-parameter: Routine-based event scheduling, triggers instrument)
I8TParser (string pattern -> event array)
I8TFXChain : Event (dictionary of FX synths on a channel)
I8TFolder : Event (hierarchical SynthDef organization)
I8TSynthLoader (loads SynthDefs from disk)
I8TChord / C (chord representation with inversions)
I8THarmony (voice-leading generation)
```

---

## Core Systems

### 1. Instrument Creation

```supercollider
i = I8T();
i[\bass] = \synthDefName;   // creates I8TSynthPlayer
i[\drums] = [\kick, \snare]; // creates InstrumentGroup
```

`I8TMain.put(key, something)` routes to `setupNode()`:
- `SynthDef` / `SynthDefVariant` -> creates `I8TSynthPlayer`
- `Array` -> creates `InstrumentGroup` (recursively)
- `NodeProxy` -> wraps as `Proxy`

Each instrument is automatically:
1. Registered with the Sequencer (creates a `SequencerTrack`)
2. Given a mixer channel (`I8TChannel`)
3. Placed in the server's `ParGroup` for parallel execution

### 2. Sequencing

See [docs/sequencing.md](sequencing.md) for the full sequencing architecture.

**Timing**: Event-scheduled Routines with `server.makeBundle` for sub-millisecond precision (~0.2-1ms).
See [docs/sequencing.md](sequencing.md) for the full timing architecture.

**User API:**
```supercollider
i[\bass].note("60 62 64 67");        // sequence notes
i[\bass].trigger("1 0.5 1 0.5");     // sequence triggers with amplitude
i[\bass].seq(\note, "C4 E4 G4");     // explicit parameter
i[\bass].note.seq("60 62 64");       // parameter-proxy syntax
i[\bass].note.clear();                // clear note parameter patterns
i[\bass].note.reset();                // clear + restore default when available
i[\bass].rm(\note);                   // remove pattern
i[\bass].note("60 62 64").x(4);      // repeat 4 times
i[\bass].note("60:0.5 62:1.5");      // explicit durations
```

**Call chain:**
```
Sequenceable.seq(parameter, pattern)
  -> Sequencer.addPattern(instrumentName, parameter, key, pattern)
    -> SequencerTrack.addPattern(parameter, key, pattern)
      -> ParameterTrack.addPattern(key, pattern)
        -> I8TParser.parse(patternString) -> I8TPattern
        -> PatternEvent wraps pattern with speed/repeat
        -> updateSequenceInfo() builds Order timeline
```

**Pattern string syntax** (parsed by I8TParser):
- `"60 62 64"` — space-separated values (notes, triggers, params)
- `"60:0.5 62:1.5"` — value:duration
- `"60*0.8"` — value*amplitude
- `"60ff"` — forte (louder), `"60pp"` — piano (softer)
- `"60x3"` — repeat 3 times
- `"60?"` — maybe (50% probability)
- `"60|62"` — or (random choice)
- `"(60 62 64):2"` — subsequence with shared duration
- `" "` (space) — rest

### 3. Mixer & Audio Routing

```
Synth output
  -> fxBus [instrument FX chain]
    -> I8TChannel.inbus
      -> inSynth (input routing)
      -> [EQ] -> [Compressor] -> [Locut]  (toggleable)
      -> I8TChannel.bus (stereo)
      -> outSynth
        -> Master bus
          -> Master channels (L/R)
            -> Server output
```

**I8TMixer** manages:
- `channels`: One `I8TChannel` per instrument (auto-created)
- `channelGroups`: Named groups of channels
- `master`: Stereo master output (2 channels)
- `masterFx`: Master effects chain
- Node groups for ordering: `mixNodeGroup` -> `groupsNodeGroup` -> `fxNodeGroup` -> `masterGroup`

**I8TChannel** provides per-instrument:
- Amplitude and pan control
- 3-band EQ (low/middle/high)
- Compressor
- Low-cut filter
- FX chain (`I8TFXChain`)
- Send routing to other channels
- Source mixing (multiple instruments can feed one channel)

**Channel sends:**
```supercollider
i[\bass].send(i[\reverb_channel]);  // send bass to reverb
i[\bass].connect(i[\sidechain]);     // connect output
```

### 4. Tempo & Clock

`I8TMain` creates a `TempoClock` at init:
```supercollider
clock = TempoClock.new(TempoClock.default.tempo);
```

TempoClock `.tempo` is in **beats per second** (not BPM).

`main.tempo` returns BPM: `clock.tempo * 120`
`main.tempo_(bpm)` sets: `clock.tempo = bpm / 120`

So at 120 BPM, `clock.tempo = 1.0` (1 beat/sec).
At 130 BPM, `clock.tempo = 1.0833...`

All Routines and scheduled events use `main.clock` for synchronized timing.

### 5. SynthDef Loading

`I8TSynthLoader` recursively loads `.scd` files from a path into an `I8TFolder` hierarchy:

```
synths/
  drums/
    kick.scd      -> synths[\drums][\kick]
    snare.scd     -> synths[\drums][\snare]
  bass/
    sub.scd       -> synths[\bass][\sub]
```

Folders create simplified references up the hierarchy, so `synths[\kick]` works
from the root level. `SynthDefVariant` extends a SynthDef with parameter presets.

### 6. MIDI Controllers

```supercollider
i.map(controller, target, parameter, range);
```

`I8TControllerManager` handles:
- `I8TMIDIController`: CC/note mapping to instrument parameters
- `I8TMIDIDevice`: MIDI device abstraction
- `ModeMatrix`: Multi-mode controller layouts (e.g., Launchpad pages)

### 7. Scheduled Functions

```supercollider
i.every(4, { "every 4 beats".postln });        // repeat every N beats
i.every(4, { "offset 2".postln }, offset: 2);   // with beat offset
i.when(16, { "at beat 16".postln });             // fire once at beat N
```

These are stored in `Sequencer.repeatFunctions` and `Sequencer.singleFunctions`
and executed at beat boundaries.

### 8. Music Theory

- `I8TChord` / `C`: Chord construction with type, inversion, added intervals
  ```supercollider
  C(60, \M7);        // C major 7th
  C(60, \m, 1);      // C minor, 1st inversion
  C(60, [0,4,7,11]); // Custom intervals
  ```
- `I8THarmony`: Voice-leading generation avoiding parallel 5ths/octaves

---

## File Map

```
Classes/
  Core/
    I8TMain.sc              Main singleton controller
    INSTRUMENT.sc / I8T.sc  Entry point aliases
    I8TNode.sc              Base node class
    I8Tevent.sc             Event system
    I8TeventListener.sc     Event listener base
    I8TGUI.sc               Basic GUI
    Instruments/
      I8TProxy.sc           NodeProxy wrapper
    Music/
      I8TChord.sc / C.sc    Chord representation
    SCextensions/
      SynthDef.sc           SynthDef.at() for variants
      SynthDefVariant.sc    Parameter preset variants
      String.sc             capitalize/uncapitalize
      IdentityDictionaryFindKey.sc  Reverse key lookup
      SequenceableCollectionFindNearest.sc  Binary search
    Sequencing/
      I8TSequencer.sc       Main sequencer
      v2/I8TSequencer2.sc   Queue system extension
      I8TSequencerTrack.sc  Per-instrument track
      I8TParameterTrack.sc  Per-parameter scheduling
      I8TSequenceable.sc    Sequencing mixin (seq/rm API)
      I8TInstrument.sc      Base instrument
      I8TSynthInstrument.sc Synth-based instrument
      I8TSynthPlayer.sc     Full synth player (poly/mono)
      I8TSynth.sc           Low-level synth wrapper
      I8TInstrumentGroup.sc Instrument container
      I8TPattern.sc / I8TP.sc  Pattern data
      I8TPatternEvent.sc    Pattern wrapper with transforms
      I8TParser.sc          String pattern parser
      I8TSequencerEvent.sc  Base sequencer event
      I8TSequenceEvent.sc   Sequence event
  Mixer/
    I8TMixer.sc             Master mixer
    I8TChannel.sc           Per-instrument channel
    I8TChannelGroup.sc      Channel container
    I8TFXChain.sc           FX chain dictionary
  Harmony/
    I8THarmony.sc           Voice-leading
  Utilities/
    I8TSynthLoader.sc       SynthDef loader
    I8TFolder.sc            Hierarchical SynthDef organization
    I8TCountDown.sc         Timer utility
    ModeMatrix.sc           MIDI mode manager
  Controllers/
    I8TControllerManager.sc MIDI controller manager
    I8TMIDIController.sc    MIDI CC/note mapping
    I8TMIDIDevice.sc        MIDI device abstraction
    I8TDevice.sc            Generic device
    I8TInstrumentController.sc  Instrument-controller bridge
    I8TControlMode.sc       Controller mode definition
    I8TMIDIManager.sc       MIDI system manager
    I8TMIDIControllerTarget.sc  MIDI target mapping
    Specs/
      I8TControllerSpec.sc  Controller specification
Extensions/
  Looper/
    I8TLooper.sc            Loop recording/playback
```

---

## Design Patterns

1. **Singleton with override**: `I8TMain` is singleton by default, `createNew: true` for multi-instance
2. **Dynamic dispatch**: `I8TNode.doesNotUnderstand()` enables `instrument.paramName_(value)` syntax
3. **Hierarchical references**: `I8TFolder` propagates refs up so `synths[\kick]` works from root
4. **Event-based triggering**: All sequencing flows through `instrument.trigger(parameter, event)`
5. **Dictionary accessor**: `InstrumentGroup.doesNotUnderstand()` and `I8TChannelGroup.doesNotUnderstand()` provide named access to children (e.g. `group.kick` returns child instrument). Broadcasting to children is handled by explicit methods (`set`, `amp_`, `octave_`, etc.), not by doesNotUnderstand
6. **Bus-based routing**: All audio through buses for flexible FX/send/mix chains
7. **String DSL**: Pattern strings (`"60:0.5*0.8ff"`) parsed by I8TParser into event arrays
