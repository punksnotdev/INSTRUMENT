(

	SynthDef(\test,{|note=60,t_trig=0,decay=0.3,distortion=1.3,cutoff=1500|
		var sig, env;
		sig = SinOsc.ar( note.midicps ) * 0.1;
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

	q.instruments[i.name].speed = 2;

	i.play;


)

i.seq(P(\cutoff,[2,4,6,1,1]*100),0);
