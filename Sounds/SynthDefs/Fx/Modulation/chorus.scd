SynthDef(\chorus, {|inBus=0,outBus=0, gate= 1|
	var source = In.ar(inBus,2);
	var chorus;
	var env = Linen.kr(gate, 0.1, 1, 0.1, 2);

	chorus= Splay.ar(Array.fill(4,{
		var maxdelaytime= rrand(0.005,0.02);

		DelayC.ar(source[0], maxdelaytime,LFNoise1.kr(Rand(0.1,0.6),0.25*maxdelaytime,0.75*maxdelaytime) )
	}));

	ReplaceOut.ar(outBus,Pan2.ar(env*chorus));

//From Steal This Sound SC Example
//By Nick Collins
}).store;
