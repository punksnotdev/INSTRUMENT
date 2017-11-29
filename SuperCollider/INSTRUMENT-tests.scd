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

	var i = Instrument.new(\instrument);

	i == g.nodes[i.name]

};

t.tests["Node Graph Instruments Communication"] = {

	var g = NodeGraph.new;

	var h = Instrument.new(\instrument1);
	var i = Instrument.new(\instrument2);

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

t.tests["Instrument has Sequencer"] = {

	var g = NodeGraph.new;

	var i = Sequenceable.new(\instrument);

	g.sequencer == i.sequencer

};

t.run;
