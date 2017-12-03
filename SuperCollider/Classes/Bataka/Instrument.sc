Instrument : Sequenceable
{

	var <>synth;
	var <>volume;

	noteOn {|note|
		[synth,note].postln;
		synth.set(\t_trig,1,\note,60+note);
	}

}
