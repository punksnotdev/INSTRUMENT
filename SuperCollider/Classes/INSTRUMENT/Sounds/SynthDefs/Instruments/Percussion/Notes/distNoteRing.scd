(

	SynthDef(\distNoteRing, {
	|freq=440.0, decayTime=0.5, amp=0.2, out=0, done=2, pan=0, att=0.001, sus=0.05, rel=2.6|

		var sig, env;

		sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(8,303,300)).tanh,LFNoise2.kr(6,[3,4])+freq*4,0.005)/2;

		sig = RLPF.ar(sig + ( LFSaw.ar(LFNoise2.kr(6,[3,4])+freq) * 3333.33 ).tanh, LFNoise2.kr(6,[3,4])+freq, 0.95 ).tanh / 2;

		env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

		sig = sig * env;

		Out.ar( out, sig * amp );

	}).store;

)
