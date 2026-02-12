# Feature: Pattern Preview (Visual Pattern Display)

## Summary

Display a visual representation of parsed pattern data when a user evaluates, hovers over, or explicitly requests a preview of a pattern expression. Melodic patterns render as a piano roll; rhythmic/trigger patterns render as a step grid; other parameters render as value lanes.

---

## Motivation

INSTRUMENT's pattern DSL is powerful but opaque -- expressions like `"(0 3 :2/5 (7 9 14 10)%7/8)"` produce complex timing and pitch sequences that are hard to mentally parse. A visual preview closes the feedback loop: you see what you wrote, instantly.

This is especially valuable during live performance where wrong patterns need to be caught before they sound, and during composition where you want to compare pattern shapes quickly.

---

## User Stories

1. **As a live coder**, I want to see my pattern visualized when I evaluate a line, so I can verify it matches my intent before the next bar kicks in.
2. **As a composer**, I want to hover over a pattern string in the IDE and see its shape, so I can compare patterns without evaluating them.
3. **As a learner**, I want to understand what operators like `%7/8`, `:2/5`, `x3`, `?0.5` actually produce visually, so I can learn the DSL faster.

---

## Trigger Events

The preview should activate via any of the following (configurable):

| Trigger | Behavior | Priority |
|---|---|---|
| **On evaluation** (`.seq()`, `.note()`, etc.) | Auto-show preview window after pattern is parsed | P0 (ship first) |
| **Explicit call** (`i.kick.preview`, `i.kick.show`) | Show/refresh preview on demand | P0 |
| **IDE cursor placement** (SC IDE) | Preview the pattern string under cursor via SC IDE hooks | P2 (future) |
| **Static parse preview** (`I8TParser.preview("...")`) | Class method that previews a pattern string without needing an instrument | P1 |

### P0 scope: evaluation + explicit call

When any `.seq()`, `.note()`, `.chord()`, `.trigger()`, or `.vol()` / `.pan()` call is evaluated, the system parses the pattern (which it already does) and sends the event list to the preview display. A persistent preview window shows the most recently evaluated pattern, or multiple patterns if viewing a track.

---

## Display Modes

The display mode is auto-detected from the event data, but can be overridden.

### 1. Piano Roll (melodic patterns)

**Triggers when:** pattern parameter is `\note` or `\chord`, or values span a range > 1 and are not all 0/1-ish.

```
 14 |          ##            |
 12 |                        |
 10 |             ##         |
  9 |                ##      |
  7 |      ####         #### |
  5 |                        |
  3 |  ##                    |
  0 |####                    |
    +------------------------+
     1  2  3  4  5  6  7  8   (beats)
```

**Visual encoding:**
- X axis = time (beats), proportional to duration
- Y axis = note value (semitones, or MIDI note depending on context)
- Block width = note duration (accounting for `:` operator and `<`/`>` release)
- Block opacity/color = amplitude (piano/forte/`*` operator)
- Dashed blocks = probability (`?` operator, show at reduced opacity with "?" label)
- Split blocks = `|` (or) operator, show both options stacked with a divider
- Euclidean expansion = show the fully expanded result (rests as empty slots)

### 2. Step Grid (rhythmic/trigger patterns)

**Triggers when:** pattern parameter is `\trigger` (default for `.seq()`), or all values are <= 2 and numeric.

```
  1.0 |[#][ ][#][#][ ][#][ ][#]|   (euclidean 5/8)
      +-------------------------+
       1   2   3   4   5   6   7   8

  or for multi-value triggers:

  2.0 |   [ ]      [#]         |
  1.0 |[#]   [#]      [#][ ][#]|
  0.5 |         [#]            |
      +-------------------------+
```

**Visual encoding:**
- Single row if all values are identical (pure on/off rhythm)
- Multiple rows if trigger values differ (show value on Y axis)
- Cell brightness/size = amplitude
- Dashed cells = probability events
- Empty cells = rests (spaces in pattern)
- Grid columns = steps, width proportional to duration

### 3. Value Lane (continuous parameters)

**Triggers when:** pattern parameter is `\vol`, `\pan`, `\rel`, `\fx`, or any custom param with continuous values.

```
  1.0 |--##          ##--------|
  0.75|      ####               |
  0.5 |            ##          |
  0.25|                        |
  0.0 |                        |
     +------------------------+
```

**Visual encoding:**
- Bar chart or connected line graph
- Each step = one event, width proportional to duration
- Height = parameter value, scaled to range

---

## Data Flow

```
User code                    Existing system              New system
─────────                    ───────────────              ──────────
i.kick.seq("1  1 1 ")  →  Sequenceable.seq()
                             │
                             ├→ I8TParser.parse()  →  events list
                             │                           │
                             ├→ I8TPattern.new()         │
                             │                           │
                             ├→ ParameterTrack            │
                             │   .addPattern()            │
                             │                           ▼
                             │                     I8TPatternPreview
                             │                       .display(
                             │                         events,
                             │                         paramName,
                             │                         trackName,
                             │                         patternString
                             │                       )
                             │                           │
                             │                           ▼
                             │                     I8TPreviewWindow
                             │                       (renders via
                             │                        UserView + drawFunc)
```

