
## Sequencing

First, run this, and don't hit Cmd+.

If you, do, restart INSTRUMENT with 'i.play'

```SuperCollider

i=INSTRUMENT();

```

### Parameters

#### Sequence any Parameter

```SuperCollider

i[\sound].seq(\anyParameter, "1 2 3");


i[\piano]=SynthPlayer(\distNote1);

i[\piano].seq(\note, "0 7 10 14");
i[\piano].seq(\rel, "0.5 0.2 1 2");

```

#### Parameter Shorthands:

#### **trigger**

```SuperCollider

i[\kick]=SynthPlayer(\kickElectro);

// numbers are amps
i[\kick].trigger("1");
i[\kick].trigger("0.5 0.7 1");
i[\kick].trigger("1");
i[\kick].stop;

```

#### **note**


```SuperCollider

i[\piano]=SynthPlayer(\distNote1);

// numbers are semitones
i[\piano].seq(\note, "3 5 7 9");
i[\piano].seq(\note, "2 4 8 10 12");
i[\piano].seq(\note, "3 5 7 9");


i[\piano].stop;

```
