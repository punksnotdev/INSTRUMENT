# INSTRUMENT
### pre-alpha
#### afterthought





INSTRUMENT is a library for livecoding music (beats, basslines, harmony, looping, FX, signal routing, synthesis, etc.) and interfacing with musical instruments and controllers from inside the SuperCollider environment.


```SuperCollider

// Create a beat

// First, evaluate following block
Task.new({


	i = INSTRUMENT().play;

	2.wait;

	i.drums = (
		kick: INSTRUMENT(i.synths.electro.kick),
		hihat: INSTRUMENT(i.synths.electro.hihat),
		clap: INSTRUMENT(i.synths.electro.clap)
	);

	i.drums.kick.seq("1  1xx");
	i.drums.hihat.seq(" 1").speed(2);
	i.drums.clap.seq("   1   1xx");

	i.drums.clock = 2;



}).play;


// Play with clock

i.drums.clock = 4;

// Add FX and slow down
(
i.drums.fx="reverb";
i.drums.clock = 1;
)

// Stop

i.drums.stop;

```





# IMPORTANT


INSTRUMENT is now publicly available as an pre-alpha release.

Please try this tool and [get in touch](mailto:furenku@gmail.com).

Expect turbulence in this repository while this test phase evolves.




### Motivation



This tool is aimed at the creation of musical compositions from scratch, 'on the fly'. INSTRUMENT focuses on musical language: rhythm, harmony, melody, audio processing.

INSTRUMENT also enables the connection of MIDI controllers with an easy interface that facilitates its mapping to different sound parameters.

The name is inspired by Fugazi.

INSTRUMENT is a voluntary independent open source initiative operating from the Mexican live coding underground

For questions, inquiries, help, or fun conversations, please contact me at furenku@gmail.com.


## Disclaimers:

- Full Documentation + Tutorial coming soon.
- INSTRUMENT is in pre-alpha phase. The API is subject to change in the near future.


# Installation:

1. Figure out your Supercollider Extensions folder path. You can find this inside SuperCollider environment, by running the following command:


```SuperCollider

Platform.userExtensionDir;

```

2. To install INSTRUMENT, theres two possible ways:
	1. Clone the repo inside your 'Extensions/' folder
	2. or download the .zip file and place it inside the 'Extensions/' folder.

3. Restart or Recompile SuperCollider.
4. Verify installation. Type:


```SuperCollider

i=INSTRUMENT();

```

to see if the Library was successfully added.




# Getting Started


## Start Server

```SuperCollider

(
// useful snippet for increasing default memory

s.options.memSize=2048*1024;
s.options.maxNodes=128*1024;
s.boot;

)

```


# Using INSTRUMENT

Most things related with INSTRUMENT are accessed through a single intance.

You will normally begin by typing and evaluating the following line:

```SuperCollider

i = INSTRUMENT().play;

```


This line will be repeated throughout this document, so any individual fragment can be tried in isolation.


## Synths

