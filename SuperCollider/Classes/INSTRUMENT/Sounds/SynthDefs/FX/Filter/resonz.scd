SynthDef(\resonz, { arg inBus=0, cutoff=1500,q=0.3, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((Resonz.ar(
			In.ar(inBus), cutoff, q
		)*4).tanh*0.75)*wet);
    )
}).store;
