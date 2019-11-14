SynthDef(\lpf, { arg inBus=0,outBus=0, freq=777,q=0.1, wet=1;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus).tanh * (1-wet))+
        (RLPF.ar(
			In.ar(inBus), freq, q
		).tanh*wet);
    )
}).store;
