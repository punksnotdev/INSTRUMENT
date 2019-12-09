I8TChannelGroup : Sequenceable
{


	var channels;


	*new {
		^super.new.init(this.graph);
	}

	init {|graph_|
		channels = ();
	}


	put {|key,channel|
		if( (
			(
				key.isKindOf(Symbol)
				||
				key.isKindOf(String)
			)
			&&
			channel.isKindOf(I8TChannel)
		), {
			channels.put(key.asSymbol, channel);
		}, {
			"Channel not valid".warn;
		});
	}

	at {|key,channel|
		^channels.at(key);
	}

	rm {|key|
		^channels.removeAt(key);
	}





    doesNotUnderstand {

        arg selector ... args;

		var value = args[0];

		['dne',selector,value].postln;

		^nil

    }




}
