(
	p = ProxySpace.push(s.boot);
	p.quant = 4;

	~z = {|t_trig=1,notes=#[50,60,62,65,69,72],amp=1|Mix.new(Pan2.ar( SinOsc.ar(Lag.kr([notes,notes+7].midicps,1/4),0,LFNoise2.kr(2,1.30*Lag2.kr(amp,1/8),0.61*Lag2.kr(amp,1/8))).tanh*Decay2.kr(t_trig,1/4,5) )) ! 2 / 2 };
	~z.play;
	~z.set(\t_trig,1);

	INSTRUMENT.new.play;

	z = Proxy.new(\cc1,~z);
	z.play;

	~btk.play;

	~btk[0] = { CaosKick.robot(t:1/[2,3],tp:[0,1]/4) ! 2 };
	~btk[1] = { CaosHats.robot(t:1/[1,2],tp:[0,2,3,7]/8) ! 2 };
	~btk[2] = { CaosHats2.robot(t:1/[1,2],tp:(1+[0,2,3,7])/8,rel:1/4)/2 ! 2 };
	~btk[3] = { (CaosKick.robot(t:1/[1,1.5],tp:[0,1]/4,rel:1/10)*8).tanh/2 ! 2 };

)
z.set(P(\chord,[C(0,\m),\r,\r,C(7,\Mmaj7),\r,\r,\r,C(2,\mmaj7),\r]));
z.set(P(\chord,[C(3,\m),\r,\r,C(2,\Mmaj7),\r,\r,\r,C(7,\mmaj7),\r]));
z.set(P(\chord,[C(3,\m9m),\r,\r,C(3,\M9),\r,\r,\r,C(6,\mmaj7),C(7,\m9)]));
