(

	SynthDef(\test,{|note=60,t_trig=0|
		var sig, env;
		sig = SinOsc.ar( note.midicps );
		env = EnvGen.kr(Env.perc,t_trig);
		sig = sig * env;
		Out.ar(0, Pan2.ar( sig, 0 ));
	}).store;

	g = NodeGraph.new;

	q = g.sequencer;

	i = Instrument.new("test1");

	i.synth = Synth(\test);

	i.seq([0,2,3,5],[\repeat,2,\speed,4]);

	i.seq([7,8,10,12],[\repeat,4,\speed,8]);

	i.seq([15,17,19,20],[\repeat,2,\speed,2]);

	q.instruments[i.name].speed = 8;

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
