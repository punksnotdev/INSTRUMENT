I8TMixer : I8TNode
{

	var channelGroups;
	var master;
	var submixes;
	var sends;
	var returns;



	* new {
		^super.new.init();
	}

	init {

		// "init mixer".postln;

		channelGroups = IdentityDictionary.new;

	}



	at {|key|

		^channelGroups[key]

	}


	put {|key, something|

		// ["put", key, something].postln;

		// if 'something' is valid source
		if( this.isValidSource( something ) ) {

			var channelGroup;
			var channel;

			channelGroup = List.new;

			channel = I8TChannel.new;

			channelGroups[key] = channelGroup;

			^channel;

		};

		"Not a valid Source".warn;

		^nil

	}


	isValidSource {|source|

		^ (
			source.isKindOf(Instrument)
			||
			source.isKindOf(InstrumentGroup)
		)

	}


}
