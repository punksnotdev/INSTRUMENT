(

SynthDef(\bassSimple, {|freq=220,amp=0.5, out=0|

	var sig;
	var env;

	sig = Saw.ar(freq);

	sig = sig * EnvGen.kr( Env.perc, doneAction: 2 );

	Out.ar(out, Pan2.ar(sig * amp, 0, amp) / 2);

}).store;



)
