Sequenceable : I8TNode
{

	var <>sequencer;
	var <clock;
	var <playing;
	var <>amp;

	var nextPatternKey;

	var currentPatternEvent;
	var currentPattern;
	var currentParameter;

	*new{|graph_,name_|
		^super.new.init(this.graph,name_);
	}

	init{|graph_,name_|
		super.init(graph_,name_);
		clock = 1;
		amp = 1;
		nextPatternKey = 0;
		this.play;
	}

	remove{
		this.stop;
		this.sequencer.unregisterInstrument(this);
	}

	kill{
		this.remove(this);
	}


	seq {|parameter_,pattern_|

		var parameters;

		parameters = this.orderPatternParameters(
			parameter_,
			pattern_
		);

		currentParameter = parameters.parameter;

		currentPatternEvent = sequencer.addPattern(
			name,
			parameters.parameter,
			nextPatternKey,
			parameters.pattern,
			parameters.play_parameters
		);

		currentPattern = currentPatternEvent.pattern.pattern;


		^this;

	}

	/* 'seq' shorthands */

	trigger {|pattern| this.seq(\trigger,pattern); }

	note {|pattern| this.seq(\note,pattern); }

	chord {|pattern| this.seq(\note,pattern); }

	vol {|pattern| this.seq(\amp,pattern); }

	pan {|pattern| this.seq(\pan,pattern); }




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


	clock_{|sp_|
		clock = sp_;
		sequencer.setSpeed(name,clock);
	}



	patterns {|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].patterns;
	}
	sequence {|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequence;
	}

	at {|key|
		nextPatternKey = key;
		^this
	}



	// Pattern Functions:



	speed {|n|
		if(n.isKindOf(Number)) {
			var speed = n.ceil.asInteger;
			currentPatternEvent.parameters[\speed]=speed;
		};
		this.closeFunction();
	}


	repeat {|n|
		if(n.isKindOf(Number)) {
			currentPatternEvent.parameters[\repeat]=n.ceil.asInteger;
		};
		this.closeFunction();
	}

	// 'repeat' aliases:
	do {|n| this.repeat(n); }
	x {|n| this.repeat(n); }
	one {|n| this.repeat(1); }



	// Pattern Transformation:


	reverse {
		currentPatternEvent = currentPatternEvent.reverse;
		this.closeFunction();
	}

	mirror {
		currentPatternEvent = currentPatternEvent.mirror;
		this.closeFunction();
	}

	pyramid {
		currentPatternEvent = currentPatternEvent.pyramid;
		this.closeFunction();
	}

	random {
		currentPatternEvent = currentPatternEvent.random;
		this.closeFunction();
	}

	maybe {|probability=0.5|
		currentPatternEvent.pattern.pattern.collect({

			arg patternEvent,index;

			if( 1.0.rand < probability ) {
				patternEvent.val = \r;
			};

		});
		this.closeFunction();
	}



	// utils, helpers

	closeFunction {
		sequencer.updateSequenceInfo( name, currentParameter );
		^this;
	}

	orderPatternParameters {

		arg
		parameter_,
		pattern_,
		play_parameters_;

		var
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

		^(
			parameter: parameter,
			pattern: pattern,
			play_parameters: play_parameters
		)

	}

}
