(

	g = NodeGraph.new;

	g.sequencer.play;

	i = Instrument.new("test1");

	i.play;

	j = Instrument.new("test2");
	j.speed = 3;
	j.play;

)
