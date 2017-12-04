(

	~bmb = {|t_trig,rel,amp1|
	// ~btk[0] = {|t_trig|
		(CaosKick.seq(t_trig,rel:rel,amp1:amp1)*1).tanh ! 2
	};
	~hhc = {|t_trig|
		(CaosHats.seq(t_trig,rel:1/8)*0.3).tanh
	};
	~snr = {|t_trig|
		(CaosSnare.seq(t_trig,rel:1/8)*0.3).tanh
	};

	~mix = ~bmb+~hhc+~snr;

	i = INSTRUMENT.new.play;

	k = Proxy.new(\k1,~bmb);
	// k.play;
	k.seq(P(\t_trig,[1,0,1,0,0,0,0,1]),[\speed,6]);
	// k.seq(P(\t_trig,[1,0,1,0,0,0,0,1]),[\speed,3]);
	k.set(P(\lowcutfreq,[2,3,5,7.5]*100),[\speed,6,\repeat,2]);
	k.seq(P(\rel,[2,3,5,7.5]/8),[\speed,6,\repeat,2]);
	k.seq(P(\amp1,[2,3,5,7.5]/8),[\speed,6,\repeat,2]);
	k.seq(P(\amp2,[2,3,5,7.5].mirror/8),[\speed,12,\repeat,2]);
	// k.seq(P(\t_trig,[1,0,1,0,0,0,0,1]),[\speed,4]);
	// k.seq(P(\t_trig,[1,0,0]),[\speed,8,\repeats,16]);

	h = Proxy.new(\h1,~hhc);
	h.play;
	h.seq(P(\t_trig,[1,0,1,1,1,0,0,1].pyramid.mirror),[\speed,6]);
	// h.seq(P(\t_trig,[1,0,1,1,1,0,0,1].pyramid.mirror),[\speed,9,\repeat,1]);
	// h.seq(P(\t_trig,[1,0,1,1,1,0,0,1].pyramid.mirror),[\speed,12,\repeat,0.5]);
	// h.seq(P(\t_trig,[1,0,1,1,1,0,0,1]),[\speed,8,\repeat,4]);

	z = Proxy.new(\z1,~snr);
	z.play;
	z.seq(P(\t_trig,[1,0,1,0,0,0,0,1].pyramid.mirror),[\speed,3]);
	// z.seq(P(\t_trig,[1,0,1,0,0,0,0,1].pyramid.mirror),[\speed,6]);
	// z.seq(P(\t_trig,[1,0,1,0,0,0,0,1].pyramid.mirror),[\speed,9]);
	// z.seq(P(\t_trig,[1,0,1,0,0,0,0,1].pyramid.mirror),[\speed,24]);

)


Tdef(\miniloop,{
		inf.do{|i|
			var j = (i / 8).floor;
			k.play(j);
			h.play(j);
			z.play(j);
			8.wait;
		}
}).stop;

k.stop;
z.stop;


~mix = ~bmb + ~hhc + ~snr + ~btk


~mix.fadeTime = 30;

~btk = {
	FreeVerb.ar(~hhc.ar + ~snr.ar,1/2,2/3)
}
