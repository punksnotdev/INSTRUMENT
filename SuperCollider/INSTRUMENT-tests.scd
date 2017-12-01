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


t.tests["Sequencer stores Patterns"] = {

	var g = NodeGraph.new;

	var s = g.sequencer;

	var patterns;

	var newPattern1 = [1,0,0,1,0,1,0,0];
	var newPattern2 = [1,0,1,0,1,0,0,0];

	s.seq(\testTrack, "newPattern1", newPattern1 );
	s.seq(\testTrack, "newPattern2", newPattern2 );

	patterns = s.patterns[\testTrack];


	patterns["newPattern1"] == newPattern1 &&
	patterns["newPattern2"] == newPattern2 &&
	patterns.size == 2


};



t.tests["Instrument adds Pattern to Sequencer"] = {

	var g = NodeGraph.new;

	var i = Instrument.new("testInstrument");

	var patterns;

	var newPattern1 = [1,0,0,1,0,1,0,0];
	var newPattern2 = [1,0,1,0,1,0,0,0];

	i.seq(newPattern1);
	i.seq(newPattern2,"newPattern2");

	patterns = g.sequencer.patterns[i.name];

	patterns[0] == newPattern1 &&
	patterns["newPattern2"] == newPattern2 &&
	patterns.size == 2;

};


// t.tests["Sequencer plays Patterns"] = {
//
// 	var g = NodeGraph.new;
//
// 	false
//
// };




// t.tests["Instrument triggers play in Sequencer"] = {
//
// 	var g = NodeGraph.new;
//
// 	var i = Instrument.new("testInstrument");
//
// 	false
//
// };


// t.tests["Sequencer plays Instrument"] = {
//
// 	var g = NodeGraph.new;
//
// 	var i = Instrument.new("testInstrument");
//
// 	false
//
// };

t.run;
