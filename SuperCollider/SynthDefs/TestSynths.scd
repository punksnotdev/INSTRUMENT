SynthDef(\test,{|note=60,t_trig=0,decay=0.3,distortion=1.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( note.midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh;

	sig = RLPF.ar( sig, cutoff, 0.1);

	Out.ar(0, Pan2.ar( sig, 0 ));
}).writeDefFile;


SynthDef(\bassTest,{|note=60,t_trig=0,decay=0.3,distortion=2.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( (note-[24,12]).midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh;

	sig = RLPF.ar( sig, cutoff, 0.01 );

	Out.ar(0, Pan2.ar( sig, 0 ));
}).writeDefFile;


SynthDef(\bassTest2,{|note=60,t_trig=0,decay=0.3,distortion=2.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( (note-[24,12]).midicps ) * 0.3;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh;

	sig = RLPF.ar( sig, cutoff, 0.3 );

	Out.ar(0, Pan2.ar( sig, 0 ));
}).writeDefFile;