### Key integration point

In `Sequenceable`, after the pattern is parsed and added to the parameter track, call:

```supercollider
// In Sequenceable.seq, after addPattern returns:
I8TPatternPreview.display(
    events: patternEvent.pattern.pattern,  // the parsed event array
    param: parameter,                       // \trigger, \note, etc.
    track: name,                            // instrument name
    source: patternString,                  // original string for title
    speed: lastPatternEvent.parameters[\speed],
    repeats: lastPatternEvent.parameters[\repeat]
);
```

### Static preview (no instrument needed)

```supercollider
// Class method for quick preview:
I8TParser.preview("(0 3 :2/5 (7 9 14 10)%7/8)");
// or:
I8TPatternPreview.show("1  1 1 ", \trigger);
```

---

## Classes to Create

### `I8TPatternPreview`

Singleton controller that manages the preview window.

```
I8TPatternPreview
  *instance          - singleton accessor
  *display(events, param, track, source, speed, repeats)
  *show(patternString, param)   - static parse + display
  *close                        - close preview window
  *toggle                       - toggle auto-preview on/off

  // instance
  window              - the Window
  canvas              - UserView for drawing
  currentEvents       - current event list being displayed
  currentParam        - current parameter name
  autoPreview         - bool, whether to auto-show on .seq()
  history             - last N previews for quick recall

  display(events, param, track, source, speed, repeats)
  detectMode(events, param) → \pianoRoll | \stepGrid | \valueLane
  refresh
```

### `I8TPatternRenderer`

Stateless drawing logic, separated from window management for testability.

```
I8TPatternRenderer
  *drawPianoRoll(canvas, events, bounds, options)
  *drawStepGrid(canvas, events, bounds, options)
  *drawValueLane(canvas, events, bounds, options)
  *drawCommon(canvas, events, bounds, options)  // grid lines, beat markers, title
```

---

## Mode Detection Logic

```
detectMode(events, param):
    if param == \trigger  → \stepGrid
    if param == \note || param == \chord  → \pianoRoll
    if param in [\vol, \pan, \rel, \fx, \amp, \rate]  → \valueLane

    // fallback: inspect values
    values = events.collect(_.val).reject(_ == \r)
    if values.every(isNumeric):
        range = values.maxItem - values.minItem
        if range <= 2 and values.every(_ <= 2):
            → \stepGrid
        else:
            → \pianoRoll   // treat as pitch-like
    else:
        → \stepGrid        // symbolic values get step display
```

---

## Visual Spec Details

### Window behavior
- **Floating window**, always on top, positioned near bottom-right (configurable)
- **Default size**: 500x200 px (piano roll), 400x100 px (step grid)
- **Auto-resize** to fit content: wider for longer patterns, taller for wider pitch range
- **Title bar**: shows `trackName.param: "source string"` (truncated)
- **Stays open** until explicitly closed or replaced by next preview
- **No focus steal**: window opens without taking keyboard focus from the IDE

