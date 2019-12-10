SynthDef(\hpf, { arg inBus=0, outBus=0,freq=60,q=1, wet=1,lag=0.05;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus) * (1-wet))+
        (HPF.ar(
			In.ar(inBus), VarLag.kr(freq,lag), q
		)*wet);
    )
}).store;
