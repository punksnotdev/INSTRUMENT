SynthDef(\ridebell, {|out=0,amp=1,rel=1.2,pan=0|

	var lodriver, locutoffenv, hidriver, hicutoffenv, freqs, res, thwack;

    var sig;



	locutoffenv = EnvGen.kr(Env.perc(0.5, rel/2)) * 20000 + 10;

	lodriver = LPF.ar(WhiteNoise.ar(0.1), locutoffenv);



	hicutoffenv = 10001 - (EnvGen.kr(Env.perc(0.01, rel/1.5)) * 10000);

	hidriver = HPF.ar(WhiteNoise.ar(0.1), hicutoffenv);

	hidriver = hidriver * EnvGen.kr(Env.perc(0.01, rel, 0.25));



	thwack = EnvGen.ar(Env.perc(0.001,0.001,1));



	freqs  = [ 12031.216162528, 18337.054767108, 5866.0731476501, 3034.4292192795, 4119.1229036665, 3518.7465379346, 12456.370562093, 18889.848792143, 5360.1984593112, 13379.586133114, 4394.3084275317, 9194.4494453702 ];


	res    = Ringz.ar(lodriver + hidriver + thwack, freqs).mean;

    sig = (res * 1) + (lodriver * 2) + (thwack * 3);

    sig = (sig*10.3).clip2/9;
    sig = sig * EnvGen.kr(Env.perc(0.01, rel), doneAction: 2);

	Out.ar(out,Pan2.ar( sig, pan ));

}).store;
