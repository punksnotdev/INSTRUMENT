SynthDef(\psykick, { | dur = 0.25, atk = 0.005, amp = 1, out=0 |
	Out.ar(out,SinOsc.ar(
		EnvGen.ar( Env(
			[2960, 70, 90],
			[0.07, 0.2],
			[-13, -1],
		) ),
		mul: amp * EnvGen.kr(
			Env.perc( atk, dur - atk),
			doneAction: 2
		);
	)/3 ! 2 );
}).store;
