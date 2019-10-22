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
				if(clearedNodes.includes(node)==false){
					node.play;
				};
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

					if( this.validateSynthDef( something.at(something.name) ) ) {
						item = this.setupNode(SynthPlayer(something.at(something.name)), key);
					};

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
						( childItem.isKindOf(I8TNode) )
					) == false) {

					("Not a valid Group item"++childItem.asString).warn;

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

	loadSynths {|path, parent|


		var files;
		var folder;
		// var level;
		var scdFiles = List.new;
		var folders = List.new;

		var items = ();

		folder = I8TFolder();

		if( path.notNil, {
			files = path.pathMatch;
			folder.name=PathName(path).folderName;
		}, {
			files = (Platform.userExtensionDir++"/INSTRUMENT/Sounds/SynthDefs/*").pathMatch;
			folder.name='root';
		});



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

			if( synthdef.isKindOf(SynthDef) ) {
				items[ fileName.toLower.replace(" ","_").asSymbol ] = synthdef;
			};
		});

		folders.collect({|folderSrc, index|

			var pathName = PathName( folderSrc );

			var folderName = pathName.folderName.toLower.replace(" ","_").asSymbol;

			// "-------".postln;
			// folderName.postln;
			// "-------".postln;

			items[folderName]=this.loadSynths( folderSrc++"*", folder );
			items[folderName].name = folderName;
			items[folderName].folderParent = folder;

		});



		items.keysValuesDo({|k,v| folder[k]=v; });


		Task.new({
			0.1.wait;
			folder.refInAncestors(folder.name, folder);
			0.1.wait;
			folder.organizeByFamilies();
			0.1.wait;
			folder.addVariants();
			0.2.wait;
			folder.makeRefs();
		}).play;

		if( parent.isNil, {
			synths = folder;
		});

		^folder

	}



	validateSynthName{|synthName|

		var synthdef;

		if( synthName.isKindOf(String) || synthName.isKindOf(Symbol) ) {

			^this.getSynthDefByName(synthName).notNil

		};

		^false;
	}

	getSynthDefByName {|synthName|

		var synthdef;

		synthdef = synths[synthName.asString.toLower.asSymbol];

		if( synthdef.isNil ) {
			synthdef = synths[synthName.asSymbol];
		};


		if(
			(
				synthdef.isNil
				||
				(
					synthdef.isKindOf(Event)
					&&
					(synthdef.isKindOf(SynthDefVariant)==false)
				)
			)
		) {
			synthdef = SynthDescLib.default.at(synthName.asSymbol);
		};

		^synthdef


	}

	validateSynthDef {|synthdef|
		var isValid = true;
		var outputs;

		var isSynth = (synthdef.isKindOf(SynthDef)||synthdef.isKindOf(SynthDesc)||synthdef.isKindOf(SynthDefVariant));

		if( isSynth == false) {
			"I8TMain: validateSynthDef: Not a valid SynthDef".warn;
			isValid=false;
			^isValid;
		};

		if( synthdef.isKindOf(SynthDef) ) {

			outputs = SynthDescLib.global[synthdef.name.asSymbol].outputs;

			if(outputs.size>1) {
				// TODO: do not add multichannel synths?
				// isValid = false;
				// ("SynthDef "++ synthdef.name ++" has more than 1 output: Total " ++ outputs.size ).warn;
			}

		};

		^isValid;
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
