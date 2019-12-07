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
	var synthLoader;

	var <> data;

	var lastMap;

	var currentFolder;

	classvar mainEvent;

	var clearedGroups;
	var clearedNodes;
	var clearedFunctions;

	*new {|createNew=false|

		if(( instance.isNil || createNew == true), {

			^super.new.init(createNew);

		}, {

			^instance;

		});

	}
	*initClass {
		StartUp.add {
			var s = Server.local;
			var i = INSTRUMENT();
		}
	}

	init {|createNew=false|


		// clock = TempoClock.default;
		clock = TempoClock.new( TempoClock.default.tempo );

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
			mixer.setupMaster();
			mixer.sequencer = sequencer;

			controllerManager = ControllerManager.new(this);

			nodes = IdentityDictionary.new;
			groups = IdentityDictionary.new;


			nextKey = 0;

			rootNode = I8TNode.new(this,"rootNode");

			this.addNode( rootNode );


			midiControllers = ();
			midiControllers.inputs = List.new;
			midiControllers.outputs = List.new;

			autoMIDI = false;
			nextMIDIController = -1;

			// this.setupGUI();




			ready = true;

			if( awaitingReadyBeforePlay == true ) {
				this.play;
			};

			thisThread.randSeed = 10e6.rand;
			threadID = 10e12.rand;

			this.play;

			"".postln;
			"".postln;
			"I N S T R U M E N T".postln;
			"".postln;
			"".postln;


		});



		// warn user if SC not running when starting INSTRUMENT manually
		if(
			Server.local.serverRunning == false
		) {
			if( I8TSynthLoader.synthsLoaded.notNil, {
				"SC server not running: boot".warn;
			});
		};

		// dictionary for placing custom data:
		data = ();

		data.synths = () ;
		data.synths.parameters = ();


		synthLoader = I8TSynthLoader();

		synths = synthLoader.loadSynths();

		currentFolder = synths;

		if( createNew == true, {
			^this
		}, {
			^instance
		});


	}


	loadSynths {|path, parent|

		var folder;

		folder = synthLoader.loadSynths(path,parent);

		if( parent.isNil ) {
			synths = folder;
		};

	}



	setupNode {|node,key|

		var item;

		if( node.isKindOf(I8TNode) ) {

			if( nodes[key].isNil, {

				item = this.addNode(node,key);

				if( playing == true ) {
					item.play;
				};

				if( item.playing == true ) {
					item.play;
				};

				node.channel = mixer.addChannel( node );

				if( node.name.asString.find("kick").notNil ) {
					node.channel.free(\locut);
				};
			}, {

				item = nodes[key];

				if( item.playing == true ) {
					item.play;
				};

				item.setContent(node);

			});

			^item;

		};


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

				if(clearedNodes.notNil, {
					if(clearedNodes.includes(node)==false){
						node.play;
					};
				}, {
					node.play;
				});
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

	clear {

		clearedGroups=groups.copy;
		clearedNodes=List.new;

		clearedNodes=nodes.values.copy;
		clearedFunctions=sequencer.repeatFunctions.copy;

		groups.collect({|g|
			g.pause;
			g.collect({|gnode|
				clearedNodes.add(gnode);
			});
		 });
		nodes.collect({|n| n.pause; });

		this.clearSequencerFunctions();

		this.go(0);

	}

	restore {

		sequencer.repeatFunctions=clearedFunctions.copy;

		clearedNodes.collect({|n|
			n.play;
		});
		clearedGroups.collect({|g|
			g.play;
		});


		this.go(0);

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


				// create node(s) depending on input type:

				if( something.isKindOf(I8TNode), {

					item = this.setupNode( something, key );

				});

				if( something.isKindOf(I8TNode), {

					item = this.setupNode( something, key );

				});



				if( this.validateSynthName(something) ) {

					var synthdef = this.getSynthDefByName(something);

					if(this.validateSynthDef(synthdef)) {
						if(nodes[synthdef].notNil, {
							item = this.setupNode(nodes[synthdef], key);
						}, {
							item = this.setupNode(SynthPlayer(synthdef), key);
						});
					}

				};

				if( (
						something.isKindOf(SynthDef)
						||
						something.isKindOf(SynthDefVariant)
					)
				) {

					item = this.setupNode(SynthPlayer(something), key);

				};


				if( something.isKindOf(I8TFolder) ) {

					item = this.setupNode(SynthPlayer(something.getMainSynthDef), key);

				};

				if( this.validateFolderName(something) ) {

					item = this.setupNode(SynthPlayer(
						this.getFolderByName(something).getMainSynthDef
					), key);

				};


				if((
					(
						something.isKindOf(Collection)
						&&
						(something.isKindOf(SynthDefVariant)==false)
					)
					&&
					(something.isKindOf(I8TFolder)==false)
					&&
					(something.isKindOf(String)==false)
				)) {

					item = this.createGroup( something, key );

				};


				if( something.isKindOf(NodeProxy)) {

					item = Proxy(something);

				};






				// MIDI:

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
				if( (
						( childItem.isKindOf(SynthDef) )
						||
						( childItem.isKindOf(SynthDefVariant) )
						||
						(this.validateSynthName(childItem))
						||
						(this.validateFolderName(childItem))
						||
						( childItem.isKindOf(I8TNode) )
					) == false) {

					("Not a valid Group item: "++childItem.asString).warn;

					allValid = false;
				};
			});

			if( allValid == true, {

				// if group already exists
				if( groups[key].notNil, {

					var currentGroup = groups[key];

					// disable any synths not included in new groups


					group_.collect({|childItem,childItemKey|

						// if key already exists in group:
						if( currentGroup.keys.includes(childItemKey) == true,

						{
							var synthdef;

							if( childItem.isKindOf(I8TNode) ) {
								synthdef = childItem.synthdef;
							};
							if( (
								childItem.isKindOf(SynthDef)
								||
								childItem.isKindOf(SynthDefVariant)
							) ) {
								synthdef = childItem;
							};

							if( this.validateSynthName(childItem) ) {
								synthdef = this.getSynthDefByName(childItem);
							};

							if( this.validateFolderName(childItem) ) {
								synthdef = this.getFolderByName(childItem).getMainSynthDef;
							};

							currentGroup.at(childItemKey).synthdef = synthdef;

							currentGroup.at(childItemKey).play;
						},
						{
							// if new key:

							if( childItem.isKindOf(I8TNode) ) {

							  if( (nodes.includes( childItem ) == false), {


								this.setupNode( childItem, childItem.name );

							  });

							  currentGroup.put( childItemKey, childItem );

							};

							if( (
								childItem.isKindOf(SynthDef)
								||
								childItem.isKindOf(SynthDefVariant)
							) ) {

								childItem = SynthPlayer(childItem);

								if( (nodes.includes( childItem ) == false), {
									this.setupNode( childItem, childItem.name );
								});

								currentGroup.put( childItemKey, childItem );

							};

							if( this.validateSynthName(childItem) ) {

								if( nodes[childItemKey].notNil, {
							  		newGroup.put( childItemKey, nodes[childItem] );
								},
								{
									var synthdef = this.getSynthDefByName(childItem);

									var newNode = SynthPlayer(synthdef);

									childItem.name=childItem;

									this.setupNode( newNode, childItem );

									currentGroup.put( childItemKey, newNode );

								});


							};

							if( this.validateFolderName(childItem) ) {

								if( nodes[childItemKey].notNil, {
							  		newGroup.put( childItemKey, nodes[childItem] );
								},
								{
									var synthdef = this.getFolderByName(childItem).getMainSynthDef;

									var newNode = SynthPlayer(synthdef);

									childItem.name=childItem;

									this.setupNode( newNode, childItem );

									currentGroup.put( childItemKey, newNode );

								});


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

					// if is new group


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

							  this.setupNode( childItem, childItem.name );

							});

							newGroup.put( childItemKey, childItem );

						  };

						  if( childItem.isKindOf(InstrumentGroup) ) {
							if( groups.includes(childItem) ) {
							  newGroup.put( childItem.name, childItem );
							};
						  };

						  if( this.validateSynthName(childItem) ) {

							var childItemName = childItem;

							if( nodes[childItemName].notNil, {
							  newGroup.put( childItemName, nodes[childItemName] );
							}, {
							  // if childItem name is a group
							  if( groups[childItemName].notNil, {
								// add its childItems
								newGroup.put( childItemName, groups[childItemName] );
							  }, {
								  // if no node and no group found
								  var synthdef = this.getSynthDefByName(childItem);
								  var newNode = SynthPlayer(synthdef);
								  this.setupNode(newNode,childItemName);
								  newGroup.put( childItemKey, newNode );

							  });
							});

						};


						if( this.validateFolderName(childItem) ) {

							var childItemName = childItem;

							if( nodes[childItemName].notNil, {
							  newGroup.put( childItemName, nodes[childItemName] );
							}, {
							  // if childItem name is a group
							  if( groups[childItemName].notNil, {
								// add its childItems
								newGroup.put( childItemName, groups[childItemName] );
							  }, {
								  // if no node and no group found
								  var synthdef = this.getFolderByName(childItem).getMainSynthDef;
								  var newNode = SynthPlayer(synthdef);
								  this.setupNode(newNode,childItemName);
								  newGroup.put( childItemKey, newNode );

							  });
							});

						};

						if( (
							childItem.isKindOf(SynthDef)
							||
							childItem.isKindOf(SynthDefVariant)
						) ) {

							var newNodeKey = key++'_'++childItemKey;

							this.put( newNodeKey, childItem );

							if( nodes[newNodeKey].notNil, {
							  newGroup.put( childItemKey, nodes[newNodeKey] );
							});

						};

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


			}, {
				"Not a valid InstrumentGroup".warn;
			});


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


	synths {
		^synthLoader.synths
	}

	synths_ {|list|
		^synthLoader.synths_(list);
	}


	listSynths {|item|
		^synthLoader.listSynths(item);
	}


	validateFolderName{|synthName|
		^synthLoader.validateFolderName(synthName);
	}

	validateSynthName{|synthName|
		^synthLoader.validateSynthName(synthName);
	}

	getSynthDefByName {|synthName|
	  ^synthLoader.getSynthDefByName(synthName);
	}

	getFolderByName {|folderName|
	  ^synthLoader.getFolderByName(folderName);
	}

	validateSynthDef {|synthdef|
	  ^synthLoader.validateSynthDef(synthdef);
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
