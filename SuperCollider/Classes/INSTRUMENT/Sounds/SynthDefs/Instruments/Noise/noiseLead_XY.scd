(
SynthDef(\noiseLead_XY, {

	arg
	out=0,
	freq=220,
	pan=0,
	amp=1;

	var sig;

	sig =  Resonz.ar( GrayNoise.ar(0.5) + SinOsc.ar(freq, 0, 0.5) * MouseX.kr(1, 1000, 50), freq*[1,5],MouseY.kr(0.001,0.1,0.4,0.5))*100 ;

	sig = Mix.new(sig.distort);

	Out.ar(out, Pan2.ar( sig * amp, pan) / 2);

}).store;
)
