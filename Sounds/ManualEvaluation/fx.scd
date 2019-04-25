SynthDef(\reverbLPF, { arg inBus=0, outBus=0, cutoff=1500,q=0.075, room=0.4,damp=0.6, wet=0.5, lag=2;
    ReplaceOut.ar(
        outBus,(

            (In.ar(inBus) * (1-wet))+
            (RLPF.ar(
                (FreeVerb.ar(In.ar(inBus),room,damp)*3).tanh, Lag2.kr(cutoff,lag), q
            )*wet

        )*0.75);
    )
}).store;


SynthDef(\shineDestroy, {

	arg
	delay=1,
	gain=3.3,
	freq=440,
	t_trig=1,
	cutoff=3500,
	res=10,
	att=1,
	decay=3,
	rel=6,
	inBus=0,
	outBus=0,
	lag=3;


	var sig, env;

	sig = In.ar(inBus);

	sig = CompanderD.ar(sig,0.7,1.75,0.9,0.2,0.3);

	sig = (sig*3.3).tanh;

	sig = Mix.new(RLPF.ar(sig,cutoff,res.reciprocal*LFSaw.kr(1/8).linlin(-1,1,0.1,1)));


	sig = sig * SinOsc.kr(4).linlin(-1,1,0.3,[1,1.3]);
	sig = (sig*gain).tanh*0.7;

	sig = sig+Mix.new(CombC.ar(sig,3,Lag2.kr(delay,lag)*[1/3,1/2,1/4],2));


	sig = (sig * 1.2).tanh/2.5;

	Out.ar(outBus,Pan2.ar(sig));


}).store;



SynthDef(\distortion, { arg inBus=0, gain = 3,cutoff=12000,q=0.1, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((RLPF.ar(In.ar(inBus),cutoff,q)*gain).tanh/4))
    )
}).store;



SynthDef(\gateDistort, {

	arg gain=3.3,
	inBus=0,
	outBus=0,
	compGate=1.7,
	lag=0.3;


	var sig, env;

	sig = In.ar(inBus);

	sig = CompanderD.ar(sig,0.3,compGate);

	sig = (sig*((Lag2.kr(gain,lag))*20)).distort.tanh;

	sig = CompanderD.ar( sig, 0.4,0.6,0.7,0.01,0.3)/4;
	ReplaceOut.ar(outBus,Pan2.ar(sig));


},
variants: (
	overdrive: [gain: 0.1, compGate: 1.5],
	hardcore: [gain: 83, compGate: 3.5],
	extreme: [gain: 3330, compGate: 4.5],
	crunch: [gain: 0.4, compGate: 2.9]
)
).store;




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




SynthDef(\hpf, { arg inBus=0, cutoff=70,q=0.3, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RHPF.ar(
			In.ar(inBus), cutoff, q
		)*wet);
    )
}).store;



SynthDef(\lpf, { arg inBus=0,outBus=0, cutoff=777,q=0.1, wet=0.5;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus).tanh * (1-wet))+
        (RLPF.ar(
			In.ar(inBus), cutoff, q
		).tanh*wet);
    )
}).store;



SynthDef(\moogFilter, { arg inBus=0, cutoff=1500,q=0.3, wet=0.25,lag=1;
    ReplaceOut.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((MoogFF.ar(
			In.ar(inBus), Lag2.kr(cutoff,lag),3.5+(q*0.47)
		)).tanh*1.5)*wet);
    )
}).store;



SynthDef(\resonz, { arg inBus=0, cutoff=1500,q=0.3, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((Resonz.ar(
			In.ar(inBus), cutoff, q
		)*4).tanh*0.75)*wet);
    )
}).store;



SynthDef(\ringMod, {

	var sig, rmod, freq=700, inBus=0, outBus=0;

	sig = In.ar(inBus);

	rmod = sig * SinOsc.ar(freq);

	Out.ar(outBus, Pan2.ar( rmod, 0 ));

}).store;





