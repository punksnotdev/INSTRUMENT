(

	p = ProxySpace.push(s);
	p.quant_(4)
	i = INSTRUMENT.new.play;

	~bmb1.fadeTime = 0;
	~bmb1 = {|t_trig,note=0|
		(RLPF.ar(FreeVerb.ar(CaosKick.seq(t_trig,rel:2.2) * 2, 0.3, 0.1).tanh,(note+[60,62,67,72]).midicps,0.01) * 1.8).tanh / 18
	};
	~bmb2 = {|t_trig|
		FreeVerb.ar(CaosKick.robot(t:1/[2,3],tp:1/2,rel:1/2,amp2:2).tanh/4,1,1)
	};
	~bmb3 = {|t_trig|
		CaosKick.robot(t:2,tp:[0,3,7,8]/16,rel:1/6,amp2:2).tanh ! 2
	};

	~bmb4.fadeTime = 10;
	~bmb4 = {|t_trig,note=0|
		(CaosKick.seq(t_trig,rel:0.2) * 1.8).tanh
	};

	k = Proxy.new(\k1,~bmb1);
	k.play;
	k.rm(P(\t_trig,[1,0,1,0,0,0,0,1]),[\speed,4]);
	k.seq(P(\note,[2,3,5,8]),[\speed,2]);
	k.seq(P(\note,[7,8,7]),[\speed,3,\repeat,8]);
	k.seq(P(\note,[7,5,7]),[\speed,3],\repeat,8]);

	l.stop;
	l = Proxy.new(\k2,~bmb4);
	l.play(4);
	l.seq(P(\t_trig,[1,1,1,0,0,0,0,0,1,1,0,0,0,0,0,0]),[\speed,4]);

	~bmb1.play;
	~bmb2.play;
	~bmb3.stop;
	~bmb4.stop;

)
