// http://sccode.org/1-5bV
SynthDef(\reverbDeep, {
	| freq=51.913087197493, amp=0.5, rel=0.28571428571429, spread=0.1, damp=0.6, inBus=0, outBus=0, wet=0.5 |
	var sig, low, high, verb, verbHi, verbLo,in;
	// env.ar is really good for this since thie envelopes are so short

	in = In.ar(inBus);


	verb = FreeVerb.ar(in,mix: 1,room: 1, damp: 1).distort;


	verb = BLowPass.ar(verb, 400+freq,2);

	// Reduce stereo spread, and balance audio to be level no matter the spread (more or less)
	verbHi = BHiPass.ar(verb, 200);
	// Keep low frequencies in mono
	verbLo = BLowPass.ar(verb, 200);
	verb = Compander.ar(verb,verb, thresh: 0.5,
		slopeBelow: 1.75, slopeAbove: 0.75,
		clampTime: 0.002, relaxTime: 0.1, mul: 0.25);
	// finalize signals
	in = (in*2).tanh;
	sig = (in*(1-wet))+(verb*wet);
	sig = Compander.ar(sig,sig, thresh: 0.2,
		slopeBelow: 0.59, slopeAbove: 1,
		clampTime: 0.002, relaxTime: 0.1, mul: 1);
	ReplaceOut.ar(outBus, (sig*2.3).tanh);
}).store;
