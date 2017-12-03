(

	g = NodeGraph.new;

	s = g.sequencer;

	i = Instrument.new("test1");

	i.seq([1,0,1,1],[\repeat,1,\speed,4]);

	i.seq([2,0,2,2],[\repeat,1,\speed,4]);

	i.seq([3,0,3,3],[\repeat,2,\speed,2]);

	s.instruments[i.name].speed = 8;

	s.play;
    //
	i.play;

	// g.sequencer.instruments[i.name].currentEvent.postln;


)



// (
//
// 	g = NodeGraph.new;
//
// 	g.sequencer.play;
//
// 	i = Instrument.new("test1");
//
// 	i.play;
//
// 	j = Instrument.new("test2");
// 	j.speed = 3;
// 	j.play;
//
// )
