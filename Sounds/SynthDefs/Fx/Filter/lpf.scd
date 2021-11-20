SynthDef(\lpf, { arg inBus=0,outBus=0, freq=1000,q=0.1, wet=1, lag=0.1;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus).tanh * (1-wet))+
        (RLPF.ar(
			In.ar(inBus), VarLag.kr(freq,lag), q
		).tanh*wet);
    )
}).store;
