SynthDef(\moogFilter, { arg inBus=0, cutoff=1500,q=0.5, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((MoogFF.ar(
			In.ar(inBus), cutoff,3.5+(q*0.4)
		)).tanh*0.75)*wet);
    )
}).store;
