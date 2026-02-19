// based on http://sccode.org/1-5bV
SynthDef(\technokick, {
	| freq=51.913087197493, amp=0.5, rel=0.28571428571429, out=0, click=0.008, spread=0.1, damp=0.6 gate=1 |
	var sig, env1, aEnv, env2, wEnv, vEnv, low, high, verb, verbHi, verbLo;
	// env.ar is really good for this since thie envelopes are so short
	env1 = EnvGen.ar(Env.adsr(0,0.1,0,0, curve: -4, bias: 1)); // initial attack
	env2 = EnvGen.ar(Env.adsr(0,click,0,0,32, bias:1)); // click adder
	aEnv = EnvGen.kr(Env.perc(0.005, rel/1.75, curve: 2)); // amplitude
	wEnv = EnvGen.ar(Env.adsr(0,0.01,0,0, curve: -4));

	vEnv = EnvGen.kr(Env.adsr(rel, rel/2, 1, 0.01, curve: 3),
		gate, doneAction: 2); // for reverb
	// separate signals for extra processing
	low  = SinOsc.ar(freq * env1 * env2);
	high = WhiteNoise.ar() * wEnv * 0.6;
	high = BBandPass.ar(high, 1000, 2);
	sig = Mix.new([low, high]) * aEnv;
	sig = (sig*2).tanh;
	// sig = sig * 0.25;
	// equalizer
	sig = BPeakEQ.ar(sig, 5500, db: 10);
	sig = BPeakEQ.ar(sig, 360, db: -12, rq: 2);
	sig = BLowShelf.ar(sig, 300, db: 6).distort;
	// compression
	sig = CompanderD.ar(sig, thresh: 0.5,
		slopeBelow: 1.75, slopeAbove: 0.75,
		clampTime: 0.002, relaxTime: 0.1, mul: 5);
	// // finalize sig signal
	// sig = Pan2.ar(sig * aEnv);
    //

	sig = CompanderD.ar(sig, thresh: 0.5,
		slopeBelow: 0.59, slopeAbove: 1,
		clampTime: 0.002, relaxTime: 0.1, mul: 1);

	Out.ar(out, Pan2.ar(sig,0));

}).store;
