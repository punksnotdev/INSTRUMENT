SynthDef(\revLPF, { arg inBus=0, cutoff=1500,q=0.1, room=0.4,damp=0.6, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (RLPF.ar(
			FreeVerb.ar(In.ar(inBus),room,damp), cutoff, q
		)*wet);
    )
}).store;
