SynthDef(\distPad2, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*8,0.05)/2;

	sig = RLPF.ar(( (sig + LFSaw.ar(freq)) * 333.33 ).tanh, freq, 0.05 );

	sig = (sig*1.3).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, Pan2.ar( sig * amp, 0) );

}).store;
