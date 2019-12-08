SynthDef(\delay2, { arg inBus=0, outBus=0, time=0.25, wet=0.75, lag=1.5;
	var sig = In.ar(inBus);
	sig = (In.ar(inBus) * (1-wet))+
    (Mix.arFill(7, { CombC.ar(
		sig, 8, Lag2.kr(time,lag), 1
		)/4;
 	})*wet);
    ReplaceOut.ar(
        outBus,
        sig;
    )
}).store;
