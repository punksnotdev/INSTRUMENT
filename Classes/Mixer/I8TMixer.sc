I8TMixer : Sequenceable
{

	var channels;
	var channelGroups;
	var <master;
	var <fx;
	var <masterFx;
	var main;
	var submixes;
	var sends;
	var returns;

	var <>sequencer;
	var <bus;
	// var <fxBus;
	var <outbus;
	var busSynth;
	var <mixNodeGroup;
	var <groupsNodeGroup;
	var <fxNodeGroup;

	var <masterGroup;

	*new {|main_|
		^super.new(main_);
	}


	init {|main_|

		main = main_;

		// this.setupSequencer( main.sequencer );

		channels = IdentityDictionary.new;
		channelGroups = IdentityDictionary.new;


		outbus = Server.local.outputBus;

		bus = Bus.audio(Server.local,1);
		// fxBus = Bus.audio(Server.local,1);

		fx = I8TChannelGroup(this);

	}

	setupMaster {

		masterGroup = Group.tail(Server.default.defaultGroup);

		mixNodeGroup = Group.head(masterGroup);
		groupsNodeGroup = Group.tail(masterGroup);
		fxNodeGroup = Group.tail(masterGroup);

		master = this.createMasterChannels();

		masterFx = this.addFxChain('master',nil,master);

	}


	setupSequencer {|sequencer_|

		sequencer = sequencer_;

	}



	createMasterChannels {



		^Array.fill(2,{|index|

			var masterChannel = I8TChannel(masterGroup, outbus, bus);

			var channelName = ("system_out_" ++ index).asSymbol;


			if(masterChannel.notNil) {
				this.setupChannelSequencer( masterChannel );

				masterChannel.setAmp( 1 );
				masterChannel.setPan( index.linlin(0,1,-1,1) );

				masterChannel.setName( channelName );

				masterChannel.addOutput(
					( name: channelName, channel: index )
				);

				masterChannel;


			}
		});

	}


	at {|key|

		^channelGroups[key]

	}


	put {|key, something|
		^this.addChannel( something );

	}
	rm {|key|

		this.channelGroups.removeAt(key);

	}


	isValidSource {|source|
		^ (
			source.isKindOf(I8TInstrument)
			||
			source.isKindOf(InstrumentGroup)
		)

	}



	addChannel {|node,group|


		if( outbus.isKindOf(Bus), {

			if( this.isValidSource( node ) ) {

				var channel, channelGroup;

				if( node.isKindOf( I8TInstrument ) ) {

					if( group.isKindOf(InstrumentGroup), {

						channel = channels[ node.name ];


						if(( channel.isKindOf( I8TChannel ) == false ), {
							channel = I8TChannel(mixNodeGroup,bus);
							channel.sequencer = sequencer;
						});


						channelGroup = channelGroups[ group.name ];

						if( channelGroup.isKindOf( IdentityDictionary ), {
							if( channelGroup.keys.includes( node.name ) == false, {
								channelGroup[ node.name ] = channel;

							});

						}, {

							channelGroups[ group.name ] = IdentityDictionary.new;

							channelGroup = channelGroups[ group.name ];

							channelGroup[ node.name ] = channel;

						});

					}, {

						// no group
						if( channels[node.name].isKindOf( I8TChannel ) == false ) {

							channel = I8TChannel(mixNodeGroup, bus);

							channel.setupSequencer( sequencer );

							channels[node.name]=channel;

							channel = channels[node.name];

						};

					});

					if( channel.notNil ) {

						channel.setInput( node );
						channel.setName( node.name );

						^channel;

					}

				};

				if( node.isKindOf( InstrumentGroup ) ) {

					var instrumentGroup = node;
					var key = instrumentGroup.name;

					var channelGroup = channelGroups[key];
					var groupMainChannel;

					if( channelGroup.isKindOf(IdentityDictionary) == false )
					{
						channelGroup = IdentityDictionary.new;
						channelGroups[key] = channelGroup;
					};


					// create main channel for group


					if( channelGroup['group'].isKindOf(I8TChannel) == false, {

						groupMainChannel=I8TChannel(groupsNodeGroup,bus);
						groupMainChannel.name=key;
						groupMainChannel.sequencer = sequencer;

						channelGroup['group']=groupMainChannel;

					}, {

						groupMainChannel = channelGroup['group'];

					});


					instrumentGroup.keysValuesDo({|k,groupedNode|

						var channel = channels[k];

						if(channel.isKindOf(I8TChannel)==false) {
							channel = this.addChannel( groupedNode, instrumentGroup );
						};

						channel.setInput( groupedNode );
						channel.setName( groupedNode.name );

						channel.connect( groupMainChannel );

					});

					^channelGroup['group']

				};


			};

		}, {

			"Mixer: No Outbus defined".warn;

		});



		^nil

	}



	getMasterChannels {
		^master
	}
	getChannels {
		^channelGroups
	}

	getChannel {|key|

		var channelGroup = channelGroups[key.asSymbol];

		^channelGroup
		// if( channelGroup.notNil ) {
		//
		// 	if( channelGroup.keys.size == 1 ) {
		// 		^channelGroup[key];
		// 	};
		// };
	}

	removeChannel {|key|
		^channelGroups.removeAt(key)
	}



	setupChannelSequencer {|channel_|

		channel_.setupSequencer( sequencer );

	}

	fx_ {|key,value|
		this.addFxChain(key,value);
	}


	addFxChain{|key,fxChain|

		var fxChannel = fx.at(key);

		if( fxChannel.isNil ) {

			fxChannel = I8TChannel(fxNodeGroup, bus);
			fxChannel.name=key;

			this.setupChannelSequencer( fxChannel );

			fx.put(key,fxChannel);

			fxChannel.sequencer = sequencer;

		};

		if( fxChain.notNil ) {
			fxChannel.rm('fx');
			fxChannel.setFxChain( fxChain );
		};


		^fxChannel

	}

}
