I8TMixer : Sequenceable
{

	var channels;
	var channelGroups;
	var <master;
	var main;
	var submixes;
	var sends;
	var returns;

	var <bus;
	var <outbus;
	var busSynth;
	var <mixGroup;
	var <masterGroup;

	*new {|main_|
		^super.new(main_);
	}


	init {|main_|

		main = main_;

		channels = IdentityDictionary.new;
		channelGroups = IdentityDictionary.new;


		outbus = Server.local.outputBus;

		bus = Bus.audio(Server.local,1);

	}

	setupMaster {

		masterGroup = Group.tail(Server.default.defaultGroup);

		mixGroup = Group.tail(masterGroup);

		master = this.createMasterChannels()


	}

	createMasterChannels {



		var masterChannels = Array.fill(2,{|index|

			var masterChannel = I8TChannel(masterGroup, outbus, bus);

			var channelName = ("system_out_" ++ index).asSymbol;


			if(masterChannel.notNil) {

				masterChannel.sequencer = sequencer;
				
				masterChannel.setAmp( 1 );
				masterChannel.setPan( index.linlin(0,1,-1,1) );

				masterChannel.setName( channelName );

				masterChannel.addOutput(
					( name: channelName, channel: index )
				);

				masterChannel;


			}
		});



		^masterChannels

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
			source.isKindOf(Instrument)
			||
			source.isKindOf(InstrumentGroup)
		)

	}



	addChannel {|node,group|


		if( outbus.isKindOf(Bus), {

			if( this.isValidSource( node ) ) {

				var channel, channelGroup;

				if( node.isKindOf( Instrument ) ) {

					if( group.isKindOf(InstrumentGroup), {

						channel = channels[ node.name ];

						if(( channel.isKindOf( I8TChannel ) == false ), {
							channel  = I8TChannel(mixGroup,bus);
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

							channel = I8TChannel(mixGroup, bus);
							channel.sequencer = sequencer;

							channels[node.name]=channel;

							channel = channels[node.name];

						};

					});

					channel.setInput( node );
					channel.setName( node.name );

					^channel;

				};

				if( node.isKindOf( InstrumentGroup ) ) {

					var instrumentGroup = node;
					var channelGroup = channelGroups[instrumentGroup.name];

					if( channelGroup.isKindOf(IdentityDictionary) == false )
					{
						channelGroup = IdentityDictionary.new;
						channelGroups[instrumentGroup.name] = channelGroup;
					};

					instrumentGroup.keysValuesDo({|k,groupedNode|

						var channel = channels[k];

						if(channel.isKindOf(I8TChannel)==false) {
							channel = this.addChannel( groupedNode, instrumentGroup );
						};

						channel.setInput( groupedNode );
						channel.setName( groupedNode.name );

						^channel;

					});

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

		if( channelGroup.notNil ) {

			if( channelGroup.keys.size == 1 ) {
				^channelGroup[key];
			};
			^channelGroup
		};
	}

	removeChannel {|key|
		^channelGroups.removeAt(key)
	}

}
