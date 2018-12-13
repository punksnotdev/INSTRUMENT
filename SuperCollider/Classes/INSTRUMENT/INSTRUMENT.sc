INSTRUMENT
{

	var <nodes;
	var <rootNode;

	var <playing;
	var <>sequencer;
	var <>controllerManager;

	var nodes;
	var groups;

	var midi;

	var <gui;

	var <speed;

	var <nextKey;

	var <>autoMIDI;
	var nextMIDIController;

	var <midiControllers;


	*new {
		// rootNode.graph_(this);
		^super.new.init();

	}
	init {

		nodes = Dictionary.new;
		sequencer = Sequencer.new;
		controllerManager = ControllerManager.new(this);

		nodes = IdentityDictionary.new;
		groups = IdentityDictionary.new;


		nextKey = 0;

		rootNode = I8TNode.new(this,"rootNode");
		this.addNode( rootNode );

		this.play;

		midiControllers = ();
		autoMIDI = false;
		nextMIDIController = 0;

	}

	addNode {|node,key|

		if( key.isNil) {
			key = nextKey;
		};

		if( nodes[key].notNil ) {
			nodes.removeAt(key);
		};

		if( node.name != "rootNode", {

			node.name = key;

			nodes[key] = node;

			if( node.isKindOf(Sequenceable), {
				node.sequencer = sequencer;
				sequencer.registerInstrument(node);
				controllerManager.addInstrument( node, key );
			})

		});

		^nodes[key]

	}



	free {|node|
		if( node.isKindOf(Sequenceable), {
			sequencer.unregisterInstrument(node);
		});
		nodes[node.name] = nil;
	}

	speed_ {|speed_|
		sequencer.speed = speed_;
	}

	play {
		playing = true;
		sequencer.play;
	}
	pause {
		sequencer.pause;
	}
	stop {
		sequencer.stop;
	}

	go {|time=0|

		sequencer.go(time);

	}

	when {|time, function|
		if( time.isInteger, {
			if( ((time.notNil) && ( function.isKindOf(Function) )),{

				sequencer.singleFunctions[time] = function;

			}, {

				if(sequencer.singleFunctions[time].isKindOf(Function), {
					sequencer.singleFunctions.removeAt(time);
				});

			});

		}, {
			"time should be an Integer".postln;
		});
	}

	clearSequencerFunctions {
		sequencer.clearRepeatFunctions();
	}

	every {|time, function, wait|
		if( time.isInteger, {
			if(function.notNil,{
			if( ((time.isInteger) && ( function.isKindOf(Function) )),{
				if( sequencer.repeatFunctions[time].isKindOf(List), {
					sequencer.repeatFunctions[time].add( ( function:function, offset: wait ));
				}, {
					sequencer.repeatFunctions[time] = List.new;
					sequencer.repeatFunctions[time].add( ( function:function, offset: wait ) );
				});

			}, {

				if(sequencer.repeatFunctions[time].isKindOf(Function), {
					sequencer.repeatFunctions.removeAt(time);
				});

			});

			}, {
				sequencer.repeatFunctions[time] = nil;

			});
		}, {
			"time should be an Integer".postln;
		});

	}

	//
	// dontPlay {|instruments, lengths|
	//
	// 	var payload;
	//
	// 	payload = ();
	//
	// 	if( instruments.notNil, {
	//
	// 		payload.instruments = instruments;
	//
	// 		if( (instruments.isKindOf(Instrument)), {
	// 			payload.instruments = [instruments];
	// 		});
	//
	// 	});
	// 	if( lengths.notNil, {
	//
	// 		payload.lengths = lengths;
	//
	// 		if( (lengths.isInteger||lengths.isFloat), {
	// 			payload.lengths = [lengths];
	// 		});
	//
	// 	});
	//

	//
	//
	// }
	//
	// for {}

	mapController {|ctlDesc|
		if(ctlDesc.controllers.isArray, {
			ctlDesc.controllers.collect({|controller|
				//,index|

				// controller.index = index;

				controllerManager.map(
					controller, InstrumentController()
				);

			});
		});

	}


	map {|controller,target,parameter,range|
		^controllerManager.map(controller,target,parameter,range);
	}


	setupGUI {

		gui = GUII8t();

	}

	midi_ {|on=false|

		controllerManager.midi_( on );

	}

	midi {
		^controllerManager.midi.devices;
	}


	at{|key|

		if( nodes[key].notNil, {
			^nodes[key]
		});

		if( groups[key].notNil, {
			^groups[key]
		});

	}

	put{|key,something|

		var item;

		nextKey = key;


		if( something.isKindOf(I8TNode), {

			item = this.addNode(something,key);
			item.play;

		});

		if( something.isKindOf(Collection)) {

			var newGroup = InstrumentGroup.new;


			something.collect({
				arg itemName;

				// if item name is a node, add it
				if( nodes[itemName].notNil, {
					newGroup.add( nodes[itemName] );
				}, {
					// if item name is a group
					if( groups[itemName].notNil, {
						// add its items
						newGroup.add( groups[itemName] );
					});
				});
			});

			groups[key] = newGroup;

			item = groups[key];

		};

		if( autoMIDI == true,{

			if( item.notNil,{
				var next = midiControllers.inputs[nextMIDIController];

				if( next.notNil,{

					var shouldIncrement = this.map( next, item, \amp,[0,1]);

					if( shouldIncrement == true ) {
						nextMIDIController =  ( nextMIDIController + 1 ) % midiControllers.size;
					}

				});

			});

		});

		^item;

	}

}