INSTRUMENT comes with some predefined SynthDefs that you can use. If you want to add more, see [**Loading more Synths**](#load-more-synths)


```SuperCollider

i=INSTRUMENT().play;

// creates a dictionary inside **i.synths**

// access by name

i.kick="kickElectro";
i.kick.seq("1");

// use the dictionary
i.kick=i.synths.electro.kick;

// replace sounds

i.kick=i.synths.electro.hihat;



// access by index
i.kick=i.synths.drums.kick[0];
i.kick=i.synths.drums.kick[1];

// index wraps around total synths number inside folder
i.kick=i.synths.drums.kick[99999];

// access randomly
i.kick=i.synths.drums.kick.choose;
i.kick=i.synths.drums.kick.choose;

i.kick=i.synths.drums.snare.choose;
i.kick=i.synths.drums.snare.choose;


// long vs short synth routes are equivalent:
i.synths.percussion.drums.kick[3] == i.synths.kick[3]

// each synth gets added to root folder
i.synths.bass.simpleBass == i.synths.simpleBass;

```



See [**Synthesizers**](#synthesizers), at the end of this document, for more info about working with Synths, Effects, and this automatic loader.




## Basic sequencing:

```SuperCollider

s.boot;

i=INSTRUMENT().play;
i.kick="kickElectro";
i.kick.seq("1");


// trigger synths with different amp values:
i.kick.seq("1 0.3 0.75 2");

i.kick.seq([1, 0.3, 0.75, 2]);


i.kick.stop;


// Press Ctrl/Cmd + . to stop the server

```

**.seq** method allows to sequence any parameter. In previous example, the default parameter 'trigger' is being automatically passed.

Lets pass a different parameter, *note*.

```SuperCollider

i=INSTRUMENT().play;

i.bass=i.synths.trance.choose;

i.bass.seq(\note, "0 2 3 5");

// shorthand:
i.bass.note("0 2 3 5");

// you can also use note names:

i.bass.note("C D Eb F");

// you can choose octaves

i.bass.note("C3 D4 Eb5 F6");


```

There are shorthands 'seq' methods for some common parameters, some of them:

- trigger
- note
- chord
- vol
- pan
- fx
- fxSet

```SuperCollider

(
i=INSTRUMENT().play;

i.bass=i.synths.trance.choose;


i.bass.note("0 2 3 7");
i.bass.note("C D Eb G");

i.bass.octave=4;
i.bass.octave=3;
i.bass.octave=5;

)


```

## Set tempo

```SuperCollider


i.tempo=180;
i.tempo=140;
i.tempo=120;
i.tempo=220;

```

## Add silences

```SuperCollider

i.kick=i.synths.kick.choose;

i.kick.seq("1   0.5");


```

## Change instrument parameters:

```SuperCollider


i.kick.amp=1/2;
i.kick.amp=3/4;
i.kick.amp=1;
i.kick.amp=2;

i.kick.clock=2;
i.bass.clock=4;

```


## Repeating events:


The 'x' operator inside string Patterns allow for repetition of last value

```SuperCollider

i=INSTRUMENT().play;


(
i.piano=i.synths.piano.rhodes1;

i.piano.amp=4;
i.piano.clock=2;
i.piano.note("0xx 2xxx 3xxxxx");
i.piano.note("0x2 2x3 3x5");

// get last pattern duration:
i.piano.duration;

i.kick=i.synths.electro.kick;

// repeat three times
i.kick.seq("1 0.5x3");

i.kick.duration;
// lazy equivalent
i.kick.seq("1 0.5xxx");

// clear patterns
i.kick.rm(\trigger)

i.kick[0].seq("1");
i.kick[1].seq("1    1");
i.kick[2].seq("1x14  ").speed(16).x(1);

i.kick.seq("1x4 :0.5 1x8 :0.25 1x16 :0.125 1x32");

// add some silences
i.kick.seq("1x5 :0.25 1  1x3   1  1x3");

// check duration

i.kick.duration;


i.bass=i.synths.bass.trance[2];
i.bass.octave=3;
i.bass.clock=3;
i.bass.amp=2;

i.bass.note("C   Dx2   Ebx3   Bbx2   Ax3   F  ");

i.bass.duration;




```
## Changing steps duration


The ':' operator inside string Patterns allows for setting of duration any values following it.

It requires a duration parameter, which must be a number (integer or fractionary).

```SuperCollider

// all subsequent events are affected


i=INSTRUMENT().play;

i.kick=i.synths.kick.choose;

// decimal representation
i.kick.seq("1 :0.25 1x3 ");

// fraction representation
i.kick.seq("1 :1/4 1x3 ");

i.kick.seq("1x4 :1/4 1x4 :1/8 1x4:2 1x2");




```

Using fractions allow some interesting rhythms:

```SuperCollider

	i=INSTRUMENT().play;

	i.kick=i.synths.kick.choose;
	i.hihat=i.synths.hihat.choose.postln;

	i.kick.seq("1xx  :3/8 1x3  1x2 :1/4 1x3  1x2  :1/2 1xx");
	i.hihat.seq("1xx  :1/3 1x3");

```





## Subsequences

You can group patterns inside patterns using parenthesis. This is useful for playing parts of patterns with different durations and repetitions.

```SuperCollider


i=INSTRUMENT().play;

i.bass = i.synths.bass.trance[2];
i.bass.clock=4;
// repeat subsequence times
i.bass.note("Cx3 (Dx2 Gx2)x2 A");
i.bass.note("(Dx2 Gx2)x3  F A ");
// change subsequence duration
i.bass.note("(Dx2 Gx2):0.5  F A ");
// same:
i.bass.note("(Dx2 Gx2):1/2  F A ");
i.bass.note("(Dx3 Gx2  B  Cx7):1/3  F A");

// change both duration and repetition
i.bass.note("(Dx2 Gx2):1/2x4  F A ");
// multiple subselections
i.bass.note("(Dx2 Gx2):1/2x4  (F A)x3 ");
// between patterns
i.bass.note("C D E  (Dx2 Ex2):1/2x3  F G C5 (B A)x2  C5 B D ");

```






## Sequencing patterns

```SuperCollider

// Ctrl/Cmd + Period

i=INSTRUMENT().play;
i.kick="kickElectro";


i.kick.clock=2;
i.kick[0].seq("1");
i.kick[1].seq("1xx   1");
i.kick[2].seq("1   1xx   1 ").speed(2);


```

## Removing patterns:

```SuperCollider


i.kick.rm(\seq,0);
i.kick.rm(\trigger,0);
i.kick.rm(\trigger,1);
i.kick.rm(\trigger,2);


```

## Control pattern speeds

```SuperCollider


i.kick[0].seq("1").speed(1);
i.kick[1].seq("1").speed(2);
i.kick[2].seq("1").speed(4);


```

## Controlling pattern repetitions

```SuperCollider

i.kick[0].seq("1").speed(1).repeat(4);
// different names for the same function:
i.kick[1].seq("1").speed(2).do(8);
i.kick[2].seq("1").speed(4).x(16);


```

## Jump to position

```SuperCollider



i = INSTRUMENT().play;
(
	i.hihat="hihatElectro";
	i.hihat.seq("1xx :0.25 1xxx :0.5 1xxx :2 1").speed(2);
	i.kick="kickElectro";
	i.kick.seq("1xx :0.25 1xxx :0.5 1xxx :2 1").speed(1/3);
)


// repeteadly evaluate this group:
(
i.hihat.go(0);
i.kick.go(0);
)


// again:
(
i.hihat.go(13);
i.kick.go(13);
)

```

## Create a basic beat

```SuperCollider

(


i = INSTRUMENT().play;

i.drums = (
	kick: i.synths.electro.kick,
	hihat: i.synths.electro.hihat,
	clap: i.synths.electro.clap
);

i.drums.kick.seq("1");
i.drums.hihat.seq(" 1").speed(2);
i.drums.clap.seq(" 1");

i.drums.clock = 2;


)



```

## Setting parameters

```SuperCollider


(

i = INSTRUMENT().play;

i.bass=i.synths.trance[2];
i.bass.note("0 2 3");
)

i.bass.rel=2;
i.bass.rel=0.2;
i.bass.dist=2;
i.bass.dist=1;
i.bass.dist=4;
i.bass.dist=14;
i.bass.dist=1;
i.bass.rel=1/2;

```


## Sequencing parameters

```SuperCollider




i.bass.seq(\rel,[2,0.2,1]);

// rests in params:
i.bass.seq(\rel,[2,0.2,\r,\r,1]);



```

## Sequencing synthdefs:

```SuperCollider

i=INSTRUMENT().play;
(

i.kick="kickDeep";
i.kick.seq("1");
i.kick.synthdef([\kickSyn1,\kickSyn2,\kickSyn3]);



i.bass=i.synths.trance.choose;
i.bass.note("0 2 3");
i.bass.synthdef([\bassTrance1,\bassTrance2,\bassTrance3]);

)


```

## FX (Effects)

```SuperCollider


(
	i=INSTRUMENT().play;

	i.piano=i.synths.piano[1];
	i.piano.note("0 2 3 5");


	i.piano.octave=3;

)

i.piano.fx="reverb";
// variants
i.piano.fx="reverb.large";
i.piano.fx="reverb.small";

(
i.piano.clock=1/4;
i.piano.fx="reverb.infinite";
)


i.piano.clock=1;
i.piano.fxSet(\wet,0);
i.piano.fxSet(\wet,1);
i.piano.fxSet(\wet,3/4);
i.piano.fxSet(\wet,1/4);

i.piano.fxSet(\room,7/8);
i.piano.fxSet(\damp,7/8);

i.piano.fxSet(\room,1/8);
i.piano.fxSet(\damp,1/8);


i.piano.fxSet(\room,1);
i.piano.fxSet(\damp,1);


i.piano.stop;

i.piano.fx=nil;
i.piano.play;

i.piano.fx=i.synths.fx.reverb[2];


```


```

## Sequencing fx parameters

```SuperCollider

(
	i = INSTRUMENT().play;

	i.clap="clapElectro";
	i.clap.seq(" 1  :0.25 1xx").speed(2);

	i.clap.fx = \reverbLPF;
	i.clap.fxSet([
		(filterHz:3000, lag:1/3),
		(filterHz:1000),
		(filterHz:2000),
		// more than one parameter:
		(filterHz:3000,q:0.1,lag:0),
		(filterHz:300,q:0.01),
	]).speed(1/2);
)


```

## Sequencing fx

```SuperCollider

(
	i = INSTRUMENT().play;

	i.clap="clapElectro";
	i.clap.seq(" 1  :0.25 1xx").speed(2);

	i.clap.fx([\reverb,\reverbLPF,\gateDistort]).speed(1/4);
)



```

## InstrumentGroup


You can group Instruments for easier manipulation, while retaining access to individual instruments

```SuperCollider


i = INSTRUMENT().play;


i.drums=(
	kick: i.synths.kick.choose,
	hihat: i.synths.hihat.choose,
	snare: i.synths.snare.choose,
);

i.drums.kick.seq("1");
i.drums.hihat.seq(" 1").speed(2);
i.drums.snare.seq(" 1").speed(1/2);

i.drums.clock=1/2;
i.drums.clock=2;
i.drums.clock=1;
i.drums.amp=1/2;
i.drums.amp=1;
i.drums.stop;
i.drums.play;

// add fx to group
i.drums.fx = \reverb;
i.drums.fx = nil;


// redeclare group with less instruments


i.drums=(
	hihat: i.synths.hihat.choose,
)

// restore

i.drums=(
	kick: i.synths.kick.choose,
	hihat: i.synths.hihat.choose,
	snare: i.synths.snare.choose,
)




// melodic synths:


i.melodies=(
	note1: i.synths.note.dist[0],
	note2: i.synths.note.dist[1]
);

i.melodies.note1.note("0 7  8");
i.melodies.note2.note("12  15 13");

i.melodies.octave=5;
i.melodies.octave=6;
i.melodies.octave=3;
i.melodies.octave=4;


```


##### Playing with groups (Example)

```SuperCollider

(

i=INSTRUMENT().play;


i.drums=(
	k:i.synths.kick[3],
	h:i.synths.hihat[2],
	s:i.synths.snare[2],
);

i.drums.k.seq("1x3  1x3  1x3  :2  1").speed(4);
i.drums.h.seq("1x2  1 ").speed(8);
i.drums.s.seq(" 1x3  1x2  :2 1  :1/2  1x3  1x2 ").speed(2);

)

i.drums=(
	k:i.synths.kick[4],
	h:"kickDeep",
	s:i.synths.snare[2],
);


i.drums=(
	// k:i.synths.electro.kick,
	h:"hihatClosed",
	s:i.synths.snare[5],
);


(

i.drums=(
	k:i.synths.electro.kick,
	h:"kickDamp",
	hh:i.synths.hihat[0],
	s:i.synths.snare[2],
);

i.drums.k.seq("1").speed(2);
i.drums.hh.clock=2;
i.drums.hh.seq("1x7  1x6  ").speed(4);

)

i.drums.hh=i.synths.hihat[2];
i.drums.k=i.synths.hihat[0];

// wait a bit:
i.drums.k=i.synths.electro.kick;


(
	i.drums.clock=1/8;
	i.drums.fx="reverb.infinite";
)

(
	i.drums.clock=2;
	// i.drums.fx=i.synths.reverb;
	i.drums.fx="reverb.small";
)

(
	i.drums.k.seq("1").speed(2);
	i.drums.fx=nil;
	i.drums.clock=1;
	i.drums.go(0);
)

i.drums.stop;

```




## Sequencing Group parameters

Groups are also sequenceable.
```SuperCollider

i = INSTRUMENT().play;

i.g=(
	g1: i.synths.kick[0],
	g2: i.synths.hihat[0],
	g3: "hihatOpen",
	g4: i.synths.snare[2],
);


i.g.g1.seq("1");
i.g.g2.seq(" 1").speed(2);

i.g.g3.seq("  1").speed(4);
i.g.g4.seq("  1").speed(3);


i.g[0].seq(\amp,"0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8").x(1);
i.g[1].seq(\amp,"2 1 0.3").speed(2);


// You can sequence any parameter:

i.g[0].seq(\rel,"0.1 2").speed(1/4).x(2);
i.g[1].seq(\rel,"0.1 0.3 0.5").speed(1/2);

i.g[0].seq(\clock,"1 2 4 0.25").speed(1/4).x(2);
i.g[1].seq(\clock,"1 2 4 0.25").speed(1/4).x(2);

i.g[0].seq(\go,"13 0").speed(1/4).x(2);
i.g[1].seq(\go,"3 2").speed(2).x(8);

i.g[0].seq(\fx,[\reverbLPF,\gateDistort]).speed(1/4).x(2);
i.g[1].seq(\fx,[nil,\reverb]).speed(1/4).x(2);


```

There are some main "group" parameters, but any parameter can be sequenced and passed directly to the group's instruments.

Main parameters are:

- **\go**: Integer: Jump to position
- **\amp**: Float: Set group's instruments' amp
- **\octave**: Integer: Set group's instruments' octave
- **\clock**: Integer: Set group's instruments' clock
- **\fx**: String: Set group's instruments' fx synthdef



# Clear and Restore
## i.clear
## i.restore


Use **i.clear** to stop running instruments,, so you can easily create quick changes without pausing main thread.
You can use **i.restore** to bring back cleared instruments and sequencer functions.



```SuperCollider

s.boot;
i=INSTRUMENT().play;

(
	i.drums=(
		k:i.synths.kick[3],
		h:i.synths.hihat[2],
		s:i.synths.snare[2],
	);

	i.drums.k.seq("1x3  1x3  1x3  :2  1").speed(4);
	i.drums.h.seq("1x2  1 ").speed(8);
	i.drums.s.seq(" 1x3  1x2  :2 1  :1/2  1x3  1x2 ").speed(2);

	i.bass = i.synths.bass.trance[2];
	i.bass.note("0 2 3");
	i.bass.fx="reverb";
	i.bass.octave=3;
	i.bass.amp=1.5;

	i.every(2,{ c=[2,4,8].choose; i.bass.clock=c; ["chose",c].postln; })

)
(
	// Clear previous sounds
	i.clear;

	i.drums2=(
		k:i.synths.kick[0],
		s:i.synths.snare[4],
	);
	i.drums2.k.seq("1x2  1x4  1x3").speed(8);
	i.drums2.s.seq(" 1x2  :2 1  :1/2  1x3  1x2 ").speed(4);
)

// restore first group
i.restore;

i.drums2.stop;

i.stop;

```


## Array manipulation
```SuperCollider



i = INSTRUMENT().play;

(
i.bass=\bassTrance1;
i.hihat=\hihatElectro;
i.bass.clock=4;
i.bass.note("0 2 3");
)

i.bass.note("0 2 3").pyramid.mirror;
// randomness
i.bass.note("0 2 3 5 7 10 12").random;
i.bass.note("0 2 3 5 7 10 12").random;


i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.5); // default value
i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.25);
i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.75);
i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0); // never play
i.hihat.seq("1xxxxxxxxx").speed(8).maybe(1); // always play