### Color scheme
- Background: dark (#1a1a2e or similar, matching common SC IDE themes)
- Active cells/notes: bright accent color (#e94560 for trigger, #0f3460 for notes)
- Rest cells: dim gray outline (#333)
- Probability events: same color at 40% opacity + dotted border
- Or events: split cell, two colors
- Beat grid lines: subtle gray (#2a2a2a)
- Bar lines (every 4 beats): slightly brighter (#444)
- Current playback position: vertical line that sweeps if pattern is playing (P1)

### Typography
- Beat numbers below grid: small monospace
- Note values on Y axis: small monospace
- Pattern source string in title: monospace, truncated with ellipsis at 60 chars

---

## Event Properties Used for Rendering

From the parsed event list (output of `I8TParser.parse`), each event is an SC Event with:

| Property | Used for | Rendering |
|---|---|---|
| `val` | Note value or trigger amplitude | Y position (piano roll) or cell fill (grid) |
| `duration` | Step length in beats | X width of cell/block |
| `amplitude` | Volume level | Opacity or brightness |
| `rel` | Release time | Tail length on piano roll blocks |
| `val.operation == \maybe` | Probability | Dashed border, reduced opacity |
| `val.operation == \or` | Random choice | Split cell showing both options |
| `val == \r` | Rest | Empty cell |
| `euclidean` | Pre-expanded by parser | Already in event list, render normally |

---

## Explicit API

```supercollider
// --- Triggered automatically (P0) ---

i.kick.seq("1  1 1 ");
// → preview window appears showing step grid

i.piano.note("(0 3 :2/5 (7 9 14 10)%7/8)");
// → preview window appears showing piano roll


// --- Explicit calls (P0) ---

// show preview for last pattern on a specific parameter
i.kick.preview;              // defaults to \trigger
i.piano.preview(\note);      // specific param

// show preview without instrument context
I8TPatternPreview.show("1  1 1 ");              // auto-detect → step grid
I8TPatternPreview.show("0 3 7 12", \note);       // force piano roll

// close
I8TPatternPreview.close;

// toggle auto-preview globally
i.preview = true;    // enable auto-preview on every .seq()/.note()
i.preview = false;   // disable (explicit .preview calls still work)


// --- Configuration ---

// window position
I8TPatternPreview.position_(800, 600);

// color theme
I8TPatternPreview.theme_(\dark);    // default
I8TPatternPreview.theme_(\light);


// --- Multi-track view (P1) ---

// show all active patterns for an instrument
i.kick.preview(\all);

// show all patterns for a group
i.drums.preview;
```

---

## Handling Special Cases

### Probability (`?` operator)
Events with `val.operation == \maybe` render at reduced opacity with the probability value shown as a small label (e.g., "70%"). The cell/block is drawn with a dashed outline.

### Or (`|` operator)
Events with `val.operation == \or` render as a vertically split cell showing both possible values. Use two different shades or a diagonal split.

### Euclidean (`%` operator)
By the time events reach the renderer, euclidean patterns are already expanded into individual hits and rests by the parser. They render normally -- the user sees the expanded result, which is the point.

### Subsequences with operators
Subsequences with duration/repetition operators are already expanded by the parser. The preview shows the final flattened timeline.

### Rests (spaces → `\r`)
Rests show as empty cells with a subtle border (step grid) or empty space (piano roll). Multiple consecutive rests are merged into one wider empty space.

### Variable duration (`:` operator)
Events may have different durations. The X axis is proportional to time in beats, not to step count. This means cells/blocks have variable widths.

### Chords (comma-separated notes)
Multiple simultaneous notes render as stacked blocks at the same X position on the piano roll.

---

## Implementation Plan

### Phase 0: Core rendering (P0)

1. **Create `I8TPatternRenderer`** class with `drawPianoRoll`, `drawStepGrid`, `drawValueLane` class methods using SC's `Pen` drawing API inside a `UserView.drawFunc`.

2. **Create `I8TPatternPreview`** singleton class that manages a floating `Window` + `UserView`, handles mode detection, and exposes `*display` and `*show` class methods.

3. **Hook into `Sequenceable`**: after `addPattern` in `.seq()` (and shorthand methods), conditionally call `I8TPatternPreview.display(...)` when auto-preview is enabled.

4. **Add `.preview` method** to `Sequenceable` that reads the last pattern from the parameter track and sends it to the preview.

5. **Add `I8TParser.preview` class method** for standalone string preview.

### Phase 1: Enhanced features

6. **Playback cursor**: animated vertical line showing current position in the playing pattern (synced to the Routine via `main.clock`).

7. **Multi-pattern view**: show all patterns for a track stacked vertically (trigger + note + vol, etc.), or all patterns in a group.

8. **Pattern comparison**: show previous vs current pattern side by side on hot-swap.

### Phase 2: IDE integration

9. **SC IDE tooltip**: use `Document.current` hooks to detect cursor position and preview the pattern string under the cursor.

10. **OSC-based external display**: send pattern data via OSC so external tools (web UI, Processing sketch, etc.) can render previews independently.

---

## File Locations

```
Classes/Core/Visualization/I8TPatternPreview.sc    - singleton controller
Classes/Core/Visualization/I8TPatternRenderer.sc   - drawing logic
```

---

## Depends On

- `I8TParser.parse()` -- already produces the event list we need
- `I8TPattern` -- stores parsed events
- `ParameterTrack` -- stores sequence info
- SuperCollider Qt GUI framework (`Window`, `UserView`, `Pen`)

## Does Not Depend On

- Playback state (preview works on parsed data, not on what's currently playing)
- Audio server (preview is pure GUI, no audio needed)
- MIDI controllers

---

## Open Questions

1. **Auto-preview default**: should auto-preview be ON by default (showing a window every time you evaluate a pattern), or OFF by default (requiring `i.preview = true` or explicit `.preview` calls)? Recommendation: **OFF by default** for live performance (you don't want surprise windows during a set), but easy to enable for practice/composition sessions.

2. **Window reuse vs. multiple windows**: should each `.preview` call reuse the same window, or should it be possible to open multiple preview windows for comparison? Recommendation: **single window by default**, with an option like `i.kick.preview(\note, multi: true)` to open a new one.

3. **Scope of "preview" for groups**: when you call `i.drums.preview`, should it show all instruments in the group in a stacked view, or one at a time? Recommendation: **stacked view** (one row per instrument, all sharing the same time axis).

4. **Playback indicator priority**: how important is the real-time playback cursor? It requires a separate animation routine synced to the clock. Recommendation: **P1**, not critical for initial usefulness.
