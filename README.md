# INSTRUMENT
### v0.1.alpha.0
#### afterthought




**Importante: Traducción al español en proceso...**


INSTRUMENT is a library for livecoding music (beats, basslines, harmony, looping, FX, signal routing, synthesis, etc.) and interfacing with musical instruments and controllers from inside the SuperCollider environment.


```SuperCollider

// Create a beat

// First, evaluate following block
(

i = INSTRUMENT().play;

i.drums = (
	kick: INSTRUMENT(i.synths.electro.kick),
	hihat: INSTRUMENT(i.synths.electro.hihat),
	clap: INSTRUMENT(i.synths.electro.clap)
);

i.drums.kick.seq("1 :0.5 1xx");
i.drums.hihat.seq(" 1").speed(2);
i.drums.clap.seq(" 1 :2 1 :0.25 1xxx ");

i.drums.clock = 2;


)


// Play with clock

i.drums.clock = 4;

// Add FX and slow down
(
i.drums.fx=\reverbLPF;
i.drums.clock = 1;
)

// Stop

i.drums.stop;

```





# IMPORTANT


INSTRUMENT is now publicly available as an alpha release.

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
- INSTRUMENT is in alpha phase. The API is subject to change in the near future.


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

INSTRUMENT comes with some predefined SynthDefs that you can use.

They are automatically added on startup, but you can manually re-load them, or load any path that contains SynthDefs or folders containing them.


```SuperCollider

i=INSTRUMENT().play;

// load your own path with SynthDefs
i.loadSynths("path/to/your/synthdefs/*");


// creates a dictionary inside **i.synths**

i.synths.drums.kick[0];
i.synths.drums.kick[1];
i.synths.drums.kick.choose;
i.synths.drums.snare.choose;
i.synths.bass.simpleBass;

```



