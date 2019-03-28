SynthDef(\delay3, { arg inBus=0, delay = 5, wet=0.75, lag=1;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        ((CombC.ar(
			In.ar(inBus), 8, LFNoise2.kr(0.3,0.4,0.5).range(0.1, Lag2.kr(delay,lag)), 1
		))*wet);
    )
}).store;
