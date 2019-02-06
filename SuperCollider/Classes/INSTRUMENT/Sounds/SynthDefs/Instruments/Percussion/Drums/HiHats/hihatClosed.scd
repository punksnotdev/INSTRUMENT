(
	SynthDef(\hihatClosed, {|amp=0.5,out=0, rel=0.125|

	    var hatosc, hatenv, hatnoise, hatoutput;

	    hatnoise = {LPF.ar(WhiteNoise.ar(1),6000)};

	    hatosc = {HPF.ar(hatnoise,2000)};
	    hatenv = EnvGen.kr(Env.perc(0.01,rel),doneAction:2);

	    hatoutput = (hatosc * hatenv)/2;

	    Out.ar(out,
	    Pan2.ar(hatoutput*amp, 0)

	    )

	}).store;
)
