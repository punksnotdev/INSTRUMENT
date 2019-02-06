(

SynthDef(\fluteGlitch, {

	arg
	out=0,
	freq=150,
	gate=1;

	var sig, env, brown;

	brown = BrownNoise.kr;

	sig =  BPF.ar(WhiteNoise.ar(0.5) + SinOsc.ar(freq) * brown, freq, 10);

	sig=sig.tanh;

	env = EnvGen.kr(Env.adsr(0.5, 0.05, 1, 3),gate,doneAction:2);

	Out.ar(out, (Pan2.ar(sig * env) / 2));

}).store;

)
