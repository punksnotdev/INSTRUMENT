Sequenceable : I8TNode
{

	var <>sequencer;
	var <clock;
	var <>baseClock;

	var <playing;
	var <>amp;

	var <>nextPatternKey;

	var currentPatternEvent;
	var currentPattern;
	var currentParameter;
	var minSpeed;

	var multiPattern;


	*new{|main_,name_|
		
		if(main_.notNil) {
			super.new(main_,name_);	
		};
		
		^super.new(main_,name_);
		
	}

	init{|main_,name_|
	
		multiPattern = false;
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

	setupSequencer {|sequencer_|		
		sequencer = sequencer_;
		sequencer.registerInstrument( this );
	}

	seq_ {|parameter_,pattern_|
		this.seq(parameter_,pattern_);
		^this;
	}

	seq {|parameter_,pattern_,test_|

		var parameter;
		var pattern;

		var parameters;		

		if( parameter_.isKindOf(Symbol), {
			parameter = parameter_;
			pattern = pattern_;
		}, {

			if( parameter_.isKindOf(Array) || parameter_.isKindOf(String) ) {
				parameter = \trigger;
				pattern = parameter_;
			};		
		});

		if( pattern.isKindOf(Array), {


			var subarrays = pattern.select({|item| item.isKindOf(Array) });

			// TODO: mejorar chequeo. ahora está asumiendo que viene bien
			if( subarrays.size == pattern.size, {

				
				var subsubarrays = subarrays.select({|item| item.select({|subitem| subitem.isKindOf(Array) }).size == item.size });

				
				this.multiPattern = true;

				// TODO: mejorar chequeo. ahora está asumiendo que viene bien
				if( subsubarrays.size == subarrays.size, {

					switch( parameter_,
						\note, {
							subarrays.do({|arr,index|
								this[index].note( arr[0] ).x( arr[1] )
							});
						}
					);


				}, {

					subarrays.do({|arr,index|
						this[index].seq( parameter_, arr )
					});

				});


				^this

			});
		});

		if( parameter.notNil && pattern.notNil, {

			parameters = this.orderPatternParameters(
				parameter,
				pattern
			);
			currentParameter = parameters.parameter;

			if( sequencer.notNil, {

				currentPatternEvent = sequencer.addPattern(
					name,
					parameters.parameter,
					nextPatternKey,
					parameters.pattern,
					parameters.play_parameters,
					test_
				);

				if( currentPatternEvent.isKindOf(PatternEvent), {

					currentPattern = currentPatternEvent.pattern.pattern;

				});

			}, {
				"seq: Sequencer is nil".warn;
			});
		}, {
			"seq: parameter or pattern is nil".warn;
		});

		^this;

	}

	/* 'seq' shorthands */

	trigger_ {|pattern,test| this.trigger(pattern,test); }
	trigger {|pattern,test| this.seq(\trigger,pattern,test); }

	note_ {|pattern,test| this.note(pattern,test); }
	note {|pattern,test| this.seq(\note,pattern,test); }

	chord_ {|pattern,test| this.chord(pattern,test); }
	chord {|pattern,test| this.seq(\chord,pattern,test); }

	vol_ {|pattern,test| this.vol(pattern,test); }
	vol {|pattern,test| this.seq(\amp,pattern,test); }

	pan_ {|pattern,test| this.pan(pattern,test); }
	pan {|pattern,test| this.seq(\pan,pattern,test); }

	fx_ {|pattern,test| this.fx(pattern,test); }
	fx {|pattern,test| this.seq(\fx,pattern,test); }

	fxSet_ {|pattern,test| this.fxSet(pattern,test); }
	fxSet {|pattern,test| this.seq(\fxSet,pattern,test); }



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
			"rm: Sequencer is nil".warn;
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

	pause {|position|

		playing = true;

		if( sequencer.notNil ) {
			^sequencer.stopInstrument( this );
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
			sequencer.sequencerTracks[name].parameterTracks.collect{|track|
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
		^sequencer.sequencerTracks[name].parameterTracks[parameter].patterns;
	}
	sequence {|parameter|
		^sequencer.sequencerTracks[name].parameterTracks[parameter].sequence;
	}

	at {|key|
		nextPatternKey = key;
		^this
	}



	// Pattern Functions:



	speed {|n|

		// if( this.multiPattern ) {

		// 	"MULTIPATTERN".warn;

		// 	^this

		// };

		if(n.isKindOf(Number)) {
			var speed = max(n.asFloat,minSpeed);
			if( n < minSpeed ) {
				("speed set to min speed value:" ++ minSpeed).warn
			};
			if( currentPatternEvent.isKindOf(PatternEvent) ) {
				currentPatternEvent.parameters[\speed]=speed;
			};
		};
		this.updateSequence();
	}


	repeat {|n|
		if(n.isKindOf(Number)) {
			if(currentPatternEvent.isKindOf(PatternEvent)) {
				currentPatternEvent.parameters[\repeat]=n.ceil.asInteger;
			};
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
		currentPatternEvent.mirror();
		this.updateSequence();
	}
	mirror1 {
		currentPatternEvent.mirror1();
		this.updateSequence();
	}
	mirror2 {
		currentPatternEvent.mirror2();
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


	rotate {|n=0|
		currentPatternEvent.rotate(n);
		this.updateSequence();
	}

	shift {|n=0|
		currentPatternEvent.shift(n);
		this.updateSequence();
	}

	lace {|length=0|
		currentPatternEvent.lace(length);
		this.updateSequence();
	}

	permute {|n=0|
		currentPatternEvent.permute(n);
		this.updateSequence();
	}



	transport {|n=0|
		currentPatternEvent.transport(n);
		this.updateSequence();
	}

	
	// utils, helpers

	updateSequence {
		if( currentParameter.notNil ) {
			sequencer.updateSequenceInfo( name, currentParameter );
		};
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

			if( pattern_.isKindOf(String) || pattern_.isKindOf(Array), {

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

	pattern {
		^currentPattern
	}

	duration {
		^currentPattern.collect({|e| e.duration }).sum()
	}

}
