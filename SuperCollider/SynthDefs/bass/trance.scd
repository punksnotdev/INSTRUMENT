
SynthDef(\tranceBazz, {
|freq=1.0, decayTime=0.5, amp=0.5, gain=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2, rq=0.06|

	var sig, env;

	sig = SinOsc.ar(freq)/4;
	sig = sig + Saw.ar(freq/4)/2;
	sig = (sig * 23.33*gain).tanh;
	sig = RLPF.ar(sig,freq*[1,4],rq).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 1 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;


SynthDef(\tranceBazz2, {
|freq=1.0, decayTime=0.5, amp=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2|

	var sig, env;

	sig = SinOsc.ar(freq/2)/4;
	sig = sig + Saw.ar(freq/4)/2;
	sig = RLPF.ar(sig,freq*[1,4],0.06);
	sig = (sig * 23.33).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 1 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;


SynthDef(\tranceBazz3, {
|freq=1.0, decayTime=0.5, amp=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2|

	var sig, env;

	sig = LFPulse.ar(freq/2,LFNoise2.kr(8,0.4,0.5));
	sig = sig + Saw.ar(freq/[4,1/2])/[2,8];
	sig = RLPF.ar(sig,freq*[1,4],0.06);
	sig = (sig * 3.33).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 1 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;
