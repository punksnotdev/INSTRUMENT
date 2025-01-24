SynthDef(\simpleCompressor, {
	arg inBus=0, outBus=0, wet=1;

	var sig = In.ar(inBus, 2);

	var dsp = CompanderD.ar(
		sig,
		thresh: (-3).dbamp,
		slopeBelow: 1,
		slopeAbove: 1/3,
		clampTime: 0.01,
		relaxTime: 0.1,
		mul: 1.5
	);

	var mix = (sig * (1-wet)) + (dsp * wet)/4;

	ReplaceOut.ar(outBus, sig);

}).store;


