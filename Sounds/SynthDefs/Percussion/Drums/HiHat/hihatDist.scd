SynthDef(\hihatDist, {|amp=0.5,out=0,cutoff=7000,rel=0.125,pan=0,gain=2,gain_out=1|

	    var hatosc, hatenv, hatnoise, hatoutput;

	    hatnoise = {LPF.ar(WhiteNoise.ar(1),cutoff,0.3)};

	    hatosc = {HPF.ar(hatnoise,2000)};
	    hatenv = EnvGen.kr(Env.perc(0.01,rel),doneAction:2);

	    hatoutput = (hatosc * hatenv)/2;
		hatoutput = (hatoutput*gain).tanh * gain_out;
	    Out.ar(out,
	    Pan2.ar(hatoutput*amp, pan)

	    )

},variants: (
	overdrive: [ gain: 3, cutoff: 3000, gain_out: 0.8 ],
	crunch: [ gain: 13, cutoff: 4000, gain_out: 0.6 ],
	hardcore: [ gain: 100, cutoff: 5000, gain_out: 0.4 ],
	extreme: [ gain: 3000, cutoff: 6000, gain_out: 0.01 ],
)).store;
