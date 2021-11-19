SynthDef(\cymbal, {|out=0,amp=1,rel=6,pan=0|

	var lodriver, locutoffenv, hidriver, hicutoffenv, freqs, res, thwack;

    var sig;



	locutoffenv = EnvGen.kr(Env.perc(0.5, rel)) * 20000 + 10;

	lodriver = LPF.ar(WhiteNoise.ar(0.1), locutoffenv);



	hicutoffenv = 10001 - (EnvGen.kr(Env.perc(1, rel/1.5)) * 10000);

	hidriver = HPF.ar(WhiteNoise.ar(0.1), hicutoffenv);

	hidriver = hidriver * EnvGen.kr(Env.perc(1, rel/2, 0.25));



	thwack = EnvGen.ar(Env.perc(0.001,0.001,1));



	freqs  = [ 141.02113574378, 3883.9497438471, 4002.8700492571, 1784.2878594305, 1171.0203715661, 16136.110875042, 17022.807586395, 11426.705820585, 2499.8250877867, 4109.0894382271, 4967.6117243121, 5133.882864769, 587.03482127042, 300.46313362701, 10827.256953235, 1002.2987789229, 731.96210468856, 1124.4305180247, 608.91028743256, 4493.432719789, 621.04284574312, 17909.299777219, 15643.413676994, 296.84535951282, 723.31506240141, 1739.6857656893, 2684.8852914081, 474.79547998656, 1174.8611170416, 584.50503963364 ];


	res    = Ringz.ar(lodriver + hidriver + thwack, freqs).mean;

    sig = (res * 1) + (lodriver * 2) + (thwack * 3);

    sig = (sig*3.5).clip2 / 9;
    sig = sig * EnvGen.kr(Env.perc(0.01, rel), doneAction: 2);

	Out.ar(out,Pan2.ar( sig, pan ));

}).store;
