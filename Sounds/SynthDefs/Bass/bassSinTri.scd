SynthDef(\bassSinTri,
{
	arg
	out=0,
	freq=90;

    var sig, env;

    sig = SinOsc.ar(freq,pi/2,1,0);

	sig = sig + LFTri.ar(freq,4,1,0);

    env = EnvGen.kr(Env.perc(0.005, 1, 2, 0.1),doneAction:2);

  	Out.ar(out, (Pan2.ar( sig * env) / 2));

}).store;
