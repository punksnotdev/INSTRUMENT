t = Tests.new;


t.tests["Node Graph Nodes is Dictionary"] = {

	var g = INSTRUMENT.new;

	g.nodes.class == Dictionary;

};

t.tests["Node Graph Root Node Registered"] = {

	var g = INSTRUMENT.new;

	g.rootNode.nodeGraph == g

};

t.tests["Node Graph Root not in Nodes"] = {

	var g = INSTRUMENT.new;


	g.nodes[g.rootNode.name] == nil;

};

t.tests["Instrument is in Node Graph"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	i == g.nodes[i.name]

};



t.tests["Instrument is removed from Node Graph"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	var nodeName = i.name;

	i.free;

	g.nodes[nodeName] == nil;

};

t.tests["Node Graph Instruments Communication"] = {

	var g = INSTRUMENT.new;

	var h = Instrument.new(\testInstrument1);
	var i = Instrument.new(\testInstrument2);
	var j = Instrument.new(\testInstrument3);

	var isGraph = true;

	g.nodes.collect({|instrument|
		var notSameObject = g != instrument.nodeGraph;
		var notSameNodeCount = g.nodes.size != instrument.nodeGraph.nodes.size;
		// var notSettable;

		instrument.name = \new_name;
		// notSettable = instrument.name.compare( \new_name ) != 0;
		// if(	notSameObject || notSameNodeCount || notSettable, { isGraph = false; });
		if(	notSameObject || notSameNodeCount, { isGraph = false; });
	});

	isGraph

};

t.tests["Graph has Sequencer"] = {

	var g = INSTRUMENT.new;

	var s = g.sequencer;

	s.class.name.asString == "Sequencer";

};


t.tests["Instrument has Patterns & Sequence references"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	i.seq( P(\note,[0,2,3,5]) );

	i.patterns(\note) == g.sequencer.instruments[i.name].parameterTracks[\note].patterns &&
	i.sequence(\note) == g.sequencer.instruments[i.name].parameterTracks[\note].sequence &&
	i.sequenceInfo(\note) == g.sequencer.instruments[i.name].parameterTracks[\note].sequenceInfo
};

t.tests["Instrument has Sequencer Track"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	g.sequencer.instruments[i.name].isKindOf(SequencerTrack);

};

t.tests["Deleting Node removes corresponding Sequencer Track"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	i.free;

	g.sequencer.instruments[i.name] == nil

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

	g = INSTRUMENT.new;

	listener = I8Tnode.new(\testListener);

	event = I8Tevent.new( listener, callback, \testEvent );

	event.parameters[\testParameter] = "testValue";

	result = event.execute();

	event.name == result.name &&
	event.parameters == result.parameters;


};

t.tests["SequencerEvents call Listeners"] = {

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

	g = INSTRUMENT.new;

	listener = I8Tnode.new(\testListener);

	event = SequencerEvent.new( [\param, 1], "testEvent" );

	event.parameters[\testParameter] = "testValue";

	result = event.execute();

	event.name == result.name &&
	event.parameters == result.parameters;

};



t.tests["Sequencer stores Patterns"] = {

	var g = INSTRUMENT.new;

	var s = g.sequencer;

	var patterns;
	var sequence;

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);
	var newPattern2 = P(\test,[1,0,1,0,1,0,0,0]);
	var newPattern3 = P(\test,[1,1,1,0,0,0,1,0]);

	var patternsCorrect;
	var sequenceCountBefore;
	var sequenceCorrect;

	if( s.instruments[\noInstrument] == nil, {
		sequenceCountBefore = 0;
	});

	s.seq(\noInstrument, \newPattern1, newPattern1 );
	s.seq(\noInstrument, \newPattern2, newPattern2 );

	// test it!
	s.seq(\noInstrument, \newPattern1, newPattern1 );
	s.seq(\noInstrument, \newPattern2, newPattern2 );

	patterns = s.instruments[\noInstrument].parameterTracks[\test].patterns;
	sequence = s.instruments[\noInstrument].parameterTracks[\test].sequence;

	patternsCorrect =
	patterns[\newPattern1] == newPattern1 &&
	patterns[\newPattern2] == newPattern2 &&
	patterns.size == 2;

	sequenceCorrect =
	sequence[0].pattern == newPattern1 &&
	sequence[1].pattern == newPattern2 &&
	sequence[2].pattern == newPattern1 &&
	sequence[3].pattern == newPattern2 &&
	sequence.size - sequenceCountBefore == 4;

	patternsCorrect && sequenceCorrect;


};


