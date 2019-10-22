SynthDef(\distortion, { arg inBus=0, outBus=0, gain = 3,cutoff=12000,q=0.1, wet=0.75;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus) * (1-wet))+
        (((RLPF.ar(In.ar(inBus),cutoff,q)*gain).tanh/4))
    )
}).store;
