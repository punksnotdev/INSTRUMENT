# INSTRUMENT

afterthought


### Disclaimer:

Documentation is still a work in progress... more news soon.


#### Before you start:

- Load SynthDefs by running 'Sounds/load-synths.scd';


```SuperCollider

(

s.options.memSize=2048*1024;
s.options.maxNodes=128*1024;
s.boot;

)


// basic sequencing:
(
i=INSTRUMENT();
i[\kick]=INSTRUMENT(\kickElectroKit);
i[\kick].seq("1");
i[\kick].seq("1 0.5 0.75");

i[\bass]=INSTRUMENT(\tranceBazz);
i[\bass].note("0 2 3");
)

// set tempo

i.tempo=180;
i.tempo=140;
i.tempo=120;

// add silences
i[\kick].seq("1   0.5");

// change instrument params:

i[\kick].amp=1/2;
i[\kick].amp=3/4;
i[\kick].amp=1;

i[\kick].clock=2;
i[\bass].clock=4;



// repeats
i[\kick].seq("1   0.5xxxx");
i[\bass].note("0 2xx 3xxx");


// change step duration

i[\kick].seq("1 :0.25 1xxx ");

// sequencing patterns

i[\kick][0].seq("1");
i[\kick][1].seq("1 ");
i[\kick][2].seq("1  ");


// removing patterns:

i[\kick].rm(\trigger,0);
i[\kick].rm(\trigger,1);
i[\kick].rm(\trigger,2);



// control pattern speeds

i[\kick][0].seq("1").speed(1);
i[\kick][1].seq("1").speed(2);
i[\kick][2].seq("1").speed(4);


// controlling pattern repetitions

i[\kick][0].seq("1").speed(1).repeat(4);
// different names:
i[\kick][1].seq("1").speed(2).do(8);
i[\kick][2].seq("1").speed(4).x(16);


// jump to position

(
	i = INSTRUMENT();
	i[\hihat]=INSTRUMENT(\hihatElectroKit);
	i[\hihat].seq("1xx :0.25 1xxx :0.5 1xxx :2 1").speed(2);
)

i[\hihat].go(0);
i[\hihat].go(4);


// create a basic beat
(
i = INSTRUMENT();
i[\kick]=INSTRUMENT(\kickElectro);
i[\hihat]=INSTRUMENT(\hihatElectroKit);
i[\clap]=INSTRUMENT(\clapElectroKit);

i[\kick].seq("1").speed(2);
i[\hihat].seq(" 1").speed(4);
i[\clap].seq(" 1").speed(2);
)

// setting parameters

(

i = INSTRUMENT();

i[\bass]=INSTRUMENT(\tranceBazz);
i[\bass].note("0 2 3");
)

i[\bass].set(\rel,2);
i[\bass].set(\rel,0.2);
i[\bass].set(\gain,0.1);
i[\bass].set(\gain,2);




// sequencing parameters

i[\bass].seq(\rel,[2,0.2,1]);

// rests in params:
i[\bass].seq(\rel,[2,0.2,\r,\r,1]);




// seq synthdefs:
(
i=INSTRUMENT();
i[\kick]=INSTRUMENT(\kickDeep);
i[\kick].seq("1");
i[\kick].synthdef([\kickSyn1,\kickSyn2,\kickSyn3]);
)


// grouping INSTRUMENTS

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

// setting fx
i[\clap].fx=\reverb;
// setting fx parameters
i[\clap].fxSet(\wet,1);
i[\clap].fxSet(\rv1,1);
i[\clap].fxSet(\rv2,1);

i[\clap].fx=nil;

i[\drums].fx = \delay2;
i[\drums].fx = nil;

// sequencing fx
i[\clap].fx([\reverb,\distortion,\delay2]).speed(1/4);

// sequencing fx parameters
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

// array manipulation

(

i = INSTRUMENT();

i[\bass]=INSTRUMENT(\tranceBazz);
i[\hihat]=INSTRUMENT(\hihatElectroKit);
)

i[\bass].note("0 2 3").pyramid.mirror;
// random
i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.5); // default value
i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.25);
i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.75);


// sequencing events:
i.every(4,{

	i[\hihat].seq("1xxxxxxxxx").speed(8).maybe(0.75);

});



// controlling proxies

p=ProxySpace.push(s);
~notes.play;
~notes = {|notes=#[60,65,67,72],gain=1| (SinOsc.ar(notes.midicps)*gain).tanh / 4 ! 2 };


i[\notes]=INSTRUMENT(~notes);
i[\notes].seq(\gain,[3,1,13]).speed(1/2);


// CHORD:

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

	]).speed(1/2);
)

// different progressions:

(

	i[\notes][0].chord([		
		C(7,[6,10,16]),
		C(1,\M,0,[16]),
	]).speed(1/2);

	i[\notes][1].chord([
		C(0,\m),
		C(3,\sus2),
		C(0,\m,1),
		C(0,\m,2,[14]),
		C(7,[6,10,16]),
		C(1,\M,0,[16]),
	]).speed(1/2).do(2);


)


// MIDI Control: Docs coming soon...


```
