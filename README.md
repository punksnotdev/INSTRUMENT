# INSTRUMENT
### v0.1.alpha.0
#### afterthought

# IMPORTANT / IMPORTANTE

#### English:

INSTRUMENT is now publicly available as an alpha-release.

Please try this tool and [get in touch](mailto:furenku@gmail.com).

Expect turbulence in this repository while this test phase evolves.




# Tutorial



INSTRUMENT is a library for musical live-coding inside SuperCollider.

It provides a simple API useful for the creation of musical compositions from scratch, 'on the fly'. INSTRUMENT focuses on musical language: rhythm, harmony, melody, audio processing.

One of its main motivations is to easily integrate with the awesome JITLib, that allows musicians to incorporate mutating synthesis onto their live acts.

INSTRUMENT also enables the connection of MIDI controllers with an easy interface that facilitates its mapping to different sound parameters.

It's name is inspired by the famous documentary about Fugazi, furenku's favorite band.

INSTRUMENT is a voluntary independent open source initiative operating from the Mexican live coding underground

For questions, inquiries, help, or fun conversations, please contact me at furenku@gmail.com.


## Disclaimers:

- Documentation is still a work in progress... more news soon.
- INSTRUMENT is not officially released yet: the API is subject to change in the near future
before the v.1 release.


# Installation:

