I8TChannelGroup : Sequenceable
{


	var mixer;
	var channels;


	*new {|mixer_,main_|
		^super.new.init(main_,mixer_);
	}

	init {|main_,mixer_|
		["new channel group", main_,mixer_].postln;
		channels = ();
		if( mixer_.isKindOf(I8TMixer) ) {
			mixer = mixer_;
		};
	}



	setupSequencer {|sequencer_|

		channels.keysValuesDo({|k,channel|
			channel.setupSequencer( sequencer_ );
		});

	}


	put {|key,value|

		var channel;

		if( value.isKindOf(I8TChannel) ) {
			channel = value;
		};

		if( value.isKindOf(I8TChannel) == false ) {
			channel = mixer.addFxChain(key,value);
		};

		if( channel.isKindOf(I8TChannel) ) {


			if( ( key.isKindOf(Symbol) || key.isKindOf(String) ), {
				mixer.setupChannelSequencer(channel);
				^channels.put(key.asSymbol, channel);
			}, {
				"Channel not valid".warn;
			});

		};

	}

	at {|key,channel|
		^channels.at(key);
	}

	rm {|key|
		^channels.removeAt(key);
	}





    doesNotUnderstand {

        arg key ... args;

		var value = args[0];

		var channel;

		if( key.isSetter, {

			channel = channels.at(key.asGetter);

			if( channel.isNil ) {
				if( value.notNil, {
					^channel = this.put(key.asGetter,value);
				});
			};

			if( channel.isKindOf(I8TChannel) ) {
				if(value.isNil, {
					channel.kill;
					this.rm(key.asGetter);
					^nil
				}, {
					^channel.setFxChain(value);
				});
			};

		}, {

			^channels.at(key);

		});

		^nil

    }




}
