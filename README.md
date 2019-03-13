# INSTRUMENT
### v0.1.prealpha.0
#### afterthought
##### v.0.2


- [Disclaimers:](#disclaimers)
- [Installation:](#installation)
- [Quick Tutorial](#tutorial)
	- [Basic sequencing:](#basic-sequencing)
	- [Set tempo](#set-tempo)
	- [Add silences](#add-silences)
	- [Change instrument parameters:](#change-instrument-parameters)
	- [Repeating events:](#repeating-events)
	- [Changing steps duration](#changing-steps-duration)
	- [Sequencing patterns](#sequencing-patterns)
	- [Removing patterns:](#removing-patterns)
	- [Control pattern speeds](#control-pattern-speeds)
	- [Controlling pattern repetitions](#controlling-pattern-repetitions)
	- [Jump to position](#jump-to-position)
	- [Create a basic beat](#create-a-basic-beat)
	- [Setting parameters](#setting-parameters)
	- [Sequencing parameters](#sequencing-parameters)
	- [Sequencing synthdefs:](#sequencing-synthdefs)
	- [Effects (FX)](#effects-fx)
	- [Sequencing fx parameters](#sequencing-fx-parameters)
	- [Sequencing fx](#sequencing-fx)
	- [Grouping INSTRUMENTS](#grouping-instruments)
	- [Array manipulation](#array-manipulation)
	- [Sequencing events:](#sequencing-events)
	- [Controlling NodeProxies](#controlling-nodeproxies)
	- [Chord progressions:](#chord-progressions)
	- [Sequencing different progressions:](#sequencing-different-progressions)
	- [Loopers:](#loopers)
	- [MIDI Control:](#midi-control)
	- [Synthesizers](#synthesizers)

<!-- /TOC -->


# Quick Tutorial



INSTRUMENT is a library for musical live-coding inside the SuperCollider environment.

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


<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->



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


## Basic sequencing:

```SuperCollider


(
i=INSTRUMENT();
i[\kick]=INSTRUMENT(\kickElectroKit);
i[\kick].seq("1");
// trigger synths with different amp values:
i[\kick].seq("1 0.5 0.75");

// this also works
i[\kick].seq([1, 0.5, 0.75]);

i[\bass]=INSTRUMENT(\tranceBazz);
// play notes
i[\bass].note("0 2 3");
)


```

## Set tempo

```SuperCollider


i.tempo=180;
i.tempo=140;
i.tempo=120;

```

## Add silences

```SuperCollider

i[\kick].seq("1   0.5");
// array notation equivalent:
i[\kick].seq([1, \r, \r, \r, 0.5]);


```

## Change instrument parameters:

```SuperCollider


i[\kick].amp=1/2;
i[\kick].amp=3/4;
i[\kick].amp=1;

i[\kick].clock=2;
i[\bass].clock=4;

```

## Repeating events:

```SuperCollider

i[\kick].seq("1   0.5xxxx");
i[\bass].note("0 2xx 3xxx");


```
## Changing steps duration

```SuperCollider

// al subsequent events are afected
i[\kick].seq("1 :0.25 1xxx ");
i[\kick].seq("1 :0.25 1xxx :0.125 1xxx ");


```

## Sequencing patterns

```SuperCollider

i[\kick][0].seq("1");
i[\kick][1].seq("1 ");
i[\kick][2].seq("1  ");

```

## Removing patterns:

```SuperCollider


i[\kick].rm(\trigger,0);
i[\kick].rm(\trigger,1);
i[\kick].rm(\trigger,2);


```

## Control pattern speeds

```SuperCollider


i[\kick][0].seq("1").speed(1);
i[\kick][1].seq("1").speed(2);
i[\kick][2].seq("1").speed(4);


```

## Controlling pattern repetitions

```SuperCollider

i[\kick][0].seq("1").speed(1).repeat(4);
// different names for the same function:
i[\kick][1].seq("1").speed(2).do(8);
i[\kick][2].seq("1").speed(4).x(16);


```

## Jump to position

```SuperCollider



(
	i = INSTRUMENT();
	i[\hihat]=INSTRUMENT(\hihatElectroKit);
	i[\hihat].seq("1xx :0.25 1xxx :0.5 1xxx :2 1").speed(2);
)

i[\hihat].go(0);
i[\hihat].go(4);


```

## Create a basic beat

```SuperCollider

(
i = INSTRUMENT();
i[\kick]=INSTRUMENT(\kickElectro);
i[\hihat]=INSTRUMENT(\hihatElectroKit);
i[\clap]=INSTRUMENT(\clapElectroKit);

i[\kick].seq("1").speed(2);
i[\hihat].seq(" 1").speed(4);
i[\clap].seq(" 1").speed(2);
)


```

## Setting parameters

```SuperCollider


(

i = INSTRUMENT();

i[\bass]=INSTRUMENT(\tranceBazz);
i[\bass].note("0 2 3");
)

i[\bass].set(\rel,2);
i[\bass].set(\rel,0.2);
i[\bass].set(\gain,0.1);
i[\bass].set(\gain,2);

```


## Sequencing parameters

```SuperCollider




i[\bass].seq(\rel,[2,0.2,1]);

// rests in params:
i[\bass].seq(\rel,[2,0.2,\r,\r,1]);



```

## Sequencing synthdefs:

```SuperCollider

(
i=INSTRUMENT();
i[\kick]=INSTRUMENT(\kickDeep);
i[\kick].seq("1");
i[\kick].synthdef([\kickSyn1,\kickSyn2,\kickSyn3]);
)


```

## Effects (FX)

```SuperCollider


i[\clap].fx=\reverb;
// setting fx parameters
i[\clap].fxSet(\wet,1);
i[\clap].fxSet(\rv1,1);
i[\clap].fxSet(\rv2,1);

i[\clap].fx=nil;


```

## Sequencing fx parameters

```SuperCollider

(
	i = INSTRUMENT();

	i[\clap]=INSTRUMENT(\clapElectroKit);
	i[\clap].seq(" 1  :0.25 1xx").speed(2);

	i[\clap].fx = \revlpf;
	i[\clap].fxSet([
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

i[\clap].fx([\reverb,\distortion,\delay2]).speed(1/4);



```

## Grouping INSTRUMENTS

```SuperCollider


(
i = INSTRUMENT();

i[\kick]=INSTRUMENT(\kickElectro);
i[\hihat]=INSTRUMENT(\hihatElectroKit);
i[\clap]=INSTRUMENT(\clapElectroKit);

i[\kick].seq("1xx :0.25 1xxx ").speed(2);
i[\hihat].seq(" 1").speed(4);
i[\clap].seq(" 1  :0.25 1xx").speed(2);


i[\drums]=[\kick,\hihat,\clap];

i[\drums].clock=1/2;
i[\drums].clock=2;
i[\drums].clock=1;
i[\drums].amp=1/2;
i[\drums].amp=1;
)

// add fx to group
i[\drums].fx = \delay2;
i[\drums].fx = nil;


```

## Array manipulation

```SuperCollider


(

i = INSTRUMENT();

i[\bass]=INSTRUMENT(\tranceBazz);
i[\hihat]=INSTRUMENT(\hihatElectroKit);
)

i[\bass].note("0 2 3").pyramid.mirror;

// randomness
i[\bass].note("0 2 3 5 7 10 12").random;
i[\bass].note("0 2 3 5 7 10 12").random;


i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.5); // default value
i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.25);
i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.75);


```

## Sequencing events:

```SuperCollider

i.every(4,{

	i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.75);

});


```

## Controlling NodeProxies

```SuperCollider


p=ProxySpace.push(s);
~sound.play;
~sound = {|notes=#[60,65,67,72],gain=1| (SinOsc.ar(notes.midicps)*gain).tanh / 4 ! 2 };


i[\notes]=INSTRUMENT(~sound);
i[\notes].seq(\gain,[3,1,13]).speed(1/2);


```

## Chord progressions:

```SuperCollider


// Class 'C' is an alias for 'I8TChord':
// C( interval, chordtype/chordarray, inversion, additional );

(

i[\notes].chord([
	C(0,\m),
	C(0,\m,1),
	C(0,\m,2),
	C(0,\m,2,[14]),
	C(7,[6,10,16])
]).speed(1/4);

)

// different chord types:
(
	i[\notes].chord([

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

	i[\notes][0].chord([
		C(0,\m),
		C(1,\M,0,[16]),
	]).speed(1/2).do(2);

	i[\notes][1].chord([
		C(0,\m),
		C(3,\sus2),
		C(2,\dim),
		C(7,\M)
	]).speed(1/2).do(3);


)


```

## Loopers:

```SuperCollider




i=INSTRUMENT();

// create looper connected to audio interface's first audio input:

// record looper for the 1st channel:
i[\loop1]=Looper(0);

//

i[\loop1].rec;

i[\loop1].start;


i[\loop1].amp=0.5;
i[\loop1].amp=1;
i[\loop1].amp=0;
i[\loop1].amp=0.3;
i[\loop1].amp=1;


// sequence amp
i[\loop1].amp("1 0.3 1 0.5 0 0.1")


i[\loop1].rate = 1/2;

i[\loop1].rate = -1;
i[\loop1].rate([1, 2, -1, \r, 3, \r , 1/2]).speed(1);

// remove rate sequencer:

i[\loop1].rm(\rate,0);


i[\loop1].rate(1/8);
i[\loop1].rate(2.5);


// record another layer
i[\loop1].rec;
i[\loop1].start;


// change rate separately for each of the layers:

i[\loop1].rate(1,0);
i[\loop1].rate(1.5,1);

i[\loop1].rate(3,0);
i[\loop1].rate(1/4,1);

i[\loop1].rate(4,0);
i[\loop1].rate(1/2,0);

i[\loop1].rate(1/2,1);
i[\loop1].rate(1/4,1);



// add fx:

i[\loop1].fx=\reverb;

i[\loop1].fxSet(\wet,1);

i[\loop1].fxSet(\rv1,1);
i[\loop1].fxSet(\rv2,1);

i[\loop2].fxSet(\gain,33.3);

i[\loop1].fx=\revlpf;
i[\loop1].fxSet(\cutoff,200)
i[\loop1].fxSet(\cutoff,1200)


i[\loop1].amp(0.5,0)
i[\loop1].amp(0.5,1)



i[\loop1].fx=nil

i[\loop1].amp(0.5);

i[\loop1].rate(1);


// create another separate looper:

i[\loop2]=Looper(0);
i[\loop2].rec;
i[\loop2].start;
i[\loop2].amp=0.5;
i[\loop2].rate([1, 2, -1, \r, 3, \r , 1/2]).speed(2);
i[\loop2].rm(\rate,0);
i[\loop2].amp("1 0.3 1 0.5 0 0.1").speed(4)
i[\loop2].rm(\amp,0);
i[\loop2].rm(\amp,0);
i[\loop2].rate = 1/3;
i[\loop2].rate = -1;
i[\loop2].rate(1/2);
i[\loop2].rate(-3);
i[\loop2].rec;
i[\loop2].start;
i[\loop2].rate(1,0);
i[\loop2].rate(4,1);
i[\loop2].rate(2/3,1);
i[\loop2].rate(5/4,1);
i[\loop2].fx=\distortion;
i[\loop2].fxSet(\wet,1/2);
i[\loop2].amp(0.01);
i[\loop2].fxSet(\cutoff,1440);
i[\loop2].fx=nil
i[\loop2].amp(0.5);
i[\loop2].rate(1);


// stop loopers:

i[\loop1].stop;
i[\loop2].stop;

```
## MIDI Control:

```SuperCollider

//Docs coming soon...


```






## Synthesizers

INSTRUMENT comes with a group of SynthDefs that you can easily add by running 'Sounds/load-synths.scd';

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
