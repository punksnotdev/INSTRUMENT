SynthDef(\bassSinTri,
{
	arg
	out=0,
	freq=90,
	rel=1;

    var sig, env;

    sig = SinOsc.ar(freq/2,pi/2,1,0);

	sig = sig + LFTri.ar(freq/2,4,1,0);

    env = EnvGen.kr(Env.perc(0.005, rel, 2, 0.1),doneAction:2);

  	Out.ar(out, (Pan2.ar( sig * env) / 2));

}).store;
