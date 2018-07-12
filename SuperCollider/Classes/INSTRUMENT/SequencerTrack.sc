// instruments se va a tomar de nodegraph!

SequencerTrack
{

	classvar <>classSequencer;
	var <sequencer;

	var <>parameterTracks;

	var <>instrument;
	var <>name;

	var <playing;

	var <>repeats;
	var <speed;
	var currentSpeed;
	var <>beats;

	*new {|instrument_|
		^super.new.init(instrument_);
	}

	init {|instrument_|

		sequencer = classSequencer;

		if( instrument_.isKindOf(Instrument), {
			instrument = instrument_;
			name = instrument.name;
		}, {
			name = instrument_;
		});

		repeats = 4;
		speed = 1;
		currentSpeed = 1;

		beats = 0;

		parameterTracks = IdentityDictionary.new;

	}



	fwd{|i|

		if( playing == true, {
			parameterTracks.collect({|p|
				p.fwd(i)
			});
		});

	}


	play {|position|
		if( position != nil, { beats = position; });

		parameterTracks.collect({|p| p.play(position); });

		^playing = true;
	}
	stop {|position|
		if( position != nil, { beats = position; });

		parameterTracks.collect({|p| p.stop(position); });

		^playing = false;
	}

	addPattern {|parameter,key,pattern,play_parameters|

		if( parameterTracks[ parameter ] == nil, {
			parameterTracks[ parameter ] = ParameterTrack.new( this, parameter );
		});
		parameterTracks[ parameter ].addPattern(key,pattern,play_parameters);

		if( playing == true, {
			parameterTracks[ parameter ].play;
		});

	}

	removePattern {|parameter,key|
		parameterTracks[parameter].removePattern(key);
	}

	getPattern{|parameter,key|

		parameterTracks[parameter].getPattern(key);

	}

	setPatternParameters{|parameter,key,play_parameters|
		// if( key.isArray, {}, {
		// 	[]
		// })
		parameterTracks[parameter].setPatternParameters(key,play_parameters);

		"TO-DO: implement set pattern!"

	}

	removePatterns {|parameter,pattern|

		parameterTracks[parameter].removePatterns(pattern);

	}

	speed_{|sp_|
		speed = sp_;
		parameterTracks.collect({|t|
			t.speed = speed;
		});
	}

}
