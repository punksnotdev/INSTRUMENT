(


SynthDef(\pitchShift, {

	arg gain=0.3,
	inBus=0,
	outBus=0,
	compGate=1.7;


	var sig, env;

	sig = In.ar(inBus);

	sig=PitchShift.ar(
        sig,    // stereo audio input
        0.1,             // grain size
        MouseX.kr(0,3),    // mouse x controls pitch shift ratio
        0,                 // pitch dispersion
        0.004            // time dispersion
    );

	ReplaceOut.ar(outBus,Pan2.ar(sig));


},
variants: (
	overdrive: [gain: 0.1, compGate: 1.5],
	hardcore: [gain: 83, compGate: 3.5],
	extreme: [gain: 3330, compGate: 4.5],
	crunch: [gain: 0.4, compGate: 2.9]
)
).store;

)
