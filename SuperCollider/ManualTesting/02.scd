(

	g = INSTRUMENT.new;

	q = g.sequencer;
	q.play;

	i = SynthPlayer.new(\test3);
	j = SynthPlayer.new(\test2,\bassTest);

	i.play;
	j.play;

	i.seq(P(\note,[0,2,3,5]),[\speed,2]);
	i.seq(P(\note,[7,8,7]),[\speed,8]);
	i.seq(P(\note,[5,7,5]),[\speed,8]);
	i.seq(P(\note,[5,7,5]),[\speed,8]);
	i.seq(P(\note,[0,2,3,5].pyramid.mirror),[\speed,8]);


	j.removePattern(P(\note,[5,7,5],[\speed,0.5]));
	j.seq(P(\note,[0,2,3,5].pyramid.mirror,[\speed,0.5]));
	j.seq(P(\cutoff,(77+[0,2,3,5]).midicps.pyramid.mirror,[\speed,5]));


	j.synth.set(\decay,3);
	j.synth.set(\distortion,1.3);
	j.synth.set(\cutoff,72.midicps);

	q.speed = 8;

	i.synth.postln;

)



i.play(0);
j.play(0);
