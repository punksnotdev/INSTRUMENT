Instrument : Sequenceable
{

	var <synth;

	var <>volume;


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
