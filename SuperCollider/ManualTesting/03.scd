(

	p = ProxySpace.push(s);

	~x = {|note=0| RLPF.ar(WhiteNoise.ar(1/2),(note+[0,3,7,5,8]).midicps, 0.01 ) };
	~x[1] = \filter -> {|in| in + (in * LFNoise2.kr(LFNoise2.kr(1/4,0.05,8),12,12.1)).tanh / 4 };
	~x[3] = \filter -> {|in| (in * LFNoise2.kr(LFNoise2.kr(1/4,5,18),33,33.1)).tanh / 1.5 };
	~x[4] = \filter -> {|in| FreeVerb.ar(in,1/3,1/3) };

	~x.play;


	INSTRUMENT.new.play;

	i = Proxy(\p1,~x);

	i.play;

	i.seq(P(\note,[0,2,3,5]));
	i.seq(P(\note,7+[0,2,3,5].pyramid),[\speed,4]);

	i.octave = 4;

)