i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.35);

```


A small composition:

```SuperCollider
// Ctrl/Cmd + .

i = INSTRUMENT().play;

(


i.voices = (

	a: i.synths.pad.dist[0],
	b: i.synths.pad.dist[1],
	c: i.synths.note.dist[0],
	d: i.synths.note.dist[1],

);


i.voices.a.note("0 2  0 2 3   0 2 3 7    0 2 3 7 14").speed(1/4);
i.voices.a.amp=1.5;
i.voices.a.set(\rel,8);

i.voices.b.note("0 2  0 2 3   0 2 3 7    0 2 3 7 14").speed(1/2).random;
i.voices.b.octave=6;
i.voices.b.amp=1/4;

i.voices.c.note("0 2  0 2 3   0 2 3 7    0 2 3 7 14").speed(2).random;
i.voices.c.octave=8;
i.voices.c.amp=1/4;

i.voices.d.note("0 2  0 2 3   0 2 3 7    0 2 3 7 14").speed(3).random;
i.voices.d.octave=7;
i.voices.d.amp=1/3;

)


```


## Sequencing events:

```SuperCollider


i = INSTRUMENT().play;

i.hihat = i.synths.hihat.choose;

i.every(4,{

	i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.75);

});


```








## Using ProxySpace


One of its main motivations is to easily integrate with the awesome JITLib, that allows musicians to incorporate mutating synthesis onto their live acts.


```SuperCollider

