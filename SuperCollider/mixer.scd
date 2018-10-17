(
s.options.memSize=2048*1024;
s.options.maxNodes=64*1024;
s.boot;
)
(



	().play;
	s.scope;



	TempoClock.default.tempo=166/120;
	ProxySpace.clearAll;
	p = ProxySpace.push(s);
	p.makeTempoClock(166/120);



	p.quant = 4;

	~out=NodeProxy.audio(s,2);
	~z=NodeProxy(s,\audio,2);

	~mix=NodeProxy.audio(s,2);

	~z= { Silent.ar ! 2 };
	~z.fadeTime = 5;
	~z = { WhiteNoise.ar(7/[8,8]) };

	~amp = 0.5;
	~mix = ~z;

	~mix.fadeTime=8;

	~out = {
	Pan2.ar(Limiter.ar(CompanderD.ar(Mix.new(HPF.ar(~mix.ar,30)), 0.9, 1, 0.9, 0.045,0.3 )*~amp),0);
	};


	~out.play;


	p.quant=4;

	~out.quant=4;

	~out.play;



	"INSTRUMENT v.0.1".postln;


	// ~z = { Silent.ar ! 2 };



)
