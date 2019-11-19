(

	SynthDef(\gateDistort, {

		arg gain=3.3,
		inBus=0,
		outBus=0,
		compGate=1.7,
		lag=0.3;


		var sig, env;

		sig = In.ar(inBus);

		sig = CompanderD.ar(sig,0.3,compGate);

		sig = (sig*((Lag2.kr(gain,lag))*20));

		sig = CompanderD.ar( sig, 0.4,0.6,0.7,0.01,0.3).distort.tanh/1.5;

		ReplaceOut.ar(outBus,Pan2.ar(sig));


	},
	variants: (
		overdrive: [gain: 3.1, compGate: 1.5],
		hardcore: [gain: 83, compGate: 3.5],
		extreme: [gain: 3330, compGate: 4.5],
		crunch: [gain: 1.4, compGate: 2.9]
	)
).store;

)
