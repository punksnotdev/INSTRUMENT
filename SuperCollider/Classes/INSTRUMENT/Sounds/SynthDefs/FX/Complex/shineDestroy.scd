(

	SynthDef(\shineDestroy, {

		arg
		delay=1,
		gain=3.3,
		freq=440,
		t_trig=1,
		cutoff=3500,
		res=10,
		att=1,
		decay=3,
		rel=6,
		inBus=0,
		outBus=0;


		var sig, env;

		sig = In.ar(inBus);

		sig = CompanderD.ar(sig,0.7,1.75,0.9,0.2,0.3);

		sig = (sig*3.3).tanh;

		sig = Mix.new(RLPF.ar(sig,cutoff,res.reciprocal*LFSaw.kr(1/8).linlin(-1,1,0.1,1)));


		sig = sig * SinOsc.kr(4).linlin(-1,1,0.3,[1,1.3]);
		sig = (sig*gain).tanh*0.7;

		sig = sig+Mix.new(CombC.ar(sig,3,delay*[1/3,1/2,1/4],2));


		sig = (sig * 1.2).tanh/2.5;

		Out.ar(outBus,Pan2.ar(sig));


	}).store;

)
