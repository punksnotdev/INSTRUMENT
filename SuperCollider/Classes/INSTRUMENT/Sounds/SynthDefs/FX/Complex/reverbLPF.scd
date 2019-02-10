SynthDef(\revLPF, { arg inBus=0, cutoff=1500,q=0.1, rv1=0.4,rv2=0.6, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RLPF.ar(
			FreeVerb.ar(In.ar(inBus),rv1,rv2), cutoff, q
		)*wet);
    )
}).store;
