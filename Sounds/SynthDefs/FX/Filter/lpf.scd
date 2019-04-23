SynthDef(\lpf, { arg inBus=0, cutoff=1500,q=0.1, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RLPF.ar(
			In.ar(inBus), cutoff, q
		)*wet);
    )
}).store;
