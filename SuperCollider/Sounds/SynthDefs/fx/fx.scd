
SynthDef(\reverb, { arg inBus=0, rv1 = 0.7, rv2=0.8, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (FreeVerb.ar(
            In.ar(inBus) * wet,
            rv1,
            rv2
        )*wet)
    )
}).store;

SynthDef(\distortion, { arg inBus=0, gain = 3,cutoff=12000,q=0.1, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((RLPF.ar(In.ar(inBus),cutoff,q)*gain).tanh/4))
    )
}).store;


SynthDef(\delay1, { arg inBus=0, delay = 0.25, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        ((DelayN.ar(
            In.ar(inBus),
            8,
            delay
        )/2)*wet)
    )
}).store;



SynthDef(\delay2, { arg inBus=0, delay=0.25, wet=0.75;
	var sig = In.ar(inBus);
	sig = (In.ar(inBus) * (1-wet))+
    (Mix.arFill(7, { CombC.ar(
		sig, 8, delay, 1
		)/4;
 	})*wet);
    Out.ar(
        [0,1],
        sig;
    )
}).store;

SynthDef(\delay3, { arg inBus=0, delay = 5, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        ((CombC.ar(
			In.ar(inBus), 8, LFNoise2.kr(0.3,0.4,0.5).range(0.1, delay), 1
		)/4)*wet);
    )
}).store;



SynthDef(\revlpf, { arg inBus=0, cutoff=1500,q=0.1, rv1=0.4,rv2=0.6, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RLPF.ar(
			FreeVerb.ar(In.ar(inBus),rv1,rv2), cutoff, q
		)*wet);
    )
}).store;


SynthDef(\lpf, { arg inBus=0, cutoff=1500,q=0.1, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RLPF.ar(
			In.ar(inBus), cutoff, q
		)*wet);
    )
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

SynthDef(\resonz, { arg inBus=0, cutoff=1500,q=0.3, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((Resonz.ar(
			In.ar(inBus), cutoff, q
		)*4).tanh*0.75)*wet);
    )
}).store;

SynthDef(\moogFF, { arg inBus=0, cutoff=1500,q=0.5, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((MoogFF.ar(
			In.ar(inBus), cutoff,3.5+q
		)).tanh*0.75)*wet);
    )
}).store;
