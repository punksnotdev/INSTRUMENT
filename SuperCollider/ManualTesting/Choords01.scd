(

	p = ProxySpace.push(s);
	p.quant=4;
	i = INSTRUMENT.new.play;

	~chords = {|t_trig,chord=\m,note=50|
		CaosChords.seq(t_trig,chord,note)
	};
	~btk1 = {
		CaosKick.robot(t:1/2,tp:1/2)
	};
	~btk2 = {
		CaosSnare.robot(t:1/4) / 3
	};
	~btk3 = {
		CaosHats.robot(t:1,tp:[0,2,3,5,6,7,9]/16) ! 2
	};

	~chords.play;
	~btk1.play;
	~btk2.play;
	~btk3.play;

	x = Proxy(\rgg,~chords);
	x.play;
	x.seq(P(\chord,[\m,\M]));
	x.seq(P(\note,[57,50]));
)
