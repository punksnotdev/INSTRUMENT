SynthDef(\distNote4, {
	|freq=440.0, decayTime=0.5, amp=0.2, out=0, done=2, pan=0, att=0.001, sus=0.05, rel=0.7|

	var sig, env;

	sig = Resonz.ar(WhiteNoise.ar(LFNoise2.kr(1,99,100)),freq*[1,1.5,2],SinOsc.kr(3,0,0.01,0.011))/[2,4,3] ! 2 ;

	sig = RLPF.ar(sig + ( SinOsc.ar(freq,pi/2) * 3.33 ).tanh, freq*[2,4], 0.15 ).tanh / 4;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store
