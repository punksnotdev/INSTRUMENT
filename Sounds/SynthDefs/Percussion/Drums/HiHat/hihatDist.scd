SynthDef(\hihatDist, {|amp=0.5,out=0, rel=0.125,pan=0,gain=2|

	    var hatosc, hatenv, hatnoise, hatoutput;

	    hatnoise = {LPF.ar(WhiteNoise.ar(1),6000)};

	    hatosc = {HPF.ar(hatnoise,2000)};
	    hatenv = EnvGen.kr(Env.perc(0.01,rel),doneAction:2);

	    hatoutput = (hatosc * hatenv)/2;
		hatoutput = (hatoutput*gain).tanh;
	    Out.ar(out,
	    Pan2.ar(hatoutput*amp, pan)

	    )

},variants: (
	overdrive: [ gain: 3 ],
	crunch: [ gain: 13 ],
	hardcore: [ gain: 100 ],
	extreme: [ gain: 3000 ],
)).store;
