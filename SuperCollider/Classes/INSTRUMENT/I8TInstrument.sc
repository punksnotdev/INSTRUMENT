Instrument : Sequenceable
{

	var <>synth;
	var <>synths;
	var <>volume;
	var <>octave;
	var <main;
	var <clock;

	*new{|name_|
		^super.new.init(name_,this.graph);
	}

	init{|graph_,name_|

		volume = 1;
		octave = 4;
		synths = List.new;
		main = graph_;
		super.init(graph_,name_);
	}

	trigger {

	}

	set {|parameter,value|

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
