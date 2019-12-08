SynthDef(\moogFilter, { arg inBus=0, outBus=0, filterHz=1500,q=0.3, wet=0.25,lag=1;
    ReplaceOut.ar(
        outBus,
        (In.ar(inBus) * (1-wet))+
        (((MoogFF.ar(
			In.ar(inBus), Lag2.kr(filterHz,lag),3.5+(q*0.47)
		)).tanh*1.5)*wet);
    )
}).store;
