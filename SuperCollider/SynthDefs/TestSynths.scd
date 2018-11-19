SynthDef(\test,{|freq=300,t_trig=0,decay=0.3,distortion=1.3,cutoff=1500,out=0|
	var sig, env;
	sig = SinOsc.ar( freq ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh/2;

	sig = RLPF.ar( sig, cutoff, 0.1);

	Out.ar(out, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\bass,{|amp=0.5,freq=300,t_trig=0,decay=0.3,distortion=33.3,cutoff=1500,q=0.1,out=0|
	var sig, env;
	sig = SinOsc.ar( freq/[2,4] ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env* amp;
	sig = (sig * distortion).tanh/4;

	sig = RLPF.ar( sig / 2, cutoff, q );

	Out.ar(out, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\bassHard,{|amp=0.5,freq=300,t_trig=1,decay=0.4,distortion=12.3,cutoff=1500,q=0.01,out=0|
	var sig, env;
	sig = SinOsc.ar( freq/[2,4] ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env* amp;
	sig = (sig/2 * distortion).tanh/4;

	sig = RLPF.ar( sig, cutoff, q );

	Out.ar(out, Pan2.ar( sig, 0 ));
}).store;
