(
	p = ProxySpace.push(s.boot);
	p.quant = 4;

	~z = {|t_trig=1,coin=0.5,amp=1,note=48| ((MoogFF.ar(WhiteNoise.ar,note.midicps,3.9).tanh*Decay2.kr(CoinGate.kr(coin,t_trig),1,0.05)*amp)*LFNoise1.kr(1/8,0.48,1.49)).tanh*2};

	~z.play;

	INSTRUMENT.new.play;

	z = Proxy.new(\ccc,~z);
	z.play;
	// z.set(P(\trigger,[1,2,3,4]/4));
	z.set(P(\coin,2*[0.25,0.05,0.1,0.5]),[\speed,1]);

	z.rm(P(\note,[0,\r,\r,\r,3,8,\r]),[\speed,8]);
	z.rm(P(\note,[0,\r,\r,\r,3,8,\r,2,\r,\r,\r,\r]),[\speed,16]);
	z.set(P(\note,[0,\r,\r,\r,3,8,\r,2,\r,\r,\r,\r,2]),[\speed,16]);


)
