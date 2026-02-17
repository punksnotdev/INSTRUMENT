I8TMain : Event
{

	classvar instance;
	classvar servers;
	

	var <server;
	var <parGroup;
	var <currentServer;
	var <isBooted;

	var <>mode;
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
	var <synthLoader;

	var <> data;

	var lastMap;

	var currentFolder;

	var clearedGroups;
	var clearedNodes;
	var clearedFunctions;

	var <proxyspace;

	*new {|server_,createNew=false|

		if(( instance.isNil || createNew == true), {

			^super.new.init(server_,createNew);

		}, {

			^instance;

		});

	}
	*initClass {
		// StartUp.add {
			// var i = INSTRUMENT();
		// }
	}

	setServer {|server_|
		currentServer = server_;
	}

	

	addServer {|server_|
		if(server_.isKindOf(Server), {
			
			if( servers[ server_.name ].isNil, {				
				servers[ server_.name ] = server_;				
			});

			^servers[ server_.name ]

		}, {
			"not a server".postln;
		});
	}

	init {|server_,createNew=false|


		mode = "play";



		if( servers.isNil, {
			servers = Dictionary.new;
		});
		
		if(server_.isKindOf(Server), {
			
			var z = this.addServer( server_ );

			if( z.isKindOf(Server), {
				server = z;
				parGroup = ParGroup.new( server );
				parGroup.register;
			});
			

		}, {
			server = Server.local;
			parGroup = Group.new( server );
			parGroup.register;
		});

		server.latency = 0.05; // 50ms lookahead for sample-accurate timing

		// clock = TempoClock.default;
		clock = TempoClock.new( TempoClock.default.tempo );

		ready = false;
		

		if( server.serverRunning, {

			if( createNew==false ) {
				instance = this;
			};

			CmdPeriod.add({
				this.kill()
			});


			nodes = Dictionary.new;
			sequencer = Sequencer.new(this);

			mixer = I8TMixer.new(this);

			mixer.setupSequencer(sequencer);		
			mixer.setupMaster();

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

			isBooted = true;

		});



		// warn user if SC not running when starting INSTRUMENT manually
		if(
			server.serverRunning == false
		) {
			"please boot".warn;

			if( I8TSynthLoader.synthsLoaded.notNil, {
				if( mode=="play" ) {
				};
			});

			isBooted = false;

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

				if( node.isKindOf(Proxy) == false ) {
					node.channel = mixer.addChannel( node );
				};
	
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
		if( node.isNil, {
			mixer.free;
			nodes.do({|n| n.free; });
			nodes.clear;
		}, {
			if( node.isKindOf(Sequenceable), {
				sequencer.unregisterInstrument(node);
				node.free;
			});
			nodes[node.name] = nil;
		});
	}

	speed_ {|speed_|
		sequencer.speed = speed_;
	}

	play {

		if( ready == true, {

			playing = true;

			nodes.collect({|node|

				var wasCleared = false;
				
				// TODO: también checar con storage:
				if(clearedNodes.notNil, {
					if(clearedNodes.includes(node)){
						wasCleared = true;
					};
				});
				if( (wasCleared==false) && data.storage.notNil, {
					data.storage.keysValuesDo({|k,v|
						wasCleared = wasCleared || v.nodes.includes(node);
					});
				});

				if( wasCleared == false ) {
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
	

	clearCheckGroup {|group,name|
	
		var wasCleared = false;

		if( data.storage.notNil, {
			data.storage.keysValuesDo({|k,v|
				if( k != name ) {
					wasCleared = wasCleared || v.groups.includes(group);
				};
			});
		});	

		
		if( clearedGroups.notNil ) {
			wasCleared = wasCleared || clearedGroups.includes(group);			
		};


		^wasCleared
	
	}

	clearCheckNode {|node,name|
		
		var wasCleared = false;

		if( data.storage.notNil, {
			data.storage.keysValuesDo({|k,v|
				if( k != name ) {
					wasCleared = wasCleared || v.nodes.includes(node);
				};
			});
		});	

		if( clearedNodes.notNil ) {
			wasCleared = wasCleared || clearedNodes.includes(node);			
		};

		^wasCleared
	
	}


	clear {|name|

		if( name.notNil, {

			if( data.storage.isNil ) {
				data.storage = ();
			};

			data.storage[name.asSymbol] = ( 
				groups: groups.reject({|g| this.clearCheckGroup(g, name.asSymbol) }),
				nodes: nodes.values.reject({|n| this.clearCheckNode(n, name.asSymbol) }),
				functions: sequencer.repeatFunctions.copy,
			);

		}, {

			clearedGroups=groups.reject({|g| this.clearCheckGroup(g) });
			clearedNodes=nodes.values.reject({|n| this.clearCheckNode(n) });

			clearedFunctions=sequencer.repeatFunctions.copy;

		});


		groups.collect({|g|

			g.pause;

			g.collect({|gnode|
				if( name.notNil, {
					// if( this.clearCheckNode(gnode, name.asSymbol) == false ) {
					// 	data.storage[name.asSymbol].nodes.add( gnode );
					// };
				}, {
					if( this.clearCheckNode(gnode) == false ) {
						clearedNodes.add(gnode);
					};
				});
			});

		});

		nodes.collect({|n|

			n.pause;

		});

		this.clearSequencerFunctions();

		this.go(0);

	}

	restore {|name|
		if( name.notNil, {


			if( data.storage[name.asSymbol].notNil, {
			
				sequencer.repeatFunctions = data.storage[name.asSymbol].functions.copy;

				data.storage[name.asSymbol].nodes.collect({|n|
					n.play;
				});
				data.storage[name.asSymbol].groups.collect({|g|
					g.play;
				});

			}, {

				( "Key '" ++ name ++ "' not found in storage" ).warn;

			});


		}, {

			sequencer.repeatFunctions=clearedFunctions.copy;

			clearedNodes.collect({|n|
				n.play;
			});
			clearedGroups.collect({|g|
				g.play;
			});
			
			clearedNodes = List.new;
			clearedGroups = List.new;

		});

		this.go(0);

	}

	stop {

		playing=false;

		nodes.collect({|node|
			node.stop;
		});

		sequencer.stop;

		if( mode=="play" ) {
			"I N S T R U M E N T stopped".postln;
		};

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
	// 		if( (instruments.isKindOf(I8TInstrument)), {
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
				target.isKindOf(MIDIControllerTarget)
			), {
				controllerManager.map(controller,target,parameter,range);
				("mapped: "++controller.name++" > "++target.name++": "++parameter).postln;
			}, {
				"Target not of class 'I8TInstrument'"
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

		if( proxyspace.isKindOf(ProxySpace)) {
			proxyspace.makeTempoClock(clock.tempo,'instrument',clock);
		};

	}

	bpm_ {|bpm|
		this.tempo( bpm );
	}

	proxyspace_{|proxyspace_|

		if( proxyspace_.isKindOf(ProxySpace)) {
			proxyspace = proxyspace_;
			proxyspace.makeTempoClock(clock.tempo,'instrument',clock);
		};

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


				if( synthLoader.validateSynthName(something) ) {

					var synthdef = synthLoader.getSynthDefByName(something);
					if(synthLoader.validateSynthDef(synthdef)) {
						if(nodes[synthdef].notNil, {
							item = this.setupNode(nodes[synthdef], key);
						}, {
							item = this.setupNode(I8TSynthPlayer(synthdef, main_: this), key);
						});
					}

				};

				if( (
						something.isKindOf(SynthDef)
						||
						something.isKindOf(SynthDefVariant)
					)
				) {

					item = this.setupNode(I8TSynthPlayer(something, main_: this), key);

				};

				if( something.isKindOf(I8TFolder) ) {

					item = this.setupNode(I8TSynthPlayer(something.getMainSynthDef, main_: this), key);

				};


				if( synthLoader.validateFolderName(something) ) {

					item = this.setupNode(I8TSynthPlayer(
						synthLoader.getFolderByName(something).getMainSynthDef, main_: this
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

					item = this.upsertGroup( something, key );

				};


				if( something.isKindOf(NodeProxy)) {

					item = Proxy(something);
					item.name = key;
					// provisionally:
					sequencer.registerInstrument(item);

					this.setupNode( item );
				};



				if( item.isNil ) {
					^nil
				};

				// MIDI:

				if( midiControllers.inputs.notNil ) {

					if( (autoMIDI == true) && (midiControllers.inputs.size > 0),{

						if( item.notNil ) {

							if( item.isKindOf(InstrumentGroup) || item.isKindOf(I8TInstrument) ) {

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


	upsertGroup {|group_,key_|

		if( key_.notNil && group_.isKindOf(Collection) ) {

			var item;

			if( this.validateGroupItems(group_), {

				// if group already exists, update, otherwise create
				if( groups[key_].notNil, {

					item = this.updateGroup( key_, group_ );

					mixer.addChannel( item );

				}, {

					item = this.createGroup( key_, group_ );

					groups[key_] = item;

					mixer.addChannel( item );
					sequencer.registerInstrument(item);
					controllerManager.addInstrument( item, key_ );

					if( playing ) {
						item.play;
					};

				});


			}, {

				if( mode=="play" ) {
					"Not a valid InstrumentGroup".warn;
				};

			});


			^item;

		};

	}


	// mixer


    fx {

		^mixer.fx;

    }

    fx_ {|key,fx|
		^mixer.addFxChain(key,fx)
    }



	updateMixerGroup {|group|

		if( group.isKindOf(InstrumentGroup)) {

			if( group.name.notNil, {

				^this.upsertGroup(group,group.name);

			}, {

				"Main: cannot update group with nil name".postln;

			});

		}

	}





	displayTracks {

		var tracks = List.new;

		sequencer.sequencerTracks.keysValuesDo({|k,v|
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
		// sequencer.sequencerTracks.collect({|track,index|
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





	kill {
		if( instance.notNil ) {
			this.stop();
			instance = nil;

			nodes = Dictionary.new;
			if( mode=="play" ) {
				"I N S T R U M E N T killed.".warn;
			};
		}
	}

	// validations:

	validateFolderName{|synthName|
		^synthLoader.validateFolderName(synthName);
	}

	validateSynthName{|synthName|
		^synthLoader.validateSynthName(synthName);
	}

	validateSynthDef{|synthDef|
		^synthLoader.validateSynthDef(synthDef);
	}

	getSynthDefByName {|synthName|
		^synthLoader.getSynthDefByName(synthName);
	}

	getFolderByName {|folderName|
		^synthLoader.getFolderByName(folderName);
	}


	validateFx{|fx|

        ^(
            fx.isKindOf(SynthDef)
            || fx.isKindOf(Collection)
            || fx.isKindOf(SynthDefVariant)
            || fx.isKindOf(Symbol)
            || fx.isKindOf(String)
            // || fx.isNil
            || (fx===false)
        )

	}


	volume_ {|volume|

		mixer.master.collect({|channel|
			channel.outSynth.set(\amp,volume);
			["set",channel.outSynth,volume].postln;
		});

	}



	validateGroupItems {|group_|
		var allValid = true;
		group_.collect({|childItem|
			if( (
					( childItem.isKindOf(SynthDef) )
					||
					( childItem.isKindOf(SynthDefVariant) )
					||
					(synthLoader.validateSynthName(childItem))
					||
					(synthLoader.validateFolderName(childItem))
					||
					( childItem.isKindOf(I8TNode) )
					||
					( childItem.isKindOf(I8TFolder) )
				) == false) {

				if( mode=="play" ) {
					("Not a valid Group item: "++childItem.asString).warn;
				};


				allValid = false;
			};
		});

		^allValid
	}



	createGroup {|key_, group_|

		var newGroup;

		if( group_.isKindOf(InstrumentGroup), {

			newGroup = group_;

			newGroup.name = key_;


		}, {

			newGroup = InstrumentGroup.new;

			newGroup.name = key_;

			group_.collect({

			  arg childItem,childItemKey;

			  var node;

			  if( childItem.isKindOf(InstrumentGroup), {

				if( groups.includes(childItem) ) {
				  newGroup.put( childItem.name, childItem );
				};

			  }, {

				  // if new key:
				  node = this.createGroupChildNode( newGroup, childItem, childItemKey );


				  newGroup.put( childItemKey, node );

			  });

			});

		});


		^newGroup

	}


	createGroupChildNode {|group,childItem,childItemKey|

		var newKey;
		var node;

		newKey = (group.name++'_'++childItemKey).asString.toLower.asSymbol;


		if( childItem.isKindOf(I8TNode) ) {
			
		  node = childItem;
		};

		if( (
			childItem.isKindOf(SynthDef)
			||
			childItem.isKindOf(SynthDefVariant)
		) ) {
			
			node = I8TSynthPlayer(childItem, main_: this);


		};

		if( synthLoader.validateSynthName(childItem) ) {


			if( nodes[newKey].notNil, {
				node = nodes[newKey];
			},
			{
				var synthdef = synthLoader.getSynthDefByName(childItem);
				node = I8TSynthPlayer(synthdef, main_: this);
			});


		};

		if( synthLoader.validateFolderName(childItem) ) {


			if( nodes[newKey].notNil, {

				node = nodes[newKey];
			},
			{
				var synthdef = synthLoader.getFolderByName(childItem).getMainSynthDef;

				node = I8TSynthPlayer(synthdef, main_: this);
			});


		};

		node.name = newKey;

		["node",node].postln;

		this.setupNode( node, newKey );


		^node

	}

	updateGroup {|key_, group_|

		var currentGroup = groups[key_];

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

				if( (
					childItem.isKindOf(I8TFolder)
				) ) {
					synthdef = childItem.getMainSynthDef;
				};

				if( synthLoader.validateSynthName(childItem) ) {
					synthdef = synthLoader.getSynthDefByName(childItem);
				};

				if( synthLoader.validateFolderName(childItem) ) {
					synthdef = synthLoader.getFolderByName(childItem).getMainSynthDef;
				};


				currentGroup.at(childItemKey).synthdef = synthdef;

				// Only restart if not already playing - avoids
				// bar-boundary quantization delay on hot-swap
				if( currentGroup.at(childItemKey).playing != true ) {
					currentGroup.at(childItemKey).play;
				};

			},
			{
				// if new key:
				var node = this.createGroupChildNode( currentGroup, childItem, childItemKey );

				currentGroup.put( childItemKey, node );


			});
		});


		// stop discarded instruments
		currentGroup.collect({|childItem,childItemKey|
			if(  group_.keys.includes(childItemKey) == false ) {
				currentGroup.stop(childItemKey);
			};
		});


		^currentGroup;

	}


}
