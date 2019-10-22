SynthDef(\resonz, { arg inBus=0, outBus=0, cutoff=1500,q=0.3, wet=0.75;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus) * (1-wet))+
        (((Resonz.ar(
			In.ar(inBus), cutoff, q
		)*4).tanh*0.75)*wet);
    )
}).store;
