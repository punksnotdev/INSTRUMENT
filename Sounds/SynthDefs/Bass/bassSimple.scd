SynthDef(\bassSimple, {|freq=220,amp=0.5, out=0, rel=0.2|

	var sig;
	var env;

	sig = Mix([Saw.ar(freq*1/2),SinOsc.ar(freq*[1/4,1/2],pi/2,3)]);

    sig = sig * EnvGen.kr( Env.perc(rel:rel), doneAction: 2 );

    sig = (sig * 18).tanh;

    
	Out.ar(out, Pan2.ar(sig * amp, 0, amp));

}).store;
