SynthDef(\rhodes2,{|out= 0 freq = 440 amp = 0.1 gate=1 lforate = 4.85 lfowidth= 0.5 filterHz= 2000 rq=0.5 pan = 0.0,rel=1|

//Electric Piano
	var pulse, filter, env;

	pulse = Pulse.ar(freq*[1,33.5.midiratio],[0.2,0.1],[0.7,0.3]);
	env = EnvGen.ar(Env.perc(0.0,rel),doneAction:2);

	//keyboard tracking filter filterHz
	filter = BLowPass4.ar(pulse,(filterHz*(env.squared))+200+freq,rq);
	filter = filter / 3;
	Out.ar(out,Pan2.ar(Mix(filter)*env*amp,pan));

//By Nick Collins, from Mitchell Sigman (2011) Steal this Sound
//http://www.sussex.ac.uk/Users/nc81/index.html
}).store;
