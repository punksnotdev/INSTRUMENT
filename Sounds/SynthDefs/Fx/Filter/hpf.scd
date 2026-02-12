SynthDef(\hpf, { arg inBus=0, outBus=0,freq=60,q=1, wet=1,lag=0.05;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus, 2) * (1-wet))+
        (HPF.ar(
			In.ar(inBus, 2), VarLag.kr(freq,lag), q
		)*wet);
    )
}).store;
