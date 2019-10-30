SynthDef(\hpf, { arg inBus=0, outBus=0,filterHz=60,q=1, wet=0.75;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus) * (1-wet))+
        (HPF.ar(
			In.ar(inBus), filterHz, q
		)*wet);
    )
}).store;
