(

	p = ProxySpace.push(s);
	p.quant=4;
	i = INSTRUMENT.new.play;

	~z = {|note=60,chord|

		Mix.new(Pan2.ar(SinOsc.ar( CaosChords.kr(note,chord),0,1.3).tanh)/14) ! 1
	};

	~z.play;
	~z.set(\chord,'M');

	x = Proxy(\rggg,~z);
	x.play;
	// x.seq(P(\chord,[\m,\M]));
	// x.set(P(\note,[57,50].midicps),[\speed,8]);
	// x.seq(P(\note,[55,54].midicps));
	// x.set(P(\note,[55,64].midicps),[\speed,16]);
	// x.seq(P(\note,[187,80].midicps));
)
