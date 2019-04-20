I8TMixer : I8TNode
{

	var channelGroups;
	var master;
	var main;
	var submixes;
	var sends;
	var returns;

	var bus;
	var busSynth;
	var mixGroup;
	var masterGroup;

	*new {|main_|
		^super.new.init(main_);
	}

	init {|main_|

		main = main_;

		channelGroups = IdentityDictionary.new;



		bus = Bus.audio(Server.local,2);

		mixGroup = Group.new;
		masterGroup = Group.new;



		master = [
			I8TChannel(mixGroup),
			I8TChannel(mixGroup)
		];

		master[0].setName(\system_out_0);
		master[1].setName(\system_out_1);

		master[0].setOutbus(\system_out_0);
		master[1].setOutbus(\system_out_1);

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


		if( this.isValidSource( node ) ) {

			var channel;
			var channelGroup;

			if( node.isKindOf( Instrument ) ) {

				var targetGroup;

				if( group.isKindOf(InstrumentGroup) == false, {


				}, {

					if( channelGroups[ group.name ].isKindOf( IdentityDictionary ), {
						"Instrument Group already exists".postln;
					}, {

						channel = I8TChannel(targetGroup);

					});
				});

				channelGroup = channelGroups[group.name];


				if( mixGroup.isKindOf(Group) == false ) {
					var targetGroup = Group.new;
					mixGroup = targetGroup;
				};



				channel = I8TChannel(mixGroup);

				channelGroup[node.name] = channel;

				this.setupChannel( node, channel );

				^channelGroup[node.name];
				

			};

			if( node.isKindOf( InstrumentGroup ) ) {

				// channelGroups[node.name] = IdentityDictionary.new;

			};


		};

		"Not a valid Source".warn;

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

		}
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
