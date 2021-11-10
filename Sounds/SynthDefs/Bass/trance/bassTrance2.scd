SynthDef(\bassTrance2, {
|freq=1.0, decayTime=0.5, amp=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2|

	var sig, env;

	sig = SinOsc.ar(freq/2)/4;
	sig = sig + Saw.ar(freq/4)/2;
	sig = RLPF.ar(sig,freq*[1,4],0.06);
	sig = (sig * 23.33).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp, pan) );

}).store;
