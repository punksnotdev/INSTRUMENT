SynthDef(\bassrrr, {
|freq=1.0, decayTime=0.5, amp=0.5, temp=100, out=0, done=2, pan=0, att=0.5, sus=0.1, rel=3,gain=20|

	var sig, env;
	var drift = freq/2-LFSaw.kr(1/rel,0,freq/4,freq/4);
	
	sig = SinOsc.ar((freq+drift/[1,1.5,2])/4)+WhiteNoise.ar(1/3) ! 1;

	sig = FreeVerb.ar(sig,1/3,2/3,1/4)/2;
	
	sig = sig + ((
		EnvGen.kr( Env.perc(rel*2,att,att*10)) * 2 * 	
		Saw.ar((freq+drift)/[4,8])*2).tanh ! 1;
	);
	sig = RLPF.ar(sig,freq*[1,4,8],0.06);
	sig = (sig * gain).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp, pan) );

}).store;
