# Feature: Pattern Variables

## Concept

Allow naming and reusing pattern fragments within a pattern string, so complex
patterns can be composed from named building blocks instead of written linearly.

Today a pattern like this:

```supercollider
i.piano.note("(0 3 (:2/5 7 9 14 10)%7/8)x3 (0 3 0 (:2/5 7 9 14 10)%7/8)")
```

Requires duplicating the `:2/5 7 9 14 10` phrase and mentally tracking nested
brackets. With pattern variables:

```supercollider
i.piano.note("a=(0 3); b=:2/5 (7 9 14 10)%7/8; =(a b)x3 0 3 0 b")
```

Variables are **expanded as a string preprocessing step** before the existing
parser runs — no changes to the core parse/playback engine needed.

## Syntax

### Statements

A pattern string is split by `;` into **statements**. Each statement is one of:

| Form | Meaning | Emits events? |
|------|---------|---------------|
| `name=value` | Variable assignment | No |
| `=pattern` | Evaluate pattern | Yes |
| `pattern` | Evaluate pattern (shorthand) | Yes |

The `=` at the start of a statement (with no name) explicitly marks it as
an evaluation block. A bare statement without any `=` also evaluates (useful
for simple variable references like a trailing `b`).

### Mid-pattern declarations

New variables can be declared **inside** an evaluation block. A `name=value`
sequence within an `=` block both stores the variable AND emits its events
at that position:

```supercollider
"=(a b)x3 a2=0 3 0 b"
//        ^^^^^^^^^^^ mid-pattern: defines a2 AND emits "0 3 0", then emits b
```

## User API

### Basic assignment and evaluation

```supercollider
// assign a and b, then evaluate a pattern using them
i.piano.note("a=(0 3); b=:2/5 (7 9 14 10)%7/8; =(a b)x3 0 3 0 b")
```

### Brackets optional on assignment

Both work — brackets are only needed to apply operators to the group:

```supercollider
// bracketed
i.bass.note("a=(0 3 5); =a ax2")

// unbracketed — value runs until next ; or name=
i.bass.note("a=0 3 5; =a ax2")

// both expand to: "0 3 5 0 3 5 0 3 5"
```

### Variables referencing earlier variables

```supercollider
i.piano.note("a=0 3; b=a 5 7; =(b)x2")
// a → "0 3"
// b → "0 3 5 7"  (a expanded in b's definition)
// emits → "(0 3 5 7)x2"
```

### Bare variable reference as shorthand eval

A statement that is just a variable name (no `=`) emits its value:

```supercollider
i.bass.note("riff=(0 3 5 7); riff; riff")
// emits → "(0 3 5 7) (0 3 5 7)"
```

### Mid-pattern variable declaration

```supercollider
i.piano.note("a=(0 3); b=(0 2 3); =(a b)x3 a2=0 3 0; b")
// Statement 1: a = "(0 3)"
// Statement 2: b = "(0 2 3)"
// Statement 3: emit "(a b)x3" then mid-pattern define a2="0 3 0" (also emitted)
//   → expands to: "((0 3) (0 2 3))x3 0 3 0"
// Statement 4: bare "b" → emit "(0 2 3)"
// Final: "((0 3) (0 2 3))x3 0 3 0 (0 2 3)"
```

### Variables with operators

Variables store raw pattern text, so they compose naturally:

```supercollider
i.piano.note("ch=(7 9 14 10)%7/8; =0 3 :2/5 ch")
// ch expands to "(7 9 14 10)%7/8" — brackets and operator preserved

i.bass.note("a=0 3; =(a 5 7)x3")
// expands to: "((0 3) 5 7)x3"
```

### Full example (motivating use case)

```supercollider
// before:
i.piano.note("(0 3 (:2/5 7 9 14 10)%7/8)x3 (0 3 0 (:2/5 7 9 14 10)%7/8)")

// after:
i.piano.note("a=(0 3); b=:2/5 (7 9 14 10)%7/8; =(a b)x3 0 3 0 b")
```

## Syntax rules

### Variable names

