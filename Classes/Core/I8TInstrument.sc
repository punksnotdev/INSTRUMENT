Instrument : Sequenceable
{


    var group;
	var groupID;

	var outbus;

	var <>synth;
	var <>synths;
	var <>volume;
	var <>octave;
	var <main;
	// var synthGroup;
	var outGroup;


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


	group_ {|group_| }
  	group {
  		^group;
  	}


	  	outbus_ {|outbus_|
	  		outbus = outbus_;
	  	}
	  	outbus {
	  		^outbus;
	  	}
}