See [**Synthesizers**](#synthesizers), at the end of this document, for more info about working with Synths, Effects, and this automatic loader.




## Basic sequencing:

```SuperCollider


i=INSTRUMENT().play;
i.kick=INSTRUMENT(\kickElectro);
i.kick.seq("1");


// trigger synths with different amp values:
i.kick.seq("1 0.1 0.75");

// this also works
i.kick.seq([1, 0.1, 0.75]);

i.kick.stop;


// Press Ctrl/Cmd + . to stop the server

```

**.seq** method allows to sequence any parameter. In previous example, the default parameter 'trigger' is being automatically passed.

Lets pass a different parameter, *note*.

```SuperCollider

i=INSTRUMENT().play;

i.bass=INSTRUMENT(i.synths.trance.choose);

i.bass.seq(\note, "0 2 3 5");

// you can also use note names:

i.bass.seq(\note, "C D Eb F");

// you can choose octaves

i.bass.seq(\note, "C3 D4 Eb5 F6");


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

i.bass=INSTRUMENT(i.synths.trance.choose);


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

i.kick=INSTRUMENT(i.synths.kick.choose);

i.kick.seq("1   0.5");
// array notation equivalent:
i.kick.seq([1, \r, \r, \r, 0.5]);


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
i.piano=INSTRUMENT(i.synths.piano.rhodes);

i.piano.amp=4;
i.piano.note("0xx 2xxx 3xxxxx");
i.piano.note("0x2 2x3 3x5");

// get last pattern duration:
i.piano.duration;

i.kick=INSTRUMENT(i.synths.electro.kick);

// repeat three times
i.kick.seq("1 0.5x3");

i.kick.duration;
// lazy equivalent
i.kick.seq("1 0.5xxx");

// using different speeds
i.kick.rm(\trigger)

i.kick[0].seq("1");
i.kick[1].seq("1    1");
i.kick[2].seq("1x14  ").speed(16).x(1);

i.kick.seq("1x4 :0.5 1x8 :0.25 1x16 :0.125 1x32");

// add some silences
i.kick.seq("1x6 :0.25 1  1x3  1  1x3");

// check duration

i.kick.duration;


i.bass=INSTRUMENT(i.synths.bassTrance3);
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

i.kick=INSTRUMENT(i.synths.kick.choose);

// decimal representation
i.kick.seq("1  1");

// fraction representation
i.kick.seq("1 :1/4 1x3 ");

i.kick.seq("1x4 :1/4 1x4 :1/8 1x4:2 1x2");




```

Using fractions allow some interesting rhythms:

```SuperCollider

	i=INSTRUMENT().play;

	i.kick=INSTRUMENT(i.synths.kick.choose);
	i.hihat=INSTRUMENT(i.synths.hihat.choose.postln);

	i.kick.seq("1xx  :3/8 1x3  1x2 :1/4 1x3  1x2  :1/2 1xx");
	i.hihat.seq("1xx  :1/3 1x3");

```

## Sequencing patterns

```SuperCollider

// Ctrl/Cmd + Period

i=INSTRUMENT().play;
i.kick=INSTRUMENT(\kickElectro);


i.kick.clock=2;
i.kick[0].seq("1");
i.kick[1].seq("1xx   1");
i.kick[2].seq("1   1xx   1 ").speed(2);

```

## Removing patterns:

```SuperCollider


i.kick.rm(\seq,0);
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



(
	i = INSTRUMENT().play;
	i.hihat=INSTRUMENT(\hihatElectro);
	i.hihat.seq("1xx :0.25 1xxx :0.5 1xxx :2 1").speed(2);
)

i.hihat.go(0);
i.hihat.go(4);


```

## Create a basic beat

```SuperCollider

(


i = INSTRUMENT().play;

i.drums = (
	kick: INSTRUMENT(i.synths.electro.kick),
	hihat: INSTRUMENT(i.synths.electro.hihat),
	clap: INSTRUMENT(i.synths.electro.clap)
);

i.drums.kick.seq("1");
i.drums.hihat.seq(" 1").speed(2);
i.drums.clap.seq(" 1");

i.drums.clock = 2;


)



(
i = INSTRUMENT().play;
i.kick=INSTRUMENT(\kickElectro);
i.hihat=INSTRUMENT(\hihatElectro);
i.clap=INSTRUMENT(\clapElectro);

i.kick.seq("1").speed(2);
i.hihat.seq(" 1").speed(4);
i.clap.seq(" 1").speed(2);
)


```

## Setting parameters

```SuperCollider


(

i = INSTRUMENT().play;

i.bass=INSTRUMENT(\tranceBazz);
i.bass.note("0 2 3");
)

i.bass.set(\rel,2);
i.bass.set(\rel,0.2);
i.bass.set(\gain,0.1);
i.bass.set(\gain,0.01);
i.bass.set(\gain,2);

```


## Sequencing parameters

```SuperCollider




i.bass.seq(\rel,[2,0.2,1]);

// rests in params:
i.bass.seq(\rel,[2,0.2,\r,\r,1]);



```

## Sequencing synthdefs:

```SuperCollider

(
i=INSTRUMENT().play;
i.kick=INSTRUMENT(\kickDeep);
i.kick.seq("1");
i.kick.synthdef([\kickSyn1,\kickSyn2,\kickSyn3]);



i.bass=INSTRUMENT(i.synths.trance.choose);
i.bass.note("0 2 3");
i.bass.synthdef([\bassTrance,\bassTrance2,\bassTrance3]);

)


```

## FX (Effects)

```SuperCollider


(
	i=INSTRUMENT().play;

	i.clap=INSTRUMENT(\clapElectro);

	i.clap.note("0 2 3 5");


	i.clap.octave=3;

)

i.clap.fx=\reverb;

i.clap.fxSet(\wet,0);
i.clap.fxSet(\wet,1);
i.clap.fxSet(\wet,3/4);

i.clap.fxSet(\room,7/8);
i.clap.fxSet(\damp,7/8);

i.clap.fxSet(\room,1/8);
i.clap.fxSet(\damp,1/8);


i.clap.fxSet(\room,1);
i.clap.fxSet(\damp,1);


i.clap.stop;

i.clap.fx=nil;
i.clap.play;

i.clap.fx=\reverb;


```


```

## Sequencing fx parameters

```SuperCollider

(
	i = INSTRUMENT().play;

	i.clap=INSTRUMENT(\clapElectro);
	i.clap.seq(" 1  :0.25 1xx").speed(2);

	i.clap.fx = \reverbLPF;
	i.clap.fxSet([
		(cutoff:3000),
		(cutoff:1000),
		(cutoff:2000),
		// more than one parameter:
		(cutoff:3000,q:0.1),
		(cutoff:300,q:0.01),
	]).speed(1/2);
)


```

## Sequencing fx

```SuperCollider

(
	i = INSTRUMENT().play;

	i.clap=INSTRUMENT(\clapElectro);
	i.clap.seq(" 1  :0.25 1xx").speed(2);

	i.clap.fx([\reverb,\reverbLPF,\delay2]).speed(1/4);
)



```

## Group


You can group Instruments for easier manipulation, while retaining access to individual instruments

```SuperCollider


i = INSTRUMENT().play;


i.drums=(
	kick: INSTRUMENT( i.synths.kick.choose ),
	hihat: INSTRUMENT( i.synths.hihat.choose ),
	snare: INSTRUMENT( i.synths.snare.choose ),
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
	hihat: INSTRUMENT( i.synths.hihat.choose ),
)


// restore

i.drums=(

	kick: INSTRUMENT( i.synths.kick.choose ),
	hihat: INSTRUMENT( i.synths.hihat.choose ),
	snare: INSTRUMENT( i.synths.snare.choose ),
)


// choose a subset of instruments, with a probability ( range: 0 - 1, default 0.5 )

// when probability larger than 0, will always select at least 1 instrument

i.drums.chooseInstrument;
i.drums.chooseInstrument(1);
i.drums.chooseInstrument(0);
i.drums.chooseInstrument(0.2);
i.drums.chooseInstrument(0.8);
i.drums.chooseInstrument(1);


// melodic synths:


i.melodies=(
	note1: INSTRUMENT( i.synths.note.distNote1 ),
	note2: INSTRUMENT( i.synths.note.distNote2 )
);

i.melodies.note1.note("0 7  8");
i.melodies.note2.note("12  15 13");

i.melodies.octave=5;
i.melodies.octave=6;
i.melodies.octave=3;
i.melodies.octave=4;


```


## Array manipulation
```SuperCollider


(

i = INSTRUMENT().play;

i.bass=INSTRUMENT(\tranceBazz);
i.hihat=INSTRUMENT(\hihatElectro);
)

i.bass.note("0 2 3").pyramid.mirror;

// randomness
i.bass.note("0 2 3 5 7 10 12").random;
i.bass.note("0 2 3 5 7 10 12").random;


i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.5); // default value
i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.25);
i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.75);


