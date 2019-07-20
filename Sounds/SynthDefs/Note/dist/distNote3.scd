SynthDef(\distNote3, {
	|freq=440.0, decayTime=0.5, amp=0.2, gain=1, out=0, done=2, pan=0, att=0.001, sus=0.05, rel=0.6, q=0.005|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(8,303,300)).tanh,freq*4,q)/2;

	sig = RLPF.ar(sig + ( LFSaw.ar(freq) * 13.33 * gain ).tanh, freq, 0.95 ).tanh / 2;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;
