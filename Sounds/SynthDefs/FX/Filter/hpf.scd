SynthDef(\hpf, { arg inBus=0, outBus=0,cutoff=40,q=0.3, wet=0.75;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus) * (1-wet))+
        (RHPF.ar(
			In.ar(inBus), cutoff, q
		)*wet);
    )
}).store;
