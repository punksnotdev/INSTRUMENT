(

	g = NodeGraph.new;

	g.sequencer.play;

	i = Instrument.new("test1");

	i.seq([1,0,0,0,1,0,0,1]);
	i.seq([1,0,1,1,1,0,0,1]);
	
	i.play;


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