t.tests["Sequencer stores Patterns in Sequence in chronological order when key and value equal"] = {

	var g = INSTRUMENT.new;

	var s = g.sequencer;

	var patterns;

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);

	var paramsCorrect1 = false;
	var paramsCorrect2 = false;
	var paramsCorrect3 = false;
	var trackSequence;

	// use repetitions and speed to check correctness

	s.seq(\noInstrument, 0, newPattern1, [\repeat,2]);
	s.seq(\noInstrument, 0, newPattern1, [\speed,0.25]);
	s.seq(\noInstrument, 0, newPattern1, [\repeat,6]);

	trackSequence = s.instruments[\noInstrument].parameterTracks[\test].sequence;

	paramsCorrect1 = trackSequence[0].parameters[\repeat] == 2;
	paramsCorrect2 = trackSequence[1].parameters[\speed] == 0.25;
	paramsCorrect3 = trackSequence[2].parameters[\repeat] == 6;

	trackSequence.size == 3 &&
	paramsCorrect1 &&
	paramsCorrect2 &&
	paramsCorrect3;

};




t.tests["Sequencer removes Patterns"] = {

	var g = INSTRUMENT.new;

	var s = g.sequencer;

	var patterns;
	var sequence;

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);
	var newPattern2 = P(\test,[1,0,1,0,1,0,0,0]);
	var newPattern3 = P(\test,[1,1,1,0,0,0,1,0]);
	var newPattern4 = P(\test,[1,0,0,0,0,0,1,0]);

	var patternsCountBefore;
	var sequenceCountBefore;

	if( s.instruments[\noInstrument] == nil, {
		sequenceCountBefore = 0;
	});

	s.seq(\noInstrument, 0, newPattern1 );
	s.seq(\noInstrument, 1, newPattern2 );
	s.seq(\noInstrument, \newPattern3, newPattern3 );
	s.seq(\noInstrument, \newPattern4, newPattern4 );

	patterns = s.instruments[\noInstrument].parameterTracks[\test].patterns;
	sequence = s.instruments[\noInstrument].parameterTracks[\test].sequence;

	patternsCountBefore = patterns.size;
	sequenceCountBefore = sequence.size;

	s.removePattern(\noInstrument, 0 );
	s.removePattern(\noInstrument,  1 );
	s.removePattern(\noInstrument, \newPattern3 );
	s.removePattern(\noInstrument, newPattern4 );

	patternsCountBefore == 4 &&
	sequenceCountBefore == 4 &&
	patterns.size == 0 &&
	sequence.size == 0

};



t.tests["Sequencer removes Patterns from Sequence in reverse chronological order when value equal"] = {

	var g = INSTRUMENT.new;

	var s = g.sequencer;

	var patterns;

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);

	var paramsCorrect1 = false;
	var paramsCorrect2 = false;
	var paramsCorrect3 = false;
	var trackSequence;
	var size;
	// use repetitions to check correctness

	s.seq(\noInstrument, 0, newPattern1, [\repeat,2]);
	s.seq(\noInstrument, 0, newPattern1, [\repeat,4]);
	s.seq(\noInstrument, 0, newPattern1, [\repeat,6]);

	trackSequence = s.instruments[\noInstrument].parameterTracks[\test].sequence;

	size = trackSequence.size-1;
	paramsCorrect1 = trackSequence[size].parameters[\repeat] == 6;

	s.removePattern(\noInstrument,  newPattern1 );

	size = trackSequence.size-1;
	paramsCorrect2 = trackSequence[size].parameters[\repeat] == 4;

	s.removePattern(\noInstrument,  newPattern1 );

	size = trackSequence.size-1;
	paramsCorrect3 = trackSequence[size].parameters[\repeat] == 2;


	s.removePattern(\noInstrument,  newPattern1 );

	paramsCorrect1 &&
	paramsCorrect2 &&
	paramsCorrect3;

};





