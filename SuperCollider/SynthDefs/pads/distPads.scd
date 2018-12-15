SynthDef(\distPad1, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*4,0.05)/2;

	sig = RLPF.ar(sig + ( LFSaw.ar(freq) * 33.33 ).tanh, freq*2, 0.35 ).tanh / 4;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;

SynthDef(\distPad2, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*8,0.05)/2;

	sig = RLPF.ar(( (sig + LFSaw.ar(freq)) * 333.33 ).tanh, freq, 0.05 );

	sig = (sig*1.3).tanh / 3;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;

SynthDef(\distPad3, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*4,0.05)/2;

	sig = RLPF.ar(
		(sig + LFSaw.ar(freq/4)),
		freq, 0.05
	);

	sig = (sig * 1.33).tanh / 4;
	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;
