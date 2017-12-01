t = Tests.new;

// t.tests["TestA"] = {
//
// 	t=TestA.new;
// 	t.class.cv1;
//
// };

t.tests["Node Graph Nodes is Dictionary"] = {

	var g = NodeGraph.new;

	g.nodes.class == Dictionary;

};

t.tests["Node Graph Root Node Registered"] = {

	var g = NodeGraph.new;

	g.rootNode.nodeGraph == g

};

t.tests["Node Graph Root not in Nodes"] = {

	var g = NodeGraph.new;


	g.nodes[g.rootNode.name] == nil;

};

t.tests["Instrument Node Graph"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	i == g.nodes[i.name]

};

t.tests["Node Graph Instruments Communication"] = {

	var g = NodeGraph.new;

	var h = Instrument.new("testInstrument1");
	var i = Instrument.new("testInstrument2");

	var isGraph = true;

	g.nodes.collect({|instrument|
		var notSameObject = g != instrument.nodeGraph;
		var notSameNodeCount = g.nodes.size != instrument.nodeGraph.nodes.size;
		var notSettable;
		instrument.name = "new name";
		notSettable = instrument.name.compare("new name") != 0;
		if(	notSameObject || notSameNodeCount || notSettable, { isGraph = false; });
	});

	isGraph

};

t.tests["Graph has Sequencer"] = {

	var g = NodeGraph.new;

	var s = g.sequencer;

	s.class.name.asString == "Sequencer";

};


t.tests["Instrument has Sequencer"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	g.sequencer == i.sequencer

};


t.tests["Events with Parameters call Listeners"] = {

	var g;
	var event;
	var listener;
	var callback;
	var result;

	callback = {|event_,listener_|
		if(event_.listener == listener_,{
			event_;
		});
	};

	g = NodeGraph.new;

	listener = I8Tnode.new("testListener");

	event = I8Tevent.new( listener, callback, "testEvent" );

	event.parameters[\testParameter] = "testValue";

	result = event.execute();

	event.name == result.name &&
	event.parameters == result.parameters;


};



t.tests["Sequencer stores Patterns"] = {

	var g = NodeGraph.new;

	var s = g.sequencer;

	var patterns;

	var newPattern1 = [1,0,0,1,0,1,0,0];
	var newPattern2 = [1,0,1,0,1,0,0,0];
	var newPattern3 = [1,1,1,0,0,0,1,0];

	var patternsCorrect;
	var sequenceCountBefore;
	var sequenceCorrect;

	sequenceCountBefore = s.sequence.size;

	s.addPattern(\testTrack, "newPattern1", newPattern1 );
	s.addPattern(\testTrack, "newPattern2", newPattern2 );

	// test it!
	s.addPattern(\testTrack, "newPattern1", newPattern1 );
	s.addPattern(\testTrack, "newPattern2", newPattern2 );

	patterns = s.patterns[\testTrack];

	patternsCorrect =
	patterns["newPattern1"] == newPattern1 &&
	patterns["newPattern2"] == newPattern2 &&
	patterns.size == 2;

	sequenceCorrect = s.sequence.size - sequenceCountBefore == 2;

	patternsCorrect && sequenceCorrect;

};


//
// t.tests["Instrument adds Pattern to Sequencer"] = {
//
// 	var g = NodeGraph.new;
//
// 	var i = Instrument.new("testInstrument");
//
// 	var patterns;
//
// 	var newPattern1 = [1,0,0,1,0,1,0,0];
// 	var newPattern2 = [1,0,1,0,1,0,0,0];
//
// 	i.seq(newPattern1);
// 	i.seq(newPattern2,"newPattern2");
//
// 	patterns = g.sequencer.sequence[i.name];
//
// 	patterns[0] == newPattern1 &&
// 	patterns["newPattern2"] == newPattern2 &&
// 	patterns.size == 2;
//
// };
//


// t.tests["Sequencer stores Repetitions"] = {
//
// 	false
//
// };



// t.tests["Instrument triggers play in Sequencer"] = {
//
// 	false
//
// };


// t.tests["Sequencer plays Instrument"] = {
//
// 	false
//
// };

t.run;
