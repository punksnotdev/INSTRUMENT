(


SynthDef(\pitchShift, {

	arg gain=0.3,
	inBus=0,
	outBus=0,
	compGate=1.7,
	lag=1,
	pitch=0.5;


	var sig, env;
	var targetPitch;

	sig = In.ar(inBus);

	// targetPitch = MouseX.kr(0,16);
	//
	// if( pitch.notNil, {
		targetPitch=pitch;
	// });

	sig=PitchShift.ar(
        sig,
        0.1,
        Lag2.kr(targetPitch*2,lag),
        0,                 // pitch dispersion
        0.004            // time dispersion
    );

	Out.ar(outBus,Pan2.ar(sig));


},
variants: (
	overdrive: [gain: 0.1, compGate: 1.5],
	hardcore: [gain: 83, compGate: 3.5],
	extreme: [gain: 3330, compGate: 4.5],
	crunch: [gain: 0.4, compGate: 2.9]
)
).store;

)
