(

	p = ProxySpace.push(s);

	p.quant = 4;

	~x = { Impulse.ar(1/[2,3]) };
	~y = { Impulse.ar(4)/8 };
	~z = {|t_trig=1,note=50| RLPF.ar(WhiteNoise.ar(1/8),note.midicps)*Decay2.kr(Impulse.kr(1))/8 };
	~z = {|t_trig=1,note=50| RLPF.ar(WhiteNoise.ar(1/8),note.midicps,0.1)*Decay2.kr(t_trig) };

	~z2 = {|t_trig=1,note=50| RLPF.ar(WhiteNoise.ar,note.midicps,0.01)*Decay2.kr(t_trig,1/2,1/8) /2};
	~z3 = {|t_trig=1,note=50| RLPF.ar(WhiteNoise.ar(1/8),note.midicps,0.1)*Decay2.kr(t_trig,0.2,0.1) /2};

	~x.stop;
	~y.stop;
	~z.stop;


	INSTRUMENT.new.play;

	x = Proxy.new(\p1,~z);
	// x.seq(P(\note,[2,3,5,8,7].pyramid.mirror));
	x.seq(P(\note,[2,3,5,8,7].pyramid.mirror),[\speed,8]);
	x.play;
	x.octave = 7;

	~btk = {|t_trig| t_trig*SinOsc.ar(60,0,LFNoise2.kr(1/4,20,20.1)).tanh / 4 }

	~mix = ~z + ~btk

)

y = Proxy.new(\p1,~btk);
y.rm(P(\note,[2,3,5,8,7].pyramid.mirror));
y.seq(P(\note,[2,3,5,8,7].pyramid.mirror),[\speed,4]);
y.octave = 4;
y.play;
y.stop;

z = Proxy.new(\p1,~z3);
z.add(P(\note,[2,3,5,8,7].pyramid.mirror,[\speed,0.5]));
z.octave = 7;
z.play;
z.stop;
