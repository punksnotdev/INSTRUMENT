Sequenceable : I8TNode
{

	var <>sequencer;
	var <speed;
	var <playing;

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
		this.stop;
		this.sequencer.unregisterInstrument(this);
	}

	kill{
		this.remove(this);
	}


	seq {|parameter_,pattern_,play_parameters_,key_|

		var parameters;

		parameters = this.orderPatternParameters(
			parameter_,
			pattern_,
			play_parameters_,
			key_
		);

		parameters.postln;

		^sequencer.addPattern(
			name,
			parameters.parameter,
			parameters.key,
			parameters.pattern,
			parameters.play_parameters
		);

	}



	one {|parameter_,pattern_,play_parameters_,key_|

		var parameters;

		parameters = this.orderPatternParameters(
			parameter_,
			pattern_,
			play_parameters_,
			key_
		);


		if( parameters.play_parameters.isKindOf(IdentityDictionary) == false) {
			parameters.play_parameters = IdentityDictionary.new;
		};
parameters.play_parameters.postln;
		parameters.play_parameters = parameters.play_parameters[\repeat]=1;


		this.seq(
			parameters.parameter,
			parameters.pattern,
			parameters.play_parameters,
			parameters.key
		);

	}


	rm {|parameter_,key_|

		var key = key_;
		var parameter = parameter_;

		if( parameter_.isNil, {

			// sequencer.clearPatterns(name,\trigger);

		}, {
			if( key.isNil ) {

				if( parameter.isKindOf(Symbol) == true, {

					// clear all patterns
					sequencer.clearPatterns(name,\trigger);
				}, {

					sequencer.removePattern(name,parameter,key);

				});

			};
			if( key.notNil ) {

				sequencer.removePattern(name,parameter,key);

			};
		});


	}


	get {|parameter,key|
		^sequencer.getPattern(name,parameter,key);
	}
	set {|parameter,key,parameters|
		^sequencer.setPatternParameters(name,parameter,key,parameters);
	}


	play {|position|

		playing = true;

		if( sequencer.notNil ) {
			^sequencer.playInstrument( this, position );
		}
	}
	stop {|position|

		playing = false;

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



	patterns {|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].patterns;
	}
	sequence {|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequence;
	}

	at {|key|
		nextKey = key;
		^this
	}


	// utils, helpers
	sequenceInfo{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequenceInfo;
	}

	orderPatternParameters {

		arg
		parameter_,
		pattern_,
		play_parameters_,
		key_;

		var
		key,
		parameter,
		pattern,
		play_parameters;

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


		^(
			key: key,
			parameter: parameter,
			pattern: pattern,
			play_parameters: play_parameters
		)

	}

}
