(

	p = ProxySpace.push(s);

	p.quant = 4;

	~z = {|t_trig=1,note=50| RLPF.ar(WhiteNoise.ar(1/8),note.midicps,0.001)*Decay2.kr(t_trig) };

	~z.play;


	INSTRUMENT.new.play;

	x = Proxy.new(\p1,~z);
	// x.seq(P(\note,[2,3,5,8,7].pyramid.mirror));
	x.seq(P(\note,[2,\r,\r,\r,3,5,8,7]),[\speed,4]);
	x.seq(P(\note,([2,3,5,8,\r,\r,\r,7]-12)),[\speed,4,\repeat,2]);

	x.play;

)

x.set(P(\note,[2,\r,\r,\r,3,5,8,7]),[\speed,16]);
x.set(P(\note,([2,3,5,8,\r,\r,\r,7]-12)),[\speed,4,\repeat,2]);
