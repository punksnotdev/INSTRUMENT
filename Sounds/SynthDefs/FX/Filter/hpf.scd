SynthDef(\hpf, { arg inBus=0, cutoff=70,q=0.3, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RHPF.ar(
			In.ar(inBus), cutoff, q
		)*wet);
    )
}).store;