- Clone the repo or download the .zip file and place it inside your SuperCollider 'Extensions/' folder. Recompile.
- Load SynthDefs by running 'Sounds/load-synths.scd';
- You can check currently existing Synthdefs [here](https://github.com/punksnotdev/INSTRUMENT/tree/master/SuperCollider/Sounds/SynthDefs)




Evaluate following code lines or groups one by one:


```SuperCollider

// First, boot
(
// useful snippet for increasing default memory

s.options.memSize=2048*1024;
s.options.maxNodes=128*1024;
s.boot;

)

```


## Start INSTRUMENT

```SuperCollider

i = INSTRUMENT().play;

```


## Synths


First, lets load the included synths.

Two options:

1. Automatic Synth Loader. (Still experimental)
2. Manual Evaluation. Get them from [this link](https://github.com/punksnotdev/INSTRUMENT/tree/master/Sounds/ManualEvaluation)


```SuperCollider

i=INSTRUMENT().play;

i.synths = i.loadSynths(Platform.userExtensionDir++"/INSTRUMENT/Sounds/SynthDefs/*");

// creates a dictionary

i.synths.drums.kick.choose;

```



See [**Synthesizers**](#synthesizers), at the end of this document, for more info about working with Synths, Effects, and this automatic loader.




## Basic sequencing:

```SuperCollider

s.volume=(-12)
(
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

i.bass=INSTRUMENT(\tranceBazz);

i.bass.seq(\note, "0 2 3 5");


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

i.bass=INSTRUMENT(\tranceBazz);

i.bass.note("0 2 3 5");


i.bass.octave=3;

)



## Set tempo

```SuperCollider


i.tempo=180;
i.tempo=140;
i.tempo=120;

```

## Add silences

```SuperCollider

i.kick.seq("1   0.5");
// array notation equivalent:
i.kick.seq([1, \r, \r, \r, 0.5]);


```

## Change instrument parameters:

```SuperCollider


i.kick.amp=1/2;
i.kick.amp=3/4;
i.kick.amp=1;

i.kick.clock=2;
i.bass.clock=4;

```

## Repeating events:


The 'x' operator inside string Patterns allow for repetition of last value

```SuperCollider

i=INSTRUMENT().play;

i.kick.seq("1   0.5xxxx");

i.bass.note("0 2xx 3xxx");


```
## Changing steps duration


The ':duration' operator inside string Patterns allow for setting of duration of following values

```SuperCollider

// all subsequent events are affected

i=INSTRUMENT().play;

i.kick.seq("1 :0.25 1xxx ");
i.kick.seq("1 :0.25 1xxx :0.125 1xxx ");


```

## Sequencing patterns

```SuperCollider

// Ctrl/Cmd + Period

i=INSTRUMENT().play;
i.kick=INSTRUMENT(\kickElectro);


i.kick.clock=2;
i.kick[0].seq("1");
i.kick[1].seq("1xx  1");
i.kick[2].seq("1  1xx  1 ").speed(2);

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
	i.hihat=INSTRUMENT(\hihatElectroKit);
	i.hihat.seq("1xx :0.25 1xxx :0.5 1xxx :2 1").speed(2);
)

i.hihat.go(0);
i.hihat.go(4);


```

## Create a basic beat

```SuperCollider

(
i = INSTRUMENT().play;
i.kick=INSTRUMENT(\kickElectro);
i.hihat=INSTRUMENT(\hihatElectroKit);
i.clap=INSTRUMENT(\clapElectroKit);

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

	i.clap=INSTRUMENT(\clapElectroKit);
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

	i.clap=INSTRUMENT(\clapElectroKit);
	i.clap.seq(" 1  :0.25 1xx").speed(2);

	i.clap.fx([\reverb,\reverbLPF,\delay2]).speed(1/4);
)



```

## Grouping INSTRUMENTS

```SuperCollider


(
i = INSTRUMENT().play;

i.kick=INSTRUMENT(\kickElectro);
i.hihat=INSTRUMENT(\hihatElectroKit);
i.clap=INSTRUMENT(\clapElectroKit);

i.kick.seq("1xx :0.25 1xxx ").speed(2);
i.hihat.seq(" 1").speed(4);
i.clap.seq(" 1  :0.25 1xx").speed(2);


i.drums=[\kick,\hihat,\clap];

i.drums.clock=1/2;
i.drums.clock=2;
i.drums.clock=1;
i.drums.amp=1/2;
i.drums.amp=1;
)

// add fx to group
i.drums.fx = \delay2;
i.drums.fx = nil;


```

## Array manipulation

```SuperCollider


(

i = INSTRUMENT().play;

i.bass=INSTRUMENT(\tranceBazz);
i.hihat=INSTRUMENT(\hihatElectroKit);
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

## Controlling NodeProxies

```SuperCollider

i = INSTRUMENT().play;

p=ProxySpace.push(s);
~sound.play;
~sound = {|notes=#[60,65,67,72],gain=1| (SinOsc.ar(notes.midicps)*gain).tanh / 4 ! 2 };


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





## Mixer


(
	i=INSTRUMENT().play;

	i.kick=INSTRUMENT(\kickElectro);
	i.hihat=INSTRUMENT(\hihatElectro);
	i.snare=INSTRUMENT(\snareElectro);

	i.kick.seq("1");
	i.snare.seq("1").speed(2);
	i.hihat.seq("1").speed(3);


	i.hihat.octave=3;

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


```

## Loopers:

```SuperCollider


i=INSTRUMENT().play;


// create looper connected to audio interface's first audio input:
i.loop1=Looper(0);

//

// record looper for the 1st channel:
i.loop1.rec;
i.loop1.start;

i.loop1.fx=\reverb;
i.loop1.fxSet(\room,3/4);

// replace
i.loop1.rec;
i.loop1.start;

i.loop1.stop(2);

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


// sequence amp
i.loop1.amp("1 0.3 1 0.5 0 0.1")


i.loop1.rate = 1/2;

i.loop1.rate = -1;
i.loop1.rate([1, 2, -1, \r, 3, \r , 1/2]).speed(1);

// remove rate sequencer:

i.loop1.rm(\rate,0);


i.loop1.rate(1/8);
i.loop1.rate(2.5);


// record another layer
i.loop1.rec;
i.loop1.start;


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

i.loop1.fxSet(\rv1,1);
i.loop1.fxSet(\rv2,1);

i.loop2.fxSet(\gain,33.3);

i.loop1.fx=\revlpf;
i.loop1.fxSet(\cutoff,200)
i.loop1.fxSet(\cutoff,1200)


i.loop1.amp(0.5,0)
i.loop1.amp(0.5,1)



i.loop1.fx=nil

i.loop1.amp(0.5);

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