t.tests["Instrument adds Pattern to Sequencer"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	var patterns;
	var sequence;

	var newPattern1 = P(\test,[1,1,1,1,1,1,1,1]);
	var newPattern2 = P(\test,[2,2,2,2,2,2,2,2]);
	var newPattern3 = P(\test,[3,3,3,3,3,3,3,3]);

	i.seq(newPattern1);

	i.seq(newPattern2,1,\newPattern2);

	// patterns with no keys dont added o 'patterns' when identical,
	// but to sequence yes.
	i.seq(newPattern3,[\repeat,4]);
	i.seq(newPattern3,[\repeat,4]);


	patterns = g.sequencer.instruments[i.name].parameterTracks[\test].patterns;
	sequence = g.sequencer.instruments[i.name].parameterTracks[\test].sequence;

	sequence[0].pattern == newPattern1 &&
	sequence[1].pattern == newPattern2 &&
	sequence[2].pattern == newPattern3
	&&
	patterns.size == 3 &&
	sequence.size == 4;


};


t.tests["Instrument removes Patterns from Sequencer"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	var patterns;

	var previousCount;

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);
	var newPattern2 = P(\test,[1,0,1,0,1,0,0,0]);

	i.seq(newPattern1);
	i.seq(newPattern2);
	i.seq(newPattern2,\newPattern2);

	patterns = g.sequencer.instruments[i.name].parameterTracks[\test].patterns;

	previousCount = patterns.size;

	i.removePattern(newPattern1);
	i.removePattern(newPattern2);
	i.removePattern(\newPattern2);

	previousCount == 3 && patterns.size == 0

};


t.tests["Sequencer stores Repetitions"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);
	var newPattern2 = P(\test,[1,0,1,0,1,0,0,0]);

	i.seq(newPattern1,[\repeat,4]);
	i.seq(newPattern1,[\repeat,6]);

	g.sequencer.instruments[i.name].parameterTracks[\test].sequence[0].parameters[\repeat] == 4 &&
	g.sequencer.instruments[i.name].parameterTracks[\test].sequence[1].parameters[\repeat] == 6;


};



t.tests["Instrument triggers play/stop in Sequencer"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	var newPattern1 = P(\test,[1,0,0,1,0,1,0,0]);
	var newPattern2 = P(\test,[1,0,1,0,1,0,0,0]);

	i.seq(newPattern1);

	i.play();

	if( g.sequencer.instruments[i.name].parameterTracks[\test].playing, {
		i.stop();
	});

	g.sequencer.instruments[i.name].parameterTracks[\test].playing == false

};


t.tests["Instrument sets SequencerTrack speed"] = {

	var g = INSTRUMENT.new;

	var i = Instrument.new(\testInstrument);

	i.play();

	i.speed = 5;

	g.sequencer.instruments[i.name].speed == i.speed
};


t.tests["Sequencer plays Instrument"] = {

	var g = INSTRUMENT.new;

	var s = g.sequencer;

	var i = Instrument.new(\testInstrument);

	i == s.instruments[i.name].instrument.trigger();

};


t.tests["SequencerTrack calculates totalBeats"] = {

	g = INSTRUMENT.new;

	i = Instrument.new("test1");

	i.seq(P(\test,[1,0,0,0,1,0,0,1]));
	i.seq(P(\test,[1,0,1,1,1,0,0,1]),[\repeat,1,\speed,0.5]);
	i.seq(P(\test,[1,0,1,1,1,0,0,1]),[\repeat,3,\speed,4]);

	g.sequencer.instruments[i.name].parameterTracks[\test].totalBeats() == 64;

};



t.run;
