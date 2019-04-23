SynthDef(\moogFilter, { arg inBus=0, cutoff=1500,q=0.7, wet=0.75,lag=1;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (((MoogFF.ar(
			In.ar(inBus), Lag2.kr(cutoff,lag),3.5+(q*0.47)
		)).tanh*1.5)*wet);
    )
}).store;
