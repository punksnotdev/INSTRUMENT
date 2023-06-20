(

SynthDef(\fluteGlitch, {

	arg
	out=0,
	freq=150,
	q=0.25,
	rate=0.3333,
	gate=1,rel=0.2;

	var sig, env, brown;

	brown = BrownNoise.kr;

	sig =  Resonz.ar(WhiteNoise.ar(0.125) + SinOsc.ar(freq,0,1.1).tanh * brown, freq*1.5,
	SinOsc.kr(rate,1/2,q/2,q/2+0.001).abs.linlin(0,1,0.01,10));

	sig=sig.tanh;

	env = EnvGen.kr(Env.perc(0.2, rel),doneAction:2);

	Out.ar(out, (Pan2.ar(sig * env) / 2));

}).store;

)
