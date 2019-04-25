Sequenceable : I8TNode
{

	var <>sequencer;
	var <clock;
	var <>baseClock;

	var <playing;
	var <>amp;

	var nextPatternKey;

	var currentPatternEvent;
	var currentPattern;
	var currentParameter;
	var minSpeed;

	*new{|graph_,name_|
		^super.new.init(this.graph,name_);
	}

	init{|graph_,name_|
		super.init(graph_,name_);
		clock = 1;
		nextPatternKey = 0;
		minSpeed=1/32;
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

		if( sequencer.notNil ) {

			currentPatternEvent = sequencer.addPattern(
				name,
				parameters.parameter,
				nextPatternKey,
				parameters.pattern,
				parameters.play_parameters
			);

			if( currentPatternEvent.notNil, {

				currentPattern = currentPatternEvent.pattern.pattern;

			});

		};

		^this;

	}

	/* 'seq' shorthands */

	test {|pattern| this.seq(\test,pattern); }

	trigger {|pattern| this.seq(\trigger,pattern); }

	note {|pattern| this.seq(\note,pattern); }

	chord {|pattern| this.seq(\chord,pattern); }

	vol {|pattern| this.seq(\amp,pattern); }

	pan {|pattern| this.seq(\pan,pattern); }

	fx {|pattern| this.seq(\fx,pattern); }

	fxSet {|pattern| this.seq(\fxSet,pattern); }




	rm {|parameter_,key_|

		var key = key_;
		var parameter = parameter_;


		if( parameter_ == \seq ) {
			parameter = \trigger;
		};

		if( sequencer.notNil, {
			if( parameter_.isNil, {

				sequencer.clearPatterns(name,\trigger);

			}, {
				if( key.isNil ) {

					if( parameter.isKindOf(Symbol) == true, {

						// clear all patterns
						sequencer.clearPatterns(name,parameter);

					}, {

						sequencer.clearPatterns(name,\trigger);

					});

				};
				if( key.notNil ) {

					sequencer.removePattern(name,parameter,key);

				};
			});
		}, {

				"Sequencer is nil".postln;
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
			^sequencer.sequencer_tracks[name].parameterTracks.collect{|track|
				track.go(time)
			};
		};
	}


	setClock{|speed_|
		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {

				var newClock = speed_;
				if( baseClock.notNil) {
					newClock = speed_ * baseClock;
				};
				sequencer.setSpeed(name,newClock);


			}
		}

	}


	clock_{|speed_|


		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {

				baseClock=speed_;
				clock=speed_;
				sequencer.setSpeed(name,speed_);

			}
		}
	}



	patterns {|parameter|
		^sequencer.sequencer_tracks[name].parameterTracks[parameter].patterns;
	}
	sequence {|parameter|
		^sequencer.sequencer_tracks[name].parameterTracks[parameter].sequence;
	}

	at {|key|
		nextPatternKey = key;
		^this
	}



	// Pattern Functions:



	speed {|n|
		if(n.isKindOf(Number)) {
			var speed = max(n.asFloat,minSpeed);
			if( n < minSpeed ) {
				("speed set to min speed value:" ++ minSpeed).warn
			};
			if( currentPatternEvent.notNil ) {
				currentPatternEvent.parameters[\speed]=speed;
			};
		};
		this.updateSequence();
	}


	repeat {|n|
		if(n.isKindOf(Number)) {
			currentPatternEvent.parameters[\repeat]=n.ceil.asInteger;
		};
		this.updateSequence();
	}

	// 'repeat' aliases:
	do {|n| this.repeat(n); }
	x {|n| this.repeat(n); }
	one {|n| this.repeat(1); }


	waitBefore {|n|
		if(n.isKindOf(Number)) {
			var speed = max(n.asFloat,0.01);

			currentPatternEvent.parameters[\waitBefore]=speed;
		};
		this.updateSequence();
	}



	// Pattern Transformation:


	reverse {
		currentPatternEvent.reverse;
		this.updateSequence();
	}

	mirror {
		currentPatternEvent.mirror;
		this.updateSequence();
	}

	pyramid {
		currentPatternEvent.pyramid;
		this.updateSequence();
	}

	random {
		currentPatternEvent.random;
		this.updateSequence();
	}

	maybe {|probability=0.5|
		currentPatternEvent.maybe(probability);
		this.updateSequence();
	}



	// utils, helpers

	updateSequence {
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

				// if( pattern_.isKindOf(Array) ) {
				// 	play_parameters = pattern_;
				// };

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
