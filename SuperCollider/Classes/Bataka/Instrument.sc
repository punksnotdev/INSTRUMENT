Instrument : Sequenceable
{

	var <name;
	var <synth;

	var <>volume;

	// *new {|name|
	// 	^super.new.init(name);
	// }

	// init {|name|
	// 	var z;
	// 	// this.class.graph.postln;
	// 	// super.init(name,this.class.graph);
	// }

	setSynth {|newSynth|
		synth = newSynth;
	}




	getVolume {
		^volume;
	}
	setVolume {|volume_|
		volume = volume_;
	}


	noteOn {|note|
		synth.play(note);
	}

}
