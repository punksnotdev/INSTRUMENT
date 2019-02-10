SynthDef(\delay2, { arg inBus=0, delay=0.25, wet=0.75;
	var sig = In.ar(inBus);
	sig = (In.ar(inBus) * (1-wet))+
    (Mix.arFill(7, { CombC.ar(
		sig, 8, delay, 1
		)/4;
 	})*wet);
    Out.ar(
        [0,1],
        sig;
    )
}).store;
