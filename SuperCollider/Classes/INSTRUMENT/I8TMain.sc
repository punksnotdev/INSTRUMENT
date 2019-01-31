I8TMain
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

	var <synths;

	var <> data;

	var lastMap;

	*new {
		// rootNode.graph_(this);
		^super.new.init();

	}
	init {


		nodes = Dictionary.new;
		sequencer = Sequencer.new(this);
		controllerManager = ControllerManager.new(this);

		nodes = IdentityDictionary.new;
		groups = IdentityDictionary.new;


		nextKey = 0;

		rootNode = I8TNode.new(this,"rootNode");
		this.addNode( rootNode );

		this.play;

		midiControllers = ();
		midiControllers.inputs = List.new;
		midiControllers.outputs = List.new;

		autoMIDI = false;
		nextMIDIController = -1;

		// this.setupGUI();

		// dictionary for placing custom data:
		data = ();

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
		if( controller.isKindOf(MIDIController), {

			if( target.isKindOf(Instrument), {
				^controllerManager.map(controller,target,parameter,range);
			}, {
				"Target not of class 'Instrument'"
			});

		}, {
			"Controller not of class 'MIDIController'"
		});

	}
	unmap {|controller,target,parameter|
		^controllerManager.unmap(controller,target,parameter);
	}

	setupGUI {

		^gui = I8TGUI(this);

	}

	midi_ {|on=false|

		controllerManager.midi_( on );

	}

	midi {
		^controllerManager.midi.devices;
	}

	clock {
		^TempoClock.default.tempo;
	}
	tempo {
		^TempoClock.default.tempo*120;
	}
	bpm {
		^this.tempo();
	}

	clock_ {|clock|
		TempoClock.default.tempo = clock;
	}
	tempo_ {|bpm|
		TempoClock.default.tempo = bpm/120;
	}
	bpm_ {|bpm|
		this.tempo( bpm );
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

			newGroup.name = key;


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


		if( midiControllers.inputs.notNil ) {

			if( (autoMIDI == true) && (midiControllers.inputs.size > 0),{



				if( item.notNil ) {

					if( item.isKindOf(InstrumentGroup) || item.isKindOf(Instrument) ) {

						var nextIndex;
						var next;
						var shouldIncrement = true;

						controllerManager.controlTargetMap.collect({
							|mapping_,key_|

							var mapping, key;

							if( mapping_.isKindOf(List) ) {
								mapping = mapping_[0];
							};


							if( mapping_.isKindOf(Event) ) {

								mapping = mapping_;

								if( mapping.target.name == item.name ) {

									shouldIncrement = false;

									midiControllers.inputs.collect({|input|

										if(input.isKindOf(MIDIController)){

											if(input.key==key) {
												next=input
											}
										}

									});

								}
							};

						});

						if( shouldIncrement == true ) {
							nextMIDIController =  ( nextMIDIController + 1 ) % midiControllers.inputs.size;
							next = midiControllers.inputs[nextMIDIController];
						};

						if( (next.notNil && item.notNil),{

							this.map( next, item, \amp,[0,1]);

						});

					}

				}

			});

		}

		^item;

	}


	synths_ {|list|


		var newList=List.new;
		var counter = 0;

		// store synths dictionary
		synths = list;


		// convert to array for displaying in a list
		synths.keysValuesDo({|k,v|

			if( v.isKindOf(Event)) {
				counter = counter + 1;
				newList.add(k);
				counter = counter + 1;

				v.keysValuesDo({|key,value|
					newList.add(value);
					counter = counter + 1;
				});
			}
		});


		if( gui.notNil ) {
			gui.synthdefs_(newList.asArray, {
				arg ...args;
				"synths gui callback:".postln;
				args.postln;
			});
		};

	}


	displayTracks {

		var tracks = List.new;

		sequencer.sequencer_tracks.keysValuesDo({|k,v|
			var track = ();

			track.name=v.name;

			if( v.playing == true ) {
				track.playing=true
			};

			tracks.add(track);
		});

		if( gui.notNil ) {
			gui.tracks = tracks.asArray;
		}

	}

	selectPlayingTracks{|selection|
		[selection].postln;
		// sequencer.sequencer_tracks.collect({|track,index|
		// 	if( selection.indexOf(index).notNil, {
		// 		"play".postln;
		// 		track.play;
		// 	}, {
		// 		// if(track.playing == true) {
		// 			"stop".postln;
		// 			track.stop;
		// 		// }
		// 	});
		// })


	}

	displayNextPattern {|nextPattern|

		if( gui.notNil ) {
			gui.currentPattern = nextPattern;
		}
	}

}
