Kick
{

	classvar synth;

	*new {
		synth = Synth(\kick);
	}

	*play {|note|
		if( note == nil, {
			synth.set(\t_trig,1);
		}, {
			synth.set(\note,note,\t_trig,1);
		})
	}

	// *play


}
