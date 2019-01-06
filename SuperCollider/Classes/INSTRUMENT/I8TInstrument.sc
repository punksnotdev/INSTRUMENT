Instrument : Sequenceable
{

	var <>synth;
	var <>synths;
	var <>volume;
	var <>octave;


	*new{|name_|
		^super.new.init(name_,this.graph);
	}

	init{|graph_,name_|

		volume = 1;
		octave = 4;
		synths = List.new;
["i7t",graph_,name_].postln;
		super.init(graph_,name_);
	}

	trigger {

	}

	set {|parameter,value|

	}
}
