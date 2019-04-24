(

	SynthDef(\padShineDestroy, {

		arg freq=440,
		t_trig=1,
		res=10,
		att=1,
		decay=3,
		rel=6;


		var sig, env, filter_env;

		sig = LFPulse.ar(freq+[1,-1],[0,3]/8,LFSaw.kr(1).linlin(-1,1,0,1));
		sig = sig + SinOsc.ar((freq)/2,0,0.6);
		sig = sig + Saw.ar((freq)/2);

		sig = (Mix.new(sig)*3.3).tanh;

		filter_env = EnvGen.kr(Env.perc(att,decay/4),t_trig);

		sig = Mix.new(RLPF.ar(sig,[freq*[3,4],filter_env.linlin(0,1,100,5000)],res.reciprocal*filter_env.linlin(0,1,0.1,1)));


		env = EnvGen.kr(Env.adsr(att,decay,1,rel),t_trig,doneAction:2);


		sig = sig * SinOsc.kr(4).linlin(-1,1,0.3,[1,1.3]);
		sig = sig.tanh;

		sig = sig * env;

		sig = sig+Mix.new(CombC.ar(sig,3,[1/3,1/2,1/4],2));


		sig = (sig * 1.2).tanh*8;

		Out.ar(0,Pan2.ar(sig));


	}).store;


)