```


A small composition:

```SuperCollider
// Ctrl/Cmd + .


(

i = INSTRUMENT().play;

i.voices = (

	a: INSTRUMENT(\distPad1),
	b: INSTRUMENT(\distPad2),
	c: INSTRUMENT(\distNote1),
	d: INSTRUMENT(\distNote2),

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

i.every(4,{

	i.hihat.seq("1xxxxxxxxx").speed(8).maybe(0.75);

});


```

## Using ProxySpace


One of its main motivations is to easily integrate with the awesome JITLib, that allows musicians to incorporate mutating synthesis onto their live acts.


```SuperCollider

i = INSTRUMENT().play;

p=ProxySpace.push(s);

~sound.play;
~sound = {|notes=#[60,65,67,72],gain=1| (SinOsc.ar(notes.midicps)*gain).tanh / 4 ! 2 };
~sound = {|notes=#[60,65,67,72],gain=1| (Saw.ar(notes.midicps/2)*gain).tanh / 4 ! 2 };


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

	i.kick=INSTRUMENT(\kickElectro);
	i.hihat=INSTRUMENT(\hihatElectro);
	i.snare=INSTRUMENT(\snareElectro);

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
i.mixer.getChannel(\snare).fxSet(\lpf,\cutoff,1900);
i.mixer.getChannel(\snare).fxSet(\lpf,\cutoff,900);
i.mixer.getChannel(\snare).fxSet(\lpf,\cutoff,500);
i.mixer.getChannel(\snare).fxSet(\lpf,\cutoff,300);


i.mixer.getChannel(\snare).addFx(\gateDistort)
i.mixer.getChannel(\snare).fxSet(\gateDistort,\gain,3**4)

i.mixer.getChannel(\snare).fxSet(\lpf,\cutoff,3000);

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
i.loop1.fxSet(\cutoff,200)
i.loop1.fxSet(\cutoff,1200)


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

i.loop2.fxSet(\cutoff,1440);


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





<a name="synthesizers"></a>

# Synthesizers



### Automatic Synth Loading
INSTRUMENT comes with a group of SynthDefs that you can easily add by running 'Sounds/load-synths.scd';


**loadFunction** will read any folder and create a dictionary with SynthDefs inside its internal folder structure.


```SuperCollider

i.synths = i.loadSynths(Platform.userExtensionDir++"/INSTRUMENT/Sounds/SynthDefs/*");

// Multiple hierarchies support.

i.synths.percussion.drums == i.synths.drums
i.synths.percussion.drums.kick == i.synths.kick
i.synths.percussion.drums.kick.kickDeep == i.synths.kickDeep

// choose one Synthdef at random

i.synths.kick.choose.postln;
i.synths.bass.choose.postln;

// a specific SynthDef
i.synths.kick.kickDeep

```


### Using your own Synthdefs

You can use your own Synthdefs. You need to have an **out** parameter for signal routing.

Optionally, you can make them add themselves to the server by putting them inside INSTRUMENT's **Sounds/Synthdef** folder

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
