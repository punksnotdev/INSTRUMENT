(

	g = INSTRUMENT.new.play;

	~z = {|note=60| RLPF.ar(WhiteNoise.ar(2),(note).midicps, 0.01 ).tanh };
	~y = {|note=0| RLPF.ar(WhiteNoise.ar(1/2),(note).midicps, 0.01 ) };
	~x = {|note=0| RLPF.ar(WhiteNoise.ar(1/2),(note).midicps, 0.01 ) };

	i = Proxy.new(\testProxy1,~z);
	j = Proxy.new(\testProxy2,~y);
	k = Proxy.new(\testProxy3,~x);

	i.play;
	j.play;
	k.play;

	i.rm(P(\note,[0,2,3,5]),[\speed,2]);
	i.seq(P(\note,[0,2,3,5]),[\speed,4]);
	j.seq(P(\note,[4,5,7,8]),[\speed,2]);
	k.rm(P(\note,[0,2,3,5]),[\speed,2]);
	k.rm(P(\note,[7,8,10,15]),[\speed,6]);
	k.seq(P(\note,[7,8,10,15]),[\speed,3]);

k.octave = 4

)

~mix = ~x+~y+~z;

i.play(48);
j.play(48);
k.play(48);

~mix = 0
~mix.fadeTime = 10;

i.stop;
