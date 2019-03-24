(

	// INSTRUMENT.new.play;
	// p=ProxySpace.push(s);

	~z = {|note| BPF.ar(WhiteNoise.ar(6),(note).midicps, 0.001 ).tanh};
	~y = {|note| BPF.ar(WhiteNoise.ar(6),(note).midicps, 0.001 ).tanh};
	~x = {|note| BPF.ar(WhiteNoise.ar(6),(note).midicps, 0.001 ).tanh};

	i = Proxy.new(\testProxy1,~z);
	j = Proxy.new(\testProxy2,~y);
	n = Proxy.new(\testProxy3,~x);

	i.play;
	j.play;
	n.play;

	i.seq(P(\note,[0,2,3,5]),[\speed,0.5]);
	j.seq(P(\note,[4,5,7,8]),[\speed,0.5]);
	n.rm(P(\note,[7,8,10,7]),[\speed,0.5]);

	i.seq(P(\note,3+[0,2,3,5]),[\speed,0.25,\repeat,2]);
	j.seq(P(\note,9+[4,5,7,8]),[\speed,0.25,\repeat,2]);
	n.rm(P(\note,12+[0,2,3,5]),[\speed,0.25]);

	~mix = ~x+~y+~z;

	
)

(
Tdef(\melodia,{
	n.seq(P(\note,[7,8,10,7].pyramid.mirror),[\speed,4,\repeat,16]);
	n.octave = 5;

	inf.do{|i|
		n.set(P(\note,[7,8,10,7].pyramid.mirror),[\speed,((i%8)+1)*2]);
		4.wait;
	}
}).play;


)

~mix[1] = \filter -> {|in| in + FreeVerb.ar(in,) }
