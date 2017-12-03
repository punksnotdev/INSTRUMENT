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
	var <>speed;
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

	addPattern {|key,pattern,parameters|

		if( parameterTracks[ pattern.target ] == nil, {
			parameterTracks[ pattern.target ] = ParameterTrack.new( this );
		});
		parameterTracks[ pattern.target ].addPattern(key,pattern,parameters)
	}

	removePattern {|key|
		parameterTracks.collect({|t|
			t.removePattern(key);
		});
	}

	getPattern{|key|

		// parameterTracks[pattern.target].removePatterns(pattern);

	}

	setPattern{|key,parameters,pattern|
	}

	removePatterns {|pattern|

		parameterTracks[pattern.target].removePatterns(pattern);

	}


}
