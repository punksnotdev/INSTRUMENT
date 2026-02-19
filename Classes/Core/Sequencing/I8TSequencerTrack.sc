// instruments se va a tomar de nodegraph!

SequencerTrack
{

	classvar <>classSequencer;
	var <sequencer;

	var main;

	var <>parameterTracks;

	var <>instrument;
	var <>name;

	var <playing;

	var <>repeats;
	var <speed;
	var currentSpeed;
	var <>beats;


	var playNext;

	*new {|instrument_, main_|
		^super.new.init(instrument_, main_);
	}

	init {|instrument_, main_|

		main = main_;

		sequencer = classSequencer;

		if( (instrument_.isKindOf(Sequenceable) ), {
		// if( (instrument_.isKindOf(I8TInstrument) || instrument_.isKindOf(InstrumentGroup)), {
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



	play {|position|
		if( position != nil, { beats = position; });

		parameterTracks.do({|p| p.play(position); });

		^playing = true;
	}
	stop {|position|
		if( position != nil, { beats = position; });

		parameterTracks.do({|p| p.stop(position); });

		^playing = false;
	}

	addPattern {|parameter,key,pattern,play_parameters,test|
		var patternEvent;

		if( parameterTracks[ parameter ] == nil, {
			parameterTracks[ parameter ] = ParameterTrack.new( this, parameter, main );
		});


		patternEvent = parameterTracks[ parameter ].addPattern(key,pattern,play_parameters,test);

		if( test.isNil || test.asSymbol != \test ) {
			if( parameterTracks[ parameter ].playing != true, {
				parameterTracks[ parameter ].play;
			}, {
				parameterTracks[ parameter ].pendingRestart = true;
			});
		};


		^patternEvent;

	}

	removePattern {|parameter,key|
		parameterTracks[parameter].removePattern(key);
	}

	removePatterns {|parameter,pattern|

		parameterTracks[parameter].removePatterns(pattern);

	}

	clearPatterns {|parameter|
		parameterTracks[parameter].clear;
	}



	getPattern{|parameter,key|
		^parameterTracks[parameter].getPattern(key);
	}

	getPatterns{|parameter,key|
		^parameterTracks[parameter].getPatterns();
	}


	setPatternParameters{|parameter,key,play_parameters|
		// if( key.isArray, {}, {
		// 	[]
		// })
		parameterTracks[parameter].setPatternParameters(key,play_parameters);

		["key,play_parameters", key,play_parameters].postln;

		"TO-DO: implement set pattern!"

	}


	speed_{|sp_|
		speed = sp_;
		parameterTracks.do({|t|
			t.speed = speed;
		});
	}

	shuffle {|amount, grid|
		parameterTracks.do({|t|
			t.shuffle = amount ? 0;
			if(grid.notNil) { t.shuffleGrid = grid };
		});
	}

	go {|time|
		parameterTracks.do({|t|
			t.go(time);
		});
	}

}
