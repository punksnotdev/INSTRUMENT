(
	var bpm = 160;
	p = ProxySpace.push(s);

	~z= { Silent.ar ! 2 };
	~mix = ~z;
	~amp = 0.5;
	~out = {
		var m=HPF.ar(~mix.ar,30);
		Out.ar(0,Mix.new(Pan2.ar(Limiter.ar(Compander.ar(m,m)*~amp))));
	};

	p.quant_(4);
	~out.quant_(4);
	// ~z.quant_(4);

	~out.play;

	p.makeTempoClock( bpm / 60 );

	~z.fadeTime = 5;

)

~z = { WhiteNoise.ar(1/10) ! 2 };
~z = { Silent.ar ! 2 };


s.record;
