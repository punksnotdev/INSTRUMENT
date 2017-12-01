t = Tests.new;


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

t.tests["Instrument is in Node Graph"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	i == g.nodes[i.name]

};

t.tests["Node Graph Instruments Communication"] = {

	var g = NodeGraph.new;

	var h = Instrument.new("testInstrument1");
	var i = Instrument.new("testInstrument2");
	var j = Instrument.new("testInstrument3");

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


t.tests["Instrument has Sequencer Track"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("trackInstrument");

	g.sequencer.instruments[i.name].isKindOf(SequencerTrack);

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

	if( s.instruments["noInstrument"] == nil, {
		sequenceCountBefore = 0;
	});

	s.addPattern("noInstrument", "newPattern1", newPattern1 );
	s.addPattern("noInstrument", "newPattern2", newPattern2 );

	// test it!
	s.addPattern("noInstrument", "newPattern1", newPattern1 );
	s.addPattern("noInstrument", "newPattern2", newPattern2 );

	patterns = s.instruments["noInstrument"].patterns;

	patternsCorrect =
	patterns["newPattern1"] == newPattern1 &&
	patterns["newPattern2"] == newPattern2 &&
	patterns.size == 2;

	sequenceCorrect = s.instruments["noInstrument"].sequence.size - sequenceCountBefore == 2;

	patternsCorrect && sequenceCorrect;

};


t.tests["Instrument adds Pattern to Sequencer"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	var patterns;

	var newPattern1 = [1,0,0,1,0,1,0,0];
	var newPattern2 = [1,0,1,0,1,0,0,0];
	var newPattern3 = [1,0,0,0,1,0,1,0];

	i.seq(newPattern1);

	// patterns with no keys get added even when identical
	i.seq(newPattern2,4);
	i.seq(newPattern2,4);

	// patterns with keys dont get duplicated:
	i.seq(newPattern3,0,"newPattern3");
	i.seq(newPattern2,0,"newPattern3");

	patterns = g.sequencer.instruments[i.name].patterns;

	patterns[0] == newPattern1 &&
	patterns[1] == newPattern2 &&
	patterns["newPattern3"] == newPattern2 &&
	patterns.size == 4 &&
	g.sequencer.instruments[i.name].sequence.size == 4;

};



t.tests["Sequencer stores Repetitions"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	var patterns;

	var newPattern1 = [1,0,0,1,0,1,0,0];
	var newPattern2 = [1,0,1,0,1,0,0,0];

	i.seq(newPattern1,4);
	i.seq(newPattern1,6);

	g.sequencer.instruments[i.name].sequence[0].parameters["repetitions"] == 4 &&
	g.sequencer.instruments[i.name].sequence[1].parameters["repetitions"] == 6;


};



t.tests["Instrument triggers play in Sequencer"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	var patterns;

	var newPattern1 = [1,0,0,1,0,1,0,0];
	var newPattern2 = [1,0,1,0,1,0,0,0];

	i.seq(newPattern1,2);
	i.seq(newPattern1,4);

	i == i.play();

};


// t.tests["Sequencer plays Instrument"] = {
//
// 	false
//
// };

t.run;
