Instrument : Sequenceable
{

	var <>synth;
	var <>volume;
	var <>octave;


	*new{|name_|
		^super.new.init(name_,this.graph);
	}

	init{|name_,graph_|

		super.init(name_,graph_);
		volume = 1;
		octave = 4;

	}

	trigger {

	}

	set {|parameter,value|

	}
}
