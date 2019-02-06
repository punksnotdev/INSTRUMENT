SynthDef(\chaosPerc,{|out=0,note=0,amp=0.5,modfactor=130,att=0.01,rel=1|
	var sig;
	var env;
	var freq;
	var modfreq;

	freq = (note+1) * (103);

	modfreq = (note+1)*modfactor;

	sig = Ringz.ar( SinOsc.ar(freq*3), SinOsc.kr(SinOsc.kr(freq.reciprocal,0,modfreq,modfreq),0,modfreq*0.9,modfreq), 0.1 );
	sig = RLPF.ar(sig,(1+note)*modfactor,0.1);
	sig = HPF.ar((sig.abs*1.3).tanh,160);

	env = EnvGen.kr(Env.perc(att,rel+note.reciprocal),doneAction:2);

	sig = sig * env;
	sig = sig * amp;

	Out.ar(out,Pan2.ar(sig,0))

},variations:(
	hollow: [modfactor:100],
	glass: [modfactor:1200]
)).store;
