Sequenceable : I8TNode
{

	var <>sequencer;
	var <speed;

	var nextKey;

	*new{|graph_,name_|
		^super.new.init(this.graph,name_);
	}

	init{|graph_,name_|
		super.init(graph_,name_);
		speed = 1;
		nextKey = 0;
		this.play;
	}

	remove{
		this.sequencer.unregisterInstrument(this);
	}

	kill{
		this.remove(this);
	}


	seq {|parameter_,pattern_,play_parameters_,key_|
		var key;
		var parameter;
		var pattern;
		var play_parameters;

		// if first argument not a symbol, its not a parameter. use default 'trigger'

		if( parameter_.isKindOf(Symbol) == true, {

			parameter = parameter_;

			if( (pattern_.isKindOf(String) || pattern_.isKindOf(Array) ), {

				pattern = pattern_;

			}, {
				^nil;
			});

			if( play_parameters_.isKindOf(Array) ) {
				play_parameters = play_parameters_;
			};

		},
		{
			if( (parameter_.isKindOf(String) || parameter_.isKindOf(Array) ), {

				pattern = parameter_;

				if( pattern_.isKindOf(Array) ) {
					play_parameters = pattern_;
				};

			}, {
				^nil
			});

			parameter = \trigger;

		});

		key = key_;

		if( key_.isNil ) {
			key = nextKey;
		};


		^sequencer.addPattern(
			name,
			parameter,
			key,
			pattern,
			play_parameters
		);

	}

	rm {|parameter_,key_|

		var key = key_;
		var parameter = parameter_;

		if( key.isNil ) {

			if( parameter.isKindOf(Symbol) == true, {

				// clear all patterns
				sequencer.clearPatterns(name,\trigger);
			});


		};

		sequencer.removePattern(name,parameter,key);

	}


	get {|parameter,key|
		^sequencer.getPattern(name,parameter,key);
	}
	set {|parameter,key,parameters|
		^sequencer.setPatternParameters(name,parameter,key,parameters);
	}


	play {|position|
		if( sequencer.notNil ) {
			^sequencer.playInstrument( this, position );
		}
	}
	stop {|position|
		if( sequencer.notNil ) {
			^sequencer.stopInstrument( this );
		}
	}

	go{|time|
		if( time.isKindOf(Number)) {
			^sequencer.instrument_tracks[name].parameterTracks.collect{|track|
				track.go(time)
			};
		};
	}


	trigger {
		// do something
	}


	speed_{|sp_|
		if( speed.isKindOf(Number)) {
			speed = sp_;
			sequencer.setSpeed(name,speed);
		}
	}



	patterns{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].patterns;
	}
	sequence{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequence;
	}
	sequenceInfo{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequenceInfo;
	}

	at{|key|
		nextKey = key;
		^this
	}

}
