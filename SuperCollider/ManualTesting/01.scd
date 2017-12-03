(

	SynthDef(\test,{|note=60,t_trig=0|
		var sig, env;
		sig = SinOsc.ar( note.midicps );
		env = EnvGen.kr(Env.perc,t_trig);
		sig = sig * env;
		sig = (sig * 3).tanh;
		Out.ar(0, Pan2.ar( sig, 0 ));
	}).store;

	g = NodeGraph.new;

	q = g.sequencer;

	i = Instrument.new("test1");
	i.synth = Synth(\test);


	// i.seq([0,2,3,5]);
	// i.seq([12,7,8,1]);
	// i.seq([10,9,11]);
	i.seq([0,2,3,5]);
	i.seq([12,7,8,1],[\repeat,3,\speed,2]);
	i.seq([10,8,6,7],[\speed,8]);
	i.seq([12,7,8,1],[\repeat,2]);

	// i.seq([0,3],[\repeat,2,\speed,3]);
	// i.seq([2,3],[\repeat,4,\speed,6]);
	// i.seq([7,8],[\repeat,3,\speed,3]);

	q.instruments[i.name].speed = 4;

	q.play;
    //
	i.play;

	// g.sequencer.instruments[i.name].currentEvent.postln;


)



// (
//
// 	g = NodeGraph.new;
//
// 	g.sequencer.play;
//
// 	i = Instrument.new("test1");
//
// 	i.play;
//
// 	j = Instrument.new("test2");
// 	j.speed = 3;
// 	j.play;
//
// )
