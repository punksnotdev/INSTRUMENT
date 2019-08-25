I8TMain : Event
{

	classvar instance;

	var ready;
	var awaitingReadyBeforePlay;

	var <threadID;
	var <>clock;

	var <nodes;
	var <rootNode;

	var <playing;
	var <>sequencer;
	var <>mixer;
	var <>controllerManager;

	var nodes;
	var <groups;

	var midi;

	var <gui;

	var <speed;

	var <nextKey;

	var autoMIDI;

	var nextMIDIController;

	var <midiControllers;

	var synths;

	var <> data;

	var lastMap;

	var currentFolder;



	classvar mainEvent;

	*new {|createNew=false|

		if(( instance.isNil || createNew == true), {

			^super.new.init(createNew);

		}, {

			^instance;

		});

	}

	init {|createNew=false|


		clock = TempoClock.default;
		// clock = TempoClock.new( TempoClock.default.tempo );

		ready = false;

		if( Server.local.serverRunning, {

			if( createNew==false ) {
				instance = this;
			};

			CmdPeriod.add({
				this.kill()
			});


			nodes = Dictionary.new;
			sequencer = Sequencer.new(this);
			mixer = I8TMixer.new(this);
			controllerManager = ControllerManager.new(this);

			nodes = IdentityDictionary.new;
			groups = IdentityDictionary.new;


			nextKey = 0;

			rootNode = I8TNode.new(this,"rootNode");

			this.addNode( rootNode );

			// this.play;

			midiControllers = ();
			midiControllers.inputs = List.new;
			midiControllers.outputs = List.new;

			autoMIDI = false;
			nextMIDIController = -1;

			// this.setupGUI();

			// dictionary for placing custom data:
			data = ();

			data.synths = () ;
			data.synths.parameters = ();


			synths = this.loadSynths();

			currentFolder = synths;

			ready = true;

			if( awaitingReadyBeforePlay == true ) {
				this.play;
			};

			thisThread.randSeed = 10e6.rand;
			threadID = 10e12.rand;

			"I N S T R U M E N T".postln;

			if( createNew == true, {
				^this
			}, {
				^instance
			});

		}, {

			"SuperCollider server not running".warn;

			^this

		});



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

	removeNode{|key|
		nodes[key].stop;
		nodes.removeAt(key);
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

		if( ready == true, {

			playing = true;
			nodes.collect({|node|
				node.play;
			});
			sequencer.play;

		}, {
			awaitingReadyBeforePlay = true;
		});
	}
	pause {
		playing=false;
		sequencer.pause;
	}
	stop {

		playing=false;

		nodes.collect({|node|
			node.stop;
		});

		sequencer.stop;

		"I N S T R U M E N T stopped".postln;

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

			if( (
				target.isKindOf(Sequenceable)
				||
				target.isKindOf(InstrumentGroup)
				||
				target.isKindOf(ControllerLogic)
			), {
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

		^controllerManager.midi_( on );
	}

	startMidi {
		controllerManager.midi_( true );
	}

	midi {
		^controllerManager.midi.devices;
	}


	tempo {
		^clock.tempo*120;
	}
	bpm {
		^this.tempo();
	}

	tempo_ {|bpm|
		clock.tempo = bpm/120;
	}

	bpm_ {|bpm|
		this.tempo( bpm );
	}


	put {|key,something|

		var item;

		nextKey = key;

		if( something.isNil, {

			if( nodes[key].notNil, {
				^nodes[key]
			});

			if( groups[key].notNil, {
				^groups[key]
			});

			^nil;

		}, {


				if( something.isKindOf(I8TNode), {

					if( nodes[key].isNil, {

						item = this.addNode(something,key);

						if( playing == true ) {
							item.play;
						};

						mixer.addChannel( something );

					}, {

						item = nodes[key];
						item.setContent(something);

					});

				});

				if( something.isKindOf(Collection)) {

					item = this.createGroup( something, key );

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

								(["SHOULD AUTOMIDI!",next,item]).postln;

								if( (next.notNil && item.notNil),{

									this.map( next, item, \amp,[0,1]);

								});

							}

						};

						autoMIDI = false;
						("Assigned MIDI controller:"++nextMIDIController).postln;
						"Auto MIDI disabled".postln;
					});

				};

				super.put(key,item);

				^item;

			});

	}

	createGroup {|group_,key_|

		if( key_.notNil ) {

			var item;

			var newGroup;

			var allValid = true;

			var key = key_;

			group_.collect({|childItem|
				if( (( childItem.isKindOf(Symbol) ) || ( childItem.isKindOf(I8TNode) )) == false) {
					allValid = false;
				};
			});

			if( allValid == true ) {

				if( groups[key].notNil, {

					var currentGroup = groups[key];

					// disable any synths not included in new groups


					group_.collect({|childItem,childItemKey|

						if( currentGroup.keys.includes(childItemKey) == true,
						{
							currentGroup.at(childItemKey).synthdef=childItem.synthdef;
							// currentGroup.at(childItemKey).play;
						},
						{

							if( childItem.isKindOf(I8TNode) ) {

							  if( (nodes.includes( childItem ) == false), {

								childItem.name=childItemKey;

								this.setupMixerNode( childItem );

							  });

							  currentGroup.put( childItemKey, childItem );

							};


						});

					});

					currentGroup.collect({|childItem,childItemKey|

						if(  group_.keys.includes(childItemKey) == false ) {
							currentGroup.stop(childItemKey);
						};

					});



					mixer.addChannel( currentGroup );

					item = currentGroup;

				}, {


					if( group_.isKindOf(InstrumentGroup), {

						newGroup = group_;

						newGroup.name = key;


					}, {

						newGroup = InstrumentGroup.new;

						newGroup.name = key;

						group_.collect({

						  arg childItem,childItemKey;

						  // if childItem name is a node, add it
						  if( childItem.isKindOf(I8TNode) ) {

							if( (nodes.includes( childItem ) == false), {

							  childItem.name=key++'_'++childItemKey;

							  this.setupMixerNode( childItem );

							});

							newGroup.put( childItemKey, childItem );

						  };

						  if( childItem.isKindOf(InstrumentGroup) ) {
							if( groups.includes(childItem) ) {
							  newGroup.put( childItem.name, childItem );
							};
						  };

						  if( childItem.isKindOf(Symbol)) {

							var childItemName = childItem;

							if( nodes[childItemName].notNil, {
							  newGroup.put( childItemName, nodes[childItemName] );
							}, {
							  // if childItem name is a group
							  if( groups[childItemName].notNil, {
								// add its childItems
								newGroup.put( childItemName, groups[childItemName] );
							  });
							});

						  }

						});

					});


					newGroup.main = this;


					groups[key] = newGroup;

					item = groups[key];

					mixer.addChannel( newGroup );

					
					newGroup.sequencer = sequencer;

					sequencer.registerInstrument(newGroup);

					controllerManager.addInstrument( newGroup, key );

					if( playing ) {
						newGroup.play;
					};

				});


			};


			^item;

		}

	}


	updateMixerGroup {|group|

		if( group.isKindOf(InstrumentGroup)) {

			if( group.name.notNil, {

				^this.createGroup(group,group.name);

			}, {

				"Main: cannot update group with nil name".postln;

			});

		}

	}

	setupMixerNode {|node|
		if( node.isKindOf( I8TNode ) ) {
			if( nodes.includes( node ) == false ) {

				this.addNode( node, node.name );

				if( playing == true ) {
					node.play;
				};

				// if( mixer[group.name].isNil ) {
				// 	mixer[group.name] = group;
				// };
				//
				node.mixerChannel = mixer.addChannel( node );

			};
		};

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

	autoMIDI {|enabled|
		if( ( enabled.isKindOf(Number) || enabled.isNil), {
			if(enabled != false ){

				autoMIDI = true;
				if( (enabled.isKindOf(Number) )) {
					if( ( enabled>=0 ) && (enabled < midiControllers.inputs.size )) {
						nextMIDIController = enabled-1;
					};
				};
				"Auto MIDI enabled".postln;
			};
		}, {
			autoMIDI = false;
			"Auto MIDI disabled".postln;
		});
	}

	autoMIDI_ {|enabled|
		this.autoMIDI(enabled);
	}

	listSynths {|item|

		if( item.isNil ) {
			this.listSynths(synths);
		};

		// item.postln;
		if( item.isKindOf(Collection) ) {
			item.collect({|value,key|
				"".postln;
				"---------------".postln;
				key.postln;
				"---------------".postln;
				value.keysValuesDo({|k,v|
					v.postln;
				});
				// ([value, key, value[key]]).postln;
				// this.listSynths(value[key]);
			});
		};

	}

	synths {
		^synths
	}

	loadSynths {|path, parent, grandparent, greatgrandparent|


		var files;
		var folder;
		// var level;
		var scdFiles = List.new;
		var folders = List.new;

		var items = I8TFolder();


		if( path.notNil, {
			files = path.pathMatch;
		}, {
			files = (Platform.userExtensionDir++"/INSTRUMENT/Sounds/SynthDefs/*").pathMatch;
		});

		if( parent.isNil ) {
			parent = I8TFolder();
		};

		folder = I8TFolder();


		files.collect({|fileName, index|

			var pathName = PathName( fileName );

			if( pathName.isFile ) {
				scdFiles.add( fileName );
			};

			if( pathName.isFolder ) {
				folders.add( fileName );
			};


		});



		scdFiles.collect({|fileSrc, index|

			var pathName = PathName( fileSrc );

			var fileName = pathName.fileNameWithoutExtension;

			var synthdef = fileSrc.load;

			if( synthdef.isKindOf(SynthDef)) {

				items[ fileName.asSymbol ]		= synthdef.name.asSymbol;
				items[ items.keys.size - 1 ]	= synthdef.name.asSymbol;

				if( data.synths.parameters.isKindOf(Event) ) {

					var parameterNames;

					parameterNames = synthdef.allControlNames.collect({|ctl|
						ctl.name.asSymbol
					});

					data.synths.parameters[synthdef.name] = parameterNames;

					// parameterNames.postln;

				};

			};


		});


		folders.collect({|folderSrc, index|

			var pathName = PathName( folderSrc );

			var folderName = pathName.folderName.toLower.asSymbol;

			// "-------".postln;
			// folderName.postln;
			// "-------".postln;

			items[folderName]=this.loadSynths( folderSrc++"*", folder, parent, grandparent );

		});



		items.keysValuesDo({|k,v|

			var uniqueValues = Set.new();

			folder[k]=v;

// should check if key exists, then create list!
			parent[k] = v;

			if( grandparent.notNil ) {
				grandparent[k] = v;
			};

			if( greatgrandparent.notNil ) {
				greatgrandparent[k] = v;
			};
		});



		this.makeFolderIndexes(folder);

		synths = folder;

		^folder

	}

	makeFolderIndexes {|folder|

		var synthDefs = List.new;

		// delete numeric indexes
		folder.keysValuesDo({|k,v|
			if(k.isNumber||v.isNil) {
				folder.removeAt(k);
			};
		});

		(folder.values).collect({|value|
			if(value.isKindOf(String)||value.isKindOf(Symbol)){
				synthDefs.add(value);
			};
		});

		synthDefs.as(Set).asArray.sort.collect({|synthdef,i|
			folder[i]=synthdef;
		});

	}


	kill {
		if( instance.notNil ) {
			this.stop();
			instance = nil;

			nodes = Dictionary.new;

			"I N S T R U M E N T killed.".warn;
		}
	}


}
