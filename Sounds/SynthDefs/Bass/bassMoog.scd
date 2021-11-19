//no use of gate, fixed length
SynthDef(\bassMoog,{|out= 0, freq = 440, amp = 1, gate=1, att=0.01, rel=0.6, fenvamount=0.5, cutoff= 1000, gain=2.0, pan=0.0|

	var osc, filter, env, filterenv, sig;

	//alternative: richer source
	osc = Mix(Pulse.ar((freq/2).lag(0.05)*[1.0,1.001,2.0],Rand(0.45,0.5)!3,0.33));

	filterenv = EnvGen.ar(Env.adsr(att*4,rel));
	filter =  MoogFF.ar(osc,cutoff*(1.0+(fenvamount*filterenv)),gain);

	// env = EnvGen.ar(Env.adsr(0.001,0.3,0.9,0.2),gate,doneAction:2);

	sig = filter+(0.8*filter.distort);
	sig = sig*8.tanh*2;
	env = EnvGen.kr(Env.perc(att,rel),doneAction:2);

	Out.ar(out,Pan2.ar(sig*env*amp,pan));

}).store;
