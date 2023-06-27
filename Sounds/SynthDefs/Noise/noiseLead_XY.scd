(
SynthDef(\noiseLead_XY, {

	arg
	out=0,
	freq=220,
	pan=0,
	amp=1, att=0.001, sus=0.05, rel=1.6;

	var sig, env;

	sig =  Resonz.ar( GrayNoise.ar(0.5) + SinOsc.ar(freq, 0, 0.5) * MouseX.kr(1, 1000, 50), freq*[1,5],MouseY.kr(0.001,0.1,0.4,0.5))*100 ;

	sig = Mix.new(sig.distort);
	
	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar(out, Pan2.ar( sig * Lag2.kr(amp,1/3), pan) / 2);

}).store;
)
