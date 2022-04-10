SynthDef(\hihatDist, {|amp=0.5,out=0,filterHz=7000,rel=0.125,pan=0,gain=4,gain_out=1|

	    var hatosc, hatenv, hatnoise, hatoutput;

	    hatnoise = {LPF.ar(WhiteNoise.ar(1),filterHz,0.3)};

	    hatosc = {HPF.ar(hatnoise,2000)};
		hatosc = (hatosc*gain).tanh * gain_out;

	    hatenv = EnvGen.kr(Env.perc(0.01,rel),doneAction:2);

	    hatoutput = (hatosc * hatenv)/2;
	    Out.ar(out,
	    Pan2.ar(hatoutput*amp, pan)

	    )

},variants: (
	a_overdrive: [ gain: 8, filterHz: 3000, gain_out: 0.6 ],
	b_crunch: [ gain: 13, filterHz: 4000, gain_out: 0.5 ],
	c_hardcore: [ gain: 300, filterHz: 2500, gain_out: 0.4, rel: 1/3 ],
	d_extreme: [ gain: 1300, filterHz: 1800, gain_out: 0.5, rel: 2/5 ],
)).store;
