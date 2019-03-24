s.boot;
s.doWhenBooted({

	i = INSTRUMENT();
	i.play;
	k = INSTRUMENT(\kickElectro);
	i[\kick] = k;

	i[\notes] = INSTRUMENT(\distRing);

	i.kick.amp=1/8;

		i.kick.seq("1 2  5");
	i.notes.note("0 2  3");

	// i.kick[1].seq("1 2  5").speed(4).mirror.x(2);
	// i.kick[2].seq("1 2  5").speed(16).pyramid.x(1);




	// i.notes.amp=1/1.5;
	//
	// i.notes.set(\rel,1/4);
	// i.notes.amp=1.25;

	// i.notes.mode=\mono;
	// i.notes.mode.postln;
	// i[\notes].mode=\poly;



	i.notes.amp=1/2;

});
