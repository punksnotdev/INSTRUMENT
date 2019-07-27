(

SynthDef(\fluteGlitch, {

	arg
	out=0,
	freq=150,
	q=0.9,
	gate=1,rel=3;

	var sig, env, brown;

	brown = BrownNoise.kr;

	sig =  Resonz.ar(WhiteNoise.ar(0.5) + SinOsc.ar(freq) * brown, freq,
	SinOsc.kr(1/3,1/2).abs.linlin(0,1,0.01,10));

	sig=sig.tanh;

	env = EnvGen.kr(Env.perc(0.2, rel),doneAction:2);

	Out.ar(out, (Pan2.ar(sig * env) / 2));

}).store;

)