- Must match `[a-zA-Z][a-zA-Z0-9]*` (letter followed by alphanumerics)
- Case-sensitive: `a` and `A` are different variables
- Names are local to a single pattern string (not shared across calls)

### Statement delimiter `;`

- Splits the pattern string into ordered statements
- Whitespace around `;` is trimmed
- Does not conflict with any existing operator

### Assignment `name=value`

- **Bracketed**: `name=(...)` — value is the full bracketed expression including
  any trailing operators (e.g., `name=(1 2 3)x2` stores `(1 2 3)x2`)
- **Unbracketed**: `name=tokens...` — value is everything after `=` until the
  next `name=`, `;`, or end of string

### Evaluation `=pattern`

- A leading `=` with no identifier before it marks the statement as an
  evaluation (emit) block
- The pattern after `=` is expanded and passed to the parser
- Mid-pattern `name=value` sequences inside an eval block both define the
  variable and emit the value at that position

### Bare reference

- A statement that doesn't contain `=` at all is treated as a pattern to
  evaluate directly (variable names within it are expanded)

### Expansion order

1. Statements processed left-to-right
2. Each assignment expands any previously-defined variables in its value
3. Forward references (using a variable before defining it) are not expanded
   (the name stays as-is, likely producing a rest or parse error)

## How it works

Variable expansion is a **pure string transformation** that runs before
`I8TParser.parse()`. The existing parser sees only the fully-expanded string
and requires no modifications.

```
Input:  "a=(0 3); b=:2/5 (7 9 14 10)%7/8; =(a b)x3 0 3 0 b"

Step 1 — Split on ";":
  ["a=(0 3)", "b=:2/5 (7 9 14 10)%7/8", "=(a b)x3 0 3 0 b"]

Step 2 — Process each statement left-to-right:

  "a=(0 3)"
    → assignment: a = "(0 3)"
    → output: nothing (pure assignment)

  "b=:2/5 (7 9 14 10)%7/8"
    → assignment: b = ":2/5 (7 9 14 10)%7/8"
    → output: nothing (pure assignment)

  "=(a b)x3 0 3 0 b"
    → eval block, expand variables:
      a → "(0 3)", b → ":2/5 (7 9 14 10)%7/8"
    → expanded: "((0 3) :2/5 (7 9 14 10)%7/8)x3 0 3 0 :2/5 (7 9 14 10)%7/8"
    → output: "((0 3) :2/5 (7 9 14 10)%7/8)x3 0 3 0 :2/5 (7 9 14 10)%7/8"

Step 3 — Concatenate outputs (space-joined):
  "((0 3) :2/5 (7 9 14 10)%7/8)x3 0 3 0 :2/5 (7 9 14 10)%7/8"

Step 4 — Pass to I8TParser.parse() as usual
```

### Example with mid-pattern declaration

```
Input:  "a=(0 3); b=(0 2 3); =(a b)x3 a2=0 3 0; b"

Statements: ["a=(0 3)", "b=(0 2 3)", "=(a b)x3 a2=0 3 0", "b"]

"a=(0 3)"      → assign a = "(0 3)"
"b=(0 2 3)"    → assign b = "(0 2 3)"

"=(a b)x3 a2=0 3 0"
  → eval block. Scan for mid-pattern assignments:
    pattern part: "(a b)x3"
    mid-pattern:  a2 = "0 3 0"  (define AND emit)
  → expand vars in pattern: "((0 3) (0 2 3))x3"
  → expand vars in mid-pattern value: "0 3 0" (no vars to expand)
  → output: "((0 3) (0 2 3))x3 0 3 0"
  → a2 is now defined for later use

"b"            → bare ref, expand → output: "(0 2 3)"

Final: "((0 3) (0 2 3))x3 0 3 0 (0 2 3)"
```

### Variable name collision with note values

If a variable name matches a note name (e.g., `b` could mean the note B),
the variable takes precedence — it was explicitly defined with `=` in the
same pattern. Use a different variable name or the MIDI number for the note.

## Implementation Plan

### Step 1: Add `*expandVariables` class method to I8TParser

