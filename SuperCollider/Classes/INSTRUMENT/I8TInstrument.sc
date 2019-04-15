Instrument : Sequenceable
{

	var outBus;

	var <>synth;
	var <>synths;
	var <>volume;
	var <>octave;
	var <main;

	*new{|name_|
		^super.new.init(name_,this.graph);
	}

	init{|graph_,name_|

		volume = 1;
		octave = 4;
		synths = List.new;
		main = graph_;
		super.init(graph_,name_);

		outBus = 0;

	}

	trigger {

	}

	set {|parameter,value|

	}


	outBus_ {|outBus_|
		outBus = outBus_;
	}
	outBus {
		^outBus;
	}

	parameters_array {|array|
		var parameters_array = List.new;

		array.keysValuesDo({|key,value|
			parameters_array.add(key.asSymbol);
			parameters_array.add(value);
		})

		^parameters_array
	}

}
