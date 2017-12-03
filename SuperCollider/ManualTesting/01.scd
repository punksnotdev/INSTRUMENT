(

	SynthDef(\test,{|note=60,t_trig=0,decay=0.3,distortion=3,cutoff=15000|
		var sig, env;
		sig = SinOsc.ar( note.midicps );
		env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
		sig = sig * env;
		sig = (sig * distortion).tanh;

		sig = RLPF.ar( sig, cutoff, 0.1);

		Out.ar(0, Pan2.ar( sig, 0 ));
	}).store;

	g = NodeGraph.new;

	q = g.sequencer;
	q.play;

	i = Instrument.new("test1");
	i.synth = Synth(\test);

	i.seq(P(\note,[0,2,3,5]));
	// i.seq(P(\note,[12,2,3,5]),1,[\speed,2]);
	i.seq(P(\note,[6,2,4,5]));
	// i.seq(P(\note,[7,2,3,5]),2);
	// i.seq(P(\decay,[1,4,7,30]/5),2,[\speed,0.5]);
	// i.seq(P(\distortion,[2,1114,7,110]));
	// i.seq(P(\cutoff,[2,0.4,6,0.1,12]*1000),[\speed,0.5]);
	// i.removePattern(P(\distortion,[2,1114,7,110]));
	// i.removePattern(P(\cutoff,[2,0.4,6,0.1,12]*1000),[\speed,0.5]);


	// i.removePattern([12,2,3,5]);
	i.removePattern([6,2,4,5]);


	// i.getPattern([0,2,3,5]).postln;
	// i.getPattern([4,2,3,5]).postln;

	// i.seq([12,7,8,1],[\repeat,3,\speed,2]);
	// i.seq([10,8,6,7],[\speed,8]);
	// i.seq([12,7,8,1],[\repeat,2]);

	q.instruments[i.name].speed = 8;

	i.play;


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