i = INSTRUMENT().play;

p=ProxySpace.push(s);

~z.play;
~z.fadeTime=10;

~z={|freq=200| RLPF.ar(WhiteNoise.ar,freq,0.2) };

i.z=INSTRUMENT(~z);

i.z.seq(\freq,"1000 600 700");

~z.fadeTime=3;
i.z.clock=1/4;
~z={|freq=200| (Resonz.ar(WhiteNoise.ar,Lag2.kr(freq,4),0.01)*30).tanh/2 };

```

```SuperCollider

i = INSTRUMENT().play;

p=ProxySpace.push(s);

~z.play;
~z.fadeTime=10;

~z={|freq=200, t_trig=1| (LFPulse.ar(freq,SinOsc.kr(3).linlin(-1,1,0,1),SinOsc.kr(5).linlin(-1,1,0,1))*30).tanh/2*Decay2.kr(t_trig,2,0) };

i=INSTRUMENT().play;
i.z=INSTRUMENT(~z);

// automatic mapping of note to 'freq and t_trig':
i.z.note("0 2 3");
i.z.clock=1;
i.z.octave=3;

```

```SuperCollider

i = INSTRUMENT().play;

p=ProxySpace.push(s);

~sound.play;
~sound = {|notes=#[60,65,67,72],gain=1| (SinOsc.ar(notes.midicps)*gain).tanh / 6 ! 2 };
~sound = {|notes=#[60,65,67,72],gain=1| (Saw.ar(notes.midicps/2)*gain).tanh / 10 ! 2 };


i.notes=INSTRUMENT(~sound);
i.notes.seq(\gain,[3,1,13]).speed(1/2);


```

## Chord progressions:

```SuperCollider


// Class 'C' is an alias for 'I8TChord':
// C( interval, chordtype/chordarray, inversion, additional );

(

i.notes.chord([
	C(0,\m),
	C(7,\M,1),
	C(3,\Mmaj7),
	C(1,\m,0,[16])
]).speed(1/2);

)

// remove fx sequence
i.notes.rm(\gain);

(

i.notes.chord([
	C(0,\m),
	C(0,\m,1),
	C(0,\m,2),
	C(0,\m,2,[14]),
	C(7,[6,10,16])
]).speed(1/4);

)

// different chord types:
(
	i.notes.chord([

		C(0,\M),
		C(0,\m),
		C(0,\M7),
		C(0,\m7),
		C(0,\dim),
		C(0,\aug),
		C(0,\Mmaj7),
		C(0,\mmaj7),
		C(0,\M9),
		C(0,\M9m),
		C(0,\m9),
		C(0,\m9m),
		C(0,\sus2),
		C(0,\sus4),

		// custom chord type
		C(0,[6,10,15]),

	]).speed(1/2);
)


```

## Sequencing different progressions:

```SuperCollider


(

	i.notes[0].chord([
		C(0,\m),
		C(1,\M,0,[16]),
	]).speed(1/2).do(2);

	i.notes[1].chord([
		C(0,\m),
		C(3,\sus2),
		C(2,\dim),
		C(7,\M)
	]).speed(1/2).do(3);


)


```


## Mixer

INSTRUMENT comes with a virtual Mixer. Any instruments added will automatically create a new mixer channel.

This mixer adds a three-band EQ per channel, as well as a flexible fx chain

```SuperCollider

(
	i=INSTRUMENT().play;

	i.kick="kickElectro";
	i.hihat="hihatElectro";
	i.snare="snareElectro";

	i.kick.seq("1");
	i.snare.seq("1").speed(2);
	i.hihat.seq("1").speed(3);

)


// Mixer EQ

i.mixer.getChannel(\kick).set(\low,0);
i.mixer.getChannel(\kick).set(\low,1);
i.mixer.getChannel(\kick).set(\low,0.5);

i.mixer.getChannel(\hihat).set(\middle,0);
i.mixer.getChannel(\hihat).set(\middle,1);
i.mixer.getChannel(\hihat).set(\middle,0.5);

i.mixer.getChannel(\snare).set(\high,0);
i.mixer.getChannel(\snare).set(\high,1);
i.mixer.getChannel(\snare).set(\high,0.5);


// Mixer FX Chain

i.kick.stop;
i.hihat.stop;

i.mixer.getChannel(\snare).addFx(\lpf);
i.mixer.getChannel(\snare).addFx(\reverb);

i.mixer.getChannel(\snare).fxSet(\reverb,\wet,0);
i.mixer.getChannel(\snare).fxSet(\reverb,\wet,1);
i.mixer.getChannel(\snare).fxSet(\reverb,\wet,1/2);
i.mixer.getChannel(\snare).fxSet(\reverb,\room,0);
i.mixer.getChannel(\snare).fxSet(\reverb,\room,1);
i.mixer.getChannel(\snare).fxSet(\reverb,\damp,1);




i.mixer.getChannel(\snare).fxSet(\reverb,\wet,1);


i.mixer.getChannel(\snare).fxSet(\lpf,\q,0.025);
i.mixer.getChannel(\snare).fxSet(\lpf,\filterHz,1900);
i.mixer.getChannel(\snare).fxSet(\lpf,\filterHz,900);
i.mixer.getChannel(\snare).fxSet(\lpf,\filterHz,500);
i.mixer.getChannel(\snare).fxSet(\lpf,\filterHz,300);


i.mixer.getChannel(\snare).addFx(\gateDistort)
i.mixer.getChannel(\snare).fxSet(\gateDistort,\gain,3**4)

i.mixer.getChannel(\snare).fxSet(\lpf,\filterHz,3000);

i.mixer.getChannel(\snare).removeFx(\gateDistort)

i.snare.stop;

i.stop;

```



## Loopers:

A multi-layer looper

- Time-synced to main sequencer.
- Allows recording of multiple layers.
- Sequenceable amp and rate manipulation.
- Works in the same way as any other INSTRUMENT
	- Sequence any parameter
	- Add FX
	- etc...


```SuperCollider


i=INSTRUMENT().play;


// create looper connected to audio interface's first audio input:
i.loop1=Looper(0);



// record looper for the 1st channel:
i.loop1.rec;


i.loop1.start;


// replace
i.loop1.rec;
i.loop1.start;


i.loop1.rec(1);
i.loop1.start(1);

i.loop1.rec(2);
i.loop1.start(2);

i.loop1.rec(3);
i.loop1.start(3);



i.loop1.amp=0.5;
i.loop1.amp=1;
i.loop1.amp=0;
i.loop1.amp=0.3;
i.loop1.amp=1;
i.loop1.amp_(1);


// sequence amp
i.loop1.seq(\amp,"1 0.2 1 0.5 0.75").speed(2)


i.loop1.rate = 1/2;

i.loop1.rate = -2;
i.loop1.rate([1, 2, -1, \r, 3, \r , 1/2]).speed(1);

// remove rate sequencer:

i.loop1.rm(\rate,0);


i.loop1.rate(1/8);
i.loop1.rate(2.5);



// change rate separately for each of the layers:

i.loop1.rate(1,0);
i.loop1.rate(1.5,1);

i.loop1.rate(3,0);
i.loop1.rate(1/4,1);

i.loop1.rate(4,0);
i.loop1.rate(1/2,0);

i.loop1.rate(1/2,1);
i.loop1.rate(1/4,1);



// add fx:

i.loop1.fx=\reverb;

i.loop1.fxSet(\wet,1);

i.loop1.fxSet(\room,0.7);
i.loop1.fxSet(\damp,0.7);

i.loop2.fxSet(\gain,33.3);

i.loop1.fx=\reverbLPF;
i.loop1.fxSet(\filterHz,200)
i.loop1.fxSet(\filterHz,1200)


i.loop1.amp(0.5,0)
i.loop1.amp(0.5,1)



i.loop1.fx=nil

i.loop1.amp=0;

i.loop1.rate(1);


// create another separate looper:

i.loop2=Looper(0);
i.loop2.rec;
i.loop2.start;
i.loop2.amp=0.5;
i.loop2.rate([1, 2, -1, \r, 3, \r , 1/2]).speed(2);
i.loop2.amp("1 0.3 1 0.5 0 0.1").speed(4)

i.loop2.fx=\distortion;

i.loop2.fxSet(\wet,1/2);

i.loop2.fxSet(\filterHz,1440);


i.loop2.fx=nil

i.loop2.amp(0.5);

i.loop2.rate(-1);


// stop loopers:

i.loop1.stop;
i.loop2.stop;

i.loop1.play;
i.loop1.stop;


```


## MIDI Control:

```SuperCollider

//Docs coming soon...


```




# Harmony

(work in progress)

```SuperCollider

/*
random harmony generator
following simple rules:
- no parallel fifths or octaves in first 2 voices
- when first two do move parallelly,
move third direction in opposite direction
*/

(
s.boot;
s.doWhenBooted({

		Task({


			i=INSTRUMENT().play;

			2.wait;

			// create 3 voices:
			i.voices=(
				v1:i.synths.piano[1],
				v2:i.synths.piano[2],
				v3:i.synths.piano[3],
			);


			h=I8THarmony();
			v=h.generateVoicings();
			h=v.collect(_.collect(Scale.minor.degrees.at(_)));

			i.voices.v1.note(h[0]);
			i.voices.v2.note(h[1]);
			i.voices.v2.note(h[2]);



			// set parameters:

			i.voices.octave=5;




			i.voices.set(\rel,0.3);
			i.voices.v1.set(\rel,4);
			i.voices.v3.set(\rel,1);

			i.voices.v1.fx="reverb.small";
			i.voices.v2.fx="reverb.large";
			i.voices.v3.fx="reverb.medium";

		}).play;


		s.volume=(-12);


})

)

(
i.every(32,{
	var nextScale = Scale.choose;
	h=I8THarmony();
	v=h.generateVoicings();
	h=v.collect(_.collect(nextScale.degrees.at(_)));

	i.voices.v1.note(h[0]);
	i.voices.v2.note(h[1]);
	i.voices.v2.note(h[2]);

	["nextScale", nextScale.name].postln;
});
""
)
```



<a name="synthesizers"></a>

# Synthesizers



## i.synths
### Automatic Synth Loading


INSTRUMENT comes with a group of SynthDefs that are automatically loaded when a new instance of INSTRUMENT is created.

You can access them via **i.synths**


```SuperCollider


i=INSTRUMENT();


i.synths.list;
i.synths.percussion.list;
i.synths.percussion.drums.list;


// all synths accesible using their names:
// (Currently, synthnames are converted to lowercase).
i.synths.kickdeep == i.synths.kickdeep;


// references are created without redundant names:
i.synths.kickdeep==i.synths.percussion.drums.kick.deep;


// Multiple hierarchies support.
i.synths.drums == i.synths.percussion.drums;
i.synths.electro == i.synths.percussion.drums.kits.electro;


// smart organization
i.synths.electro.kick===i.synths.kick.electro;



// numeric indexes are fixed by prefixing an 's'
i.synths.kick.syn.s1===i.synths.kicksyn1;
i.synths.kick.s808===i.synths.kick808;


// numerical indexing based on alphabetical order.
i.synths.kick[0].name
i.synths.kick[1].name
i.synths.kick[2].name
i.synths.kick[3].name

// (number wraps around item size)
(
Task.new({
	30.do{|h|
		[h,i.synths.kick[h].name].postln;
		0.01.wait;
	}
}).play;
)

// choose one Synthdef at random

i.synths.kick.choose.postln;
i.synths.bass.choose.postln;

// a specific SynthDef
i.synths.kick.kickdeep

```

You can load your own SynthDef paths:

```SuperCollider
i.loadSynths("path/to/synths");
```


### Using your own Synthdefs

You can use your own Synthdefs. You need to have an **out** parameter for signal routing.

Optionally, you can make them add themselves to the server by putting them inside INSTRUMENT's **Sounds/Synthdef** folder.


<a name="load-more-synths"></a>
#### Loading More Synths

They are automatically added on startup, but you can manually re-load them, or load any path that contains SynthDefs or folders containing them.


```SuperCollider

i=INSTRUMENT().play;

// load your own path with SynthDefs
i.loadSynths("path/to/your/synthdefs/*");

```


```SuperCollider

Synthdef(\yourSynthdef, {|out=0,amp=1,freq,gate=1|

	//... your synthesis code
	Out.ar( out, /*...*/ ));

}).store;

```

##### Convenient parameters

- **amp** - for setting amplitude manually or via a sequence
- **gate** useful for retriggering and monophonic synths

- **note**
- **freq**

**NOTE:** you can use note or freq interchangeably. When you sequence notes, both parameters are addressed. **note** must be converted to freq using the **.midicps** method.

You can check currently existing Synthdefs [here](https://github.com/punksnotdev/INSTRUMENT/tree/master/SuperCollider/Sounds/SynthDefs)



###### FX Synthdefs


Only requirements are using **inBus**, **outBus**, **wet**, and **amp** params.

See following example:

```SuperCollider


SynthDef(\reverb, {
	arg
	inBus=0,
	outBus=0,
	wet=0.5,
	room=0.3,
	damp=0.3
	amp=1;

	var sig;
	var targetRoom, targetDamp, lag=0.3;
	var dsp, mix;

	sig = In.ar(inBus) * 1.25;

	dsp = FreeVerb.ar( sig, 1, room, damp );

	mix = (sig * (1-wet)) + (dsp * wet);

	ReplaceOut.ar( outBus, Pan2.ar( mix * amp.linlin(0,1,0,1.5), 0 ) );

}).store;

```
