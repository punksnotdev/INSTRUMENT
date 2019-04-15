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
	var synthGroup;

	*new {|main_|
		^super.new.init(main_);
	}

	init {|main_|

		main = main_;

		channelGroups = IdentityDictionary.new;



		bus = Bus.audio(Server.local,2);

		synthGroup = Group.new;

		busSynth = Synth.tail(
			synthGroup,
			\audioBus,
			[\inBus, bus, \outBus,0]
		);


		master = [I8TChannel(synthGroup),I8TChannel(synthGroup)];

		master[0].setName(\system_out_0);
		master[1].setName(\system_out_1);

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

				channel = I8TChannel(synthGroup);

				this.setupChannel( node, channel );

				if( group.isNil ) {

					if( channelGroups[node.name].isNil, {
						channelGroup = IdentityDictionary.new;
					}, {
						["channelGroup is not nil!"].warn;
					});
				};

				if( group.isKindOf(InstrumentGroup)) {

					if(channelGroups[group.name].isNil ) {
						channelGroups[group.name] = IdentityDictionary.new;
					};

					channelGroup = channelGroups[group.name];

					channel = I8TChannel(synthGroup);

					channelGroup[node.name] = channel;

					this.setupChannel( node, channel );

					^channelGroups[node.name];

				};

			};

			if( node.isKindOf( InstrumentGroup ) ) {

				channelGroups[node.name] = IdentityDictionary.new;

			};


		};

		"Not a valid Source".warn;

		^nil

	}


	setupChannel{|node, channel|
		if( node.isKindOf(I8TNode)){

			channel.setSynthGroup( synthGroup );
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
