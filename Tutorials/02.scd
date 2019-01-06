# INSTRUMENT

---


- Sounds
- Writing Patterns
- Pattern Functions
- Fx



......

## Sounds

### Before...

Load Synthdefs for this Tutorial.

#### SynthPlayer

Plays back patterns using Synths


```SuperCollider

i[\piano] = SynthPlayer(\pianoSynth);

```
#### Proxy

Plays back patterns controlling NodeProxy parameters

## Sequencing

```SuperCollider

// '.seq' maps to \trigger parameter

seq("1")

```


```SuperCollider
rm


```


---

# Writing Patterns

## String Patterns


```SuperCollider

i[\kick][1].seq("0.5xxx ")

```


## Array Patterns


```SuperCollider

i[\kick][1].seq([0.5, 0.25, 0.75])

```

---

# Pattern Functions

## Set Parameters

### speed

```SuperCollider
i[\kick][1].seq("1").speed(1);
i[\kick][1].seq("1").speed(2);
i[\kick][1].seq("1").speed(4);
i[\kick][1].seq("1").speed(8);
i[\kick][1].seq("1").speed(1/2);
i[\kick][1].seq("1").speed(1/4);
i[\kick][1].seq("1").speed(1/8);
```


### repeat


```SuperCollider

i[\kick][0].seq(":0.5 1").repeat(8);
i[\kick][1].seq("1  0.5").repeat(1);

```
#### 'repeat' shorthands

##### do
```SuperCollider

i[\kick][1].seq("1").do(1)
```

##### one
```SuperCollider

// same result
i[\kick][1].seq("1").do(1)
i[\kick][1].seq("1").one
```