SynthDef(\pitchShift, {

	arg gain=0.3,
	inBus=0,
	outBus=0,
	compGate=1.7,
	lag=1,
	pitch;


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




SynthDef(\delay1, { arg inBus=0, delay = 0.25, wet=0.75,lag=1.5;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        ((DelayN.ar(
            In.ar(inBus),
            8,
            Lag2.kr(delay,lag)
        )/2)*wet)
    )
}).store;




SynthDef(\delay2, { arg inBus=0, delay=0.25, wet=0.75, lag=1.5;
	var sig = In.ar(inBus);
	sig = (In.ar(inBus) * (1-wet))+
    (Mix.arFill(7, { CombC.ar(
		sig, 8, Lag2.kr(delay,lag), 1
		)/4;
 	})*wet);
    Out.ar(
        [0,1],
        sig;
    )
}).store;




SynthDef(\delay3, { arg inBus=0, delay = 5, wet=0.75, lag=1;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        ((CombC.ar(
			In.ar(inBus), 8, LFNoise2.kr(0.3,0.4,0.5).range(0.1, Lag2.kr(delay,lag)), 1
		))*wet);
    )
}).store;




SynthDef(\reverb, {


	arg
	inBus=0,
	outBus=0,
	wet=0.5,
	room=0.3,
	damp=0.3,
	amp=1;

	var sig;
	var targetRoom, targetDamp, lag=0.3;
	var dsp, mix;

	sig = In.ar(inBus) * 1.25;

	dsp = FreeVerb.ar( sig, 1, room, damp );

	mix = (sig * (1-wet)) + (dsp * wet);

	ReplaceOut.ar( outBus, Pan2.ar( mix * amp.linlin(0,1,0,1.5), 0 ) );


}).store;



SynthDef(\eq, {

	arg inBus=99,
		outBus=99,

		amp=1,
		pan=0,


		low=0.5,
		middle=0.5,
		high=0.5,

		lowFreq=250,
		middleFreq=1100,
		highFreq=6500,

		lowCut=1,
		lowCutFreq=35;


	var sig;
	var dry;
	var wet;

	var dBsLow=low.max(0).min(1);
	var dBsMiddle=middle.max(0).min(1);
	var dBsHigh=high.max(0).min(1);

	dBsLow = dBsLow.linlin(0,1,-24,24);
	dBsMiddle = dBsMiddle.linlin(0,1,-24,24);
	dBsHigh = dBsHigh.linlin(0,1,-24,24);

	dry = In.ar(inBus) * amp;

	wet = BLowShelf.ar(dry, lowFreq, 1, dBsLow);
	wet = BPeakEQ.ar(wet, middleFreq, 1, dBsMiddle);
	wet = BHiShelf.ar(wet, highFreq, 1, dBsHigh);

	sig = (wet * (1 - lowCut)) + (HPF.ar(wet,lowCutFreq)*lowCut);

	ReplaceOut.ar(outBus, Pan2.ar(sig,pan))

}).store;



SynthDef(\eq, {

	arg inBus=99,
		outBus=99,

		amp=1,
		pan=0,


		low=0.5,
		middle=0.5,
		high=0.5,

		lowFreq=250,
		middleFreq=1100,
		highFreq=6500,

		lowCut=1,
		lowCutFreq=35;


	var sig;
	var dry;
	var wet;

	var dBsLow=low.max(0).min(1);
	var dBsMiddle=middle.max(0).min(1);
	var dBsHigh=high.max(0).min(1);

	dBsLow = dBsLow.linlin(0,1,-24,24);
	dBsMiddle = dBsMiddle.linlin(0,1,-24,24);
	dBsHigh = dBsHigh.linlin(0,1,-24,24);

	dry = In.ar(inBus) * amp;

	wet = BLowShelf.ar(dry, lowFreq, 1, dBsLow);
	wet = BPeakEQ.ar(wet, middleFreq, 1, dBsMiddle);
	wet = BHiShelf.ar(wet, highFreq, 1, dBsHigh);

	sig = (wet * (1 - lowCut)) + (HPF.ar(wet,lowCutFreq)*lowCut);

	ReplaceOut.ar(outBus, Pan2.ar(sig,pan))

}).store;
