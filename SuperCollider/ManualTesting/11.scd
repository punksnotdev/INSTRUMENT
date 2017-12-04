// crazy lace

(
	p = ProxySpace.push(s.boot);
	p.quant = 4;

	~z.play;
	~y.play;

	~z = {|note=48,t_trig=1| SinOsc.ar((note+[0,12,19]).midicps,0,SinOsc.kr([2/3,3],0,LFNoise2.kr(1/8,13,14))).tanh * Decay2.kr(t_trig) ! 2 };

	~y = {|notes=#[0,0,0,0,0,0],t_trig=1| SinOsc.ar((notes+[0,12]).midicps,0,SinOsc.kr([2/3,3],0,LFNoise2.kr(1/8,13,14))).tanh * Decay2.kr(t_trig,0.1,3)/4 ! 2 };

	INSTRUMENT.new.play;

	z = Proxy.new(\baZZ,~z);
	z.play;
	z.octave=2;
	z.set(P(\note,(C(7,\M).chord++\r++C(6,\m9m).chord++\r++\r++C(2,\mmaj7).chord).pyramid),[\speed,4])

	y = Proxy.new(\pad,~y);
	y.play;
	y.octave=4;
	y.set(P(\chord,[C(7,\M),\r,C(6,\m9m),\r,\r,C(2,\mmaj7)]),[\speed,2])

	z.play(0);
	y.play(0)

	// z.set(P(\note,[C(12,\M).chord,\r,C(6,\m9m).chord,\r,\r,C(2,\mmaj7).chord].lace(32)),[\speed,16])

)
