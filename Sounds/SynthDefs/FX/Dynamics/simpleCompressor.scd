SynthDef(\simpleCompressor, {

	arg
	inBus=0, outBus=0, wet=1;

	var sig = In.ar(inBus);


	var dsp = CompanderD.ar(
		sig,
		0.9,
		1,
		0.8
	);

	var mix = (sig * (1-wet)) + (dsp * wet);

	ReplaceOut.ar( outBus, Pan2.ar( mix, 0 ) );

}).store;
