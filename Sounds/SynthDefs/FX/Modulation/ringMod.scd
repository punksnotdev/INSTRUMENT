(
SynthDef(\ringMod, {

	var sig, rmod, freq=700, inBus=0, outBus=0;

	sig = In.ar(inBus);

	rmod = sig * SinOsc.ar(freq);

	Out.ar(outBus, Pan2.ar( rmod, 0 ));

}).store;
)
