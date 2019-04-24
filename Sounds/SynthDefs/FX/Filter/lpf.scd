SynthDef(\lpf, { arg inBus=0,outBus=0, cutoff=777,q=0.1, wet=0.5;
    Out.ar(
        outBus,
        (In.ar(inBus).tanh * (1-wet))+
        (RLPF.ar(
			In.ar(inBus), cutoff, q
		).tanh*wet);
    )
}).store;
