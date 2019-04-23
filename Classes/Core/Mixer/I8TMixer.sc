I8TMixer : I8TNode
{

	var channelGroups;
	var master;
	var main;
	var submixes;
	var sends;
	var returns;

	var bus;
	var outbus;
	var busSynth;
	var <mixGroup;
	var <masterGroup;

	*new {|main_|
		^super.new(main_);
	}

	init {|main_|

		main = main_;

		channelGroups = IdentityDictionary.new;


		outbus = Server.local.outputBus;

		bus = Bus.audio(Server.local,2);

		masterGroup = Group.tail(Server.default.defaultGroup);
		mixGroup = Group.tail(masterGroup);



		master = Array.fill(2,{I8TChannel(masterGroup)});

		master[0].setSynthGroup( masterGroup );


		master[0].setAmp( 1 );
		master[0].setOutbus( outbus );

		master[0].setName(\system_out_0);
		master[1].setName(\system_out_1);

		master[0].setOutbus(outbus);
		master[1].setOutbus(outbus);

		master[0].addOutput(
			(name:\system_out_0,channel:0)
		);
		master[1].addOutput(
			(name:\system_out_1,channel:1)
		);

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

						channelGroup = channelGroups[ group.name ];

						if( channelGroup.isKindOf( IdentityDictionary ), {
							// ["Mixer: Instrument Group already exists",group.name].postln;
							if( channelGroup.keys.includes( node.name ) == false, {
								channel = I8TChannel(mixGroup);

							}, {
								// ["Mixer:", group.name, "already includes node key"].postln;
							})
						}, {

							channel = I8TChannel(mixGroup);
							channelGroups[ group.name ] = IdentityDictionary.new;
							channelGroup=channelGroups[ group.name ];

							// ["Mixer: Instrument Group doesn't exist",group.name].postln;
						});
					}, {
						// no group
						if( channelGroups[node.name].isKindOf( IdentityDictionary ) == false ) {

							// ["Mixer: no group", node.name].postln;
							channel = I8TChannel(mixGroup);

							channelGroups[node.name]=IdentityDictionary.new;
							channelGroup = channelGroups[node.name];

						};

					});

					channelGroup[node.name]=channel;


					this.setupChannel( node, channel );

					^channelGroup[node.name];


				};

				if( node.isKindOf( InstrumentGroup ) ) {

					var instrumentGroup = node;
					var channelGroup = channelGroups[instrumentGroup.name];

					if( channelGroup.isKindOf(IdentityDictionary) == false )
					{
						// ["Mixer: added new group", instrumentGroup.name ].postln;
						channelGroup = IdentityDictionary.new;
						channelGroups[instrumentGroup.name] = channelGroup;
					};

					instrumentGroup.keysValuesDo({|k,v|
						if(channelGroups[instrumentGroup.name][ k ].isKindOf(I8TChannel)==false) {
							// ["Mixer: add new group child", node.name, k, v, channelGroups[instrumentGroup.name]].postln;
							this.addChannel( v, instrumentGroup );
						};
					});

				};


			};
		}, {

			"Mixer: No Outbus defined".warn;

		});


		["Not a valid Source", node].postln;

		^nil

	}



	setupChannel{|node, channel|
		if( node.isKindOf(I8TNode)){

			channel.setSynthGroup( mixGroup );
			channel.setInput( node );
			channel.addOutput( master[0] );
			channel.addOutput( master[1] );
			channel.setName( node.name );
			channel.setAmp( node.amp );
			channel.setOutbus( master[0].getBus );


		}
	}

	getMasterChannels {
		^master
	}
	getChannels {
		^channelGroups
	}

	getChannel {|key|
		^channelGroups[key]
	}

	removeChannel {|key|
		^channelGroups.removeAt(key)
	}

}