- [ ] New method `*expandVariables {|input|}` that returns the expanded string
- [ ] Split input on `;`, trim whitespace from each segment
- [ ] Classify each segment: assignment (`name=`), eval (`=`), or bare pattern
- [ ] Return expanded string or original input unchanged if no `;` found
- **File**: `Classes/Core/Sequencing/I8TParser.sc`

### Step 2: Implement assignment extraction

- [ ] Detect `name=value` pattern: identifier `[a-zA-Z][a-zA-Z0-9]*` followed
  immediately by `=`
- [ ] For bracketed values `name=(...)`, capture the full bracket expression
  including trailing operators
- [ ] For unbracketed values `name=tokens`, capture until next `name=` or
  end of segment
- [ ] Store in ordered list of pairs (to support variable-references-variable)
- [ ] Expand earlier variables in later definitions
- **File**: `Classes/Core/Sequencing/I8TParser.sc`

### Step 3: Implement eval block expansion

- [ ] For `=pattern` segments, detect mid-pattern `name=value` sub-assignments
- [ ] Split the eval block into alternating pattern-parts and assignments
- [ ] Assignments: store variable, expand value, include in output
- [ ] Pattern parts: expand all known variables, include in output
- [ ] Concatenate all parts as the segment's output
- **File**: `Classes/Core/Sequencing/I8TParser.sc`

### Step 4: Implement bare reference expansion

- [ ] For segments with no `=`, expand all known variable names
- [ ] Use word-boundary detection: character before/after the name must be a
  space, bracket, operator character, or string boundary — not a substring
  of a longer token
- **File**: `Classes/Core/Sequencing/I8TParser.sc`

### Step 5: Wire into `*parse`

- [ ] Call `this.expandVariables(input)` at the start of `*parse`, before
  `validateMatching` and `getSubsequences`
- [ ] The rest of `*parse` operates on the expanded string unchanged
- **File**: `Classes/Core/Sequencing/I8TParser.sc`

### Step 6: Verify

- [ ] Pattern without variables or `;` produces identical results
- [ ] `"a=(0 3); =a a"` → `"(0 3) (0 3)"`
- [ ] `"a=0 3; b=a 5; =(b)x2"` → `"(0 3 5)x2"` (var referencing var)
- [ ] `"a=(0 3); =(a 5 7)x3"` → `"((0 3) 5 7)x3"` (nested brackets)
- [ ] `"riff=(0 3 5 7); riff; riff"` → `"(0 3 5 7) (0 3 5 7)"` (bare ref)
- [ ] `"a=0 3; =(a)x3 b=5 7; b"` → `"(0 3)x3 5 7 5 7"` (mid-pattern decl)
- [ ] Original motivating example produces equivalent events to linear version
- [ ] Patterns with `D|F` (or operator) still work — no `;` means no preprocessing

## Depends on

- Existing `I8TParser.parse()` architecture (string → subsequences → events)
- No changes to I8TPattern, I8TParameterTrack, or playback code

## Considerations

- **Existing TODO**: I8TParser.sc line 817 has
  `// TODO: implement pattern variables using { and }`. This PRD proposes
  `name=value` with `;` delimiters instead. `{ }` could still be used for a
  different purpose (e.g., lambda/computed values) in the future.
- **`;` is clean**: Unlike the previous `|` / `:|` proposal, `;` has no
  conflict with any existing operator. It's a familiar statement separator.
- **No recursion depth limit**: Variables can reference other variables, but
  pattern strings are short and manually typed. A depth or length limit could
  be added later if needed.
- **Variable scope is per-pattern-string**: Variables are not shared across
  different `.note()` or `.seq()` calls. For shared motifs across instruments,
  use SuperCollider variables:
  ```supercollider
  ~riff = "0 3 5 7";
  i.bass.note(~riff);
  i.piano.note(~riff);
  ```
- **Redefinition is allowed**: If the same variable name is assigned twice, the
  second overwrites the first. This lets patterns evolve mid-string.
- **Backward compatible**: Patterns without `;` skip the preprocessing entirely
  and behave exactly as before.
