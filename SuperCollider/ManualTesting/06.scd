(

	// TempoClock.default = TempoClock.new(144/120);
	p = ProxySpace.push(s);
	TempoClock.tempo = (144/120);
	p.makeTempoClock(144/120);

	// TempoClock.default = TempoClock.new(155/120);
	// p.makeTempoClock(144/120);
	// p.clock = TempoClock.default;

	p.quant = 4;

	~z = {|t_trig=1,note=50,amp=1| SinOsc.ar(Lag.kr(note.midicps,1/4),0,LFNoise2.kr(2,130*Lag2.kr(amp,1/8),131*Lag2.kr(amp,1/8))).tanh*Decay2.kr(t_trig) / 2 };
	// ~z = {|t_trig=1,note=50,amp=1| SinOsc.ar(Lag.kr((note+[0,7]).midicps,1/4),0,LFNoise2.kr(2,11130*Lag2.kr(amp,0.01),11131*Lag2.kr(amp,0.01))).tanh*Decay2.kr(t_trig,1,3) / 2 };

	~z.play;



	INSTRUMENT.new.play;
	x = Proxy.new(\p1,~z);
	// x.seq(P(\note,[2,3,5,8,7].pyramid.mirror));
	x.set(P(\note,[0,\r,2,3,\r,5,\r,8]),[\speed,4]);
	x.set(P(\note,[12,0,7,3,0,5,0,0]),[\speed,4]);
	// x.set(P(\note,[12,0,7,3,0,5,0,0]),[\speed,2]);
	// x.set(P(\note,[12,\r,\r,\r,7,\r,3,\r,\r,\r,5,\r,0,\r,0]),[\speed,8]);
	// x.rm(P(\note,[0,\r,2,3,\r,5,\r,8,0,\r,\r,\r,\r,\r,\r,7,\r]),[\speed,4]);
	// x.set(P(\note,[0,\r,2,3,\r,5,\r,8,1,\r,\r,\r,\r,\r,\r,7,\r]),[\speed,4]);
	// x.seq(P(\note,[0,\r,2,3,\r,5,\r,8]),[\speed,4]);
	// x.rm(P(\note,[0,\r,2,3,\r,5,\r,8,3,\r,\r,\r,\r,\r,\r,7,\r]),[\speed,16]);
	// x.seq(P(\note,[0,0,0,0,3,5,8,7]),[\speed,4]);
	// x.set(P(\note,([2,3,5,8,\r,\r,\r,7])),[\speed,8,\repeat,2]);
	x.seq(P(\octave,([4,3,2,3,\r,\r,\r,2])),[\speed,4]);
	x.set(P(\amp,([16,3,2,3,1,1,1,2]/16)),[\speed,4]);

	x.play(0);
	// x.stop;

	~btk.play;

	~btk[0]={(CaosKick.robot(t:~tempo.kr,rel:1/10)*13).tanh*2};
	~btk[1]={(CaosKick.robot(t:~tempo.kr*1/2)*3).tanh};
	~btk[2]={CaosHats.robot(t:~tempo.kr/1,tp:[0,2,5]/8) ! 2};
	~btk[3]={CaosHats2.robot(t:~tempo.kr/1,tp:LFPulse.kr(~tempo.kr/8,1/2,1/2,1,2)+[0,2,5]/8,rel:1/8)/2 ! 2};
	~btk[4]={CaosSnare.robot(t:~tempo.kr*1/4,tp:1/[2,3])};

	~btk[5]={CaosHats2.robot(t:~tempo.kr/1,tp:LFPulse.kr(~tempo.kr/8,1/2,1/2,1,2)+[0,2,5]/8,rel:1/8)/2 ! 2};
	// ~btk[6]={CaosHats2.robot(t:~tempo.kr/4,tp:(Stepper.kr(Impulse.kr(~tempo.kr/3),0,0,8,1)+[0,2,5])/8,rel:3)/2 ! 2};
	// ~btk[8] = \filter -> {|in| in/8 + FreeVerb.ar(RLPF.ar(in,LFNoise2.kr(~tempo.kr/4,500,1250),1/10),1,1/[10,1/3])*[1,1/8] };
	x.play(0);
	~btk[8] = 0;

	~btk[10]=0;//{(CaosKick.robot(t:~tempo.kr*2,rel:1/6)*13).tanh/2};

	// ~btk.fadeTime = 30;
	// ~btk = 0;
	~btk.play;
)
