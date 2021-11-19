ParameterTrack
{

	var track;
	var <>name;
	var main;
	var <patterns;
	var <sequence;
	var <patternEvents;

	var <playing;

	var <>repeats;
	var <>speed;
	var currentSpeed;
	var <>beats;

	var netAddr;

	var durationSequencer;

	var <sequenceInfo;

	var waitOffset;


	var currentTick;
	var lastTick;
	var nextDuration;


	*new{|track_,name_,main_|
		^super.new.init(track_,name_,main_);
	}
	init{|track_,name_,main_|

				// netAddr = NetAddr("192.168.1.116",4567);

		track = track_;
		name = name_;
		main = main_;

		repeats = track.repeats;
		speed = track.speed;
		currentSpeed = speed;

		beats = 0;
		waitOffset=0;


		currentTick = 0;
		lastTick = 0;

		nextDuration = 0;

		patterns = IdentityDictionary.new();
		patternEvents = IdentityDictionary.new();
		sequence = List.new();




		durationSequencer = {|trigger=true|

			var dur = 1;


			var beatPatternIndex;
			var beatValue;
			var currentPattern;
			var nextBeat;
			var currentEvent = this.getCurrentEvent();


			nextBeat = ((currentTick.asInteger - lastTick.asInteger)) > (nextDuration * main.sequencer.tickTime);

			if( nextBeat == true ) {

				if( currentEvent.notNil, {

					var channel;

					if( currentEvent.initialWait.isNil, {

						if(currentEvent.parameters[\waitBefore].notNil, {
							(currentEvent.parameters[\waitBefore] / currentSpeed).wait;
						});
						currentEvent.initialWait = true;
					});


					currentPattern = currentEvent.pattern;

					beatPatternIndex = (beats-sequenceInfo.indices[ currentEvent.time ]) % currentPattern.pattern.size;
					// beatPatternIndex = beats % currentPattern.pattern.size;

					beatValue = currentPattern.pattern[ beatPatternIndex ];

					// // send osc message

					// channel = ("/"++name++"/"++track.instrument.name).asString;

					// netAddr.sendMsg( channel, beatValue.val );

					if( beatValue.notNil, {
						if( trigger == true ) {

							if((( beatValue.val != \r)&&(beatValue != nil)), {
								track.instrument.trigger( name, beatValue );
								beatValue.played=true;
								currentPattern.played=true;
								currentEvent.played=true;
							});

						};

						if( beatValue.duration.notNil, {

							dur = beatValue.duration.asFloat;

						});

						if( currentEvent.parameters[\speed] != nil, {
							currentSpeed = currentEvent.parameters[\speed] * speed;
							currentSpeed = currentSpeed.max(0.001);
						}, {
							currentSpeed = speed;
						});

						dur = dur / currentSpeed;

						nextDuration = dur;

					}, {

						nextDuration = 0;

					});

				});


				// beats = beats + 1;
				// helps with sync but breaks duration changes inside patterns:
				// beats = floor( (currentTick / (main.sequencer.tickTime)) * currentSpeed ).asInteger;

				beats = beats % this.totalBeats();

				// if( waitOffset == 0, {
				//
				// 	dur.wait;
				//
				// }, {
				// 	(dur+waitOffset).wait;
				// 	waitOffset = 0;
				//
				// });

				lastTick = currentTick;

			};



		};

	}

	fwd {|i|

  		if( playing == true, {

			// if( ( i % ( 128 / currentSpeed ).floor ) == 0, {

  				var beatPatternIndex;
  				var beatValue;
  				var currentPattern;
				var currentEvent = this.getCurrentEvent();


  				if( currentEvent.notNil, {

  					currentPattern = currentEvent.pattern;

  					if( currentPattern.hasDurations == true, {

  						durationSequencer.value();

  					}, {


  						beatPatternIndex = beats % currentPattern.pattern.size;

  						beatValue = currentPattern.pattern[ beatPatternIndex ];

  						if( beatValue.notNil, {
  							var theValue;

  							if( beatValue.isKindOf(Event), {
  								theValue = beatValue;
  							}, {
  								theValue = beatValue.val;
  							});

  							if( ((theValue != \r)&&(theValue != nil)), {
  								track.instrument.trigger( name, theValue );

  							});



  							if( currentEvent.notNil, {
  								if( currentEvent.parameters[\speed].notNil, {
  									currentSpeed = currentEvent.parameters[\speed] * speed;
  								}, {
  									currentSpeed = speed;
  								});
  							});

  						});


  						beats = (i / main.sequencer.tickTime).asInteger;
  						beats = beats % this.totalBeats();

  					});
  				});


  			// });


  		});

  		currentTick = main.sequencer.ticks;

  	}


	play {|position|


		// currentTick = main.sequencer.ticks;

	    if( position != nil, {
	      beats = position;
	      // currentTick = position * main.sequencer.tickTime;
	    }, {
			// beats = 0;
		});

	    // lastTick = currentTick;

	    // nextDuration = 0;
	    durationSequencer.value(trigger: false);
	    // if( position != nil, { beats = position; }, { beats = 0 });
	    ^playing = true;

	}

	stop {|position|
		if( position != nil, { beats = position; });
		// if( durationSequencer.isPlaying, {
		// 	durationSequencer.stop;
		// });

		^playing = false;
	}

	go {|time|
		// durationSequencer.stop();
		//
		// durationSequencer.play(main.clock, doReset: true, quant: 0);

		beats = time;
		if(time.isNil) {
			beats = 0;
		}
	}

	addPattern {|key,pattern,play_parameters,test|

		var eventName;
		var newKey;
		var newPatternEvent;

		var isKeyValid;


		if(key.isKindOf(Integer), {
			var largest = 0;

			patterns.keys.do({|k| if( k > largest) { largest = k; }; });

			if( key > (largest+1),  {
				isKeyValid = false;
				("Invalid key: keys must be sequential. Next valid key is: "++(largest+1)).warn;

			}, {
				isKeyValid = true;
			});

		}, {
			"Invalid key: key must be a number".warn;
			key = nil;
		});


		if( isKeyValid == true, {

			if( key == nil, {

				var found = -1;
				block{|break|
					patterns.keysValuesDo({|key_,item|
						if( (
							item.isKindOf(I8TPattern) && pattern.isKindOf(I8TPattern)
						), {

							if( item.pattern == pattern.pattern, {
								found = key_;
								break.value;
							});

						});
					});
				};

				if( found >= 0, {
					if( patterns[found] != nil, {
						key = found;
					});
				}, {
					key = patterns.size;
				});


			});


			eventName = (
				"pattern-" ++
				track.name ++ "-" ++
				name.asString ++ "-" ++
				key.asString
			).toLower;


			if( pattern.isKindOf(I8TPattern), {

				newPatternEvent = PatternEvent.new( pattern, eventName);

			}, {

				newPatternEvent = PatternEvent.new( I8TPattern(pattern), eventName);

			});


			if( play_parameters.isArray, {
				var paramDict = play_parameters.asDict;
				newPatternEvent.parameters[\repeat] = paramDict[\repeat];
				newPatternEvent.parameters[\speed] = paramDict[\speed];
				newPatternEvent.parameters[\waitBefore] = paramDict[\waitBefore];
			});

			if(patternEvents[key]==nil,{
				patternEvents[key] = List.new;
			});

			newPatternEvent.time=key;


			if( test.isNil || (test.asSymbol != \test) ) {


				if( sequence[key].notNil && newPatternEvent.notNil ) {
					if( sequence[key].played==true ) {

						waitOffset = this.calculateSyncOffset(sequence[key].pattern.pattern,newPatternEvent.pattern.pattern);
						beats = (beats - waitOffset).asInteger;
						waitOffset = waitOffset % 1;

					};
				};


				if(sequence[key].notNil,{

					sequence[key] = newPatternEvent;

				},{
					sequence.add( newPatternEvent );
				});


				patternEvents[key].add(newPatternEvent);
				patterns[ key ] = pattern;



				this.updateSequenceInfo();
			};

			^newPatternEvent;

		});

		"Pattern not added".warn;


	}

	removePattern {|key|

		var eventKey;
		if(key.isKindOf(I8TPattern),{
			var pattern;
			var k;

			pattern = key;
			k = this.findArray( pattern );

			if( patterns[k] != nil, {

				this.removePatternEvents(k);

				if( patternEvents[k].size <= 0, {
					patterns[k] = nil;
					patternEvents[k] = nil;
				});

			});

		},{
			if( key.isKindOf(Integer) || key.isKindOf(Symbol), {
				eventKey = key;
				patterns[key] = nil;
// ["rmvpatternEvents",name,key].postln;
				this.removePatternEvents(key);

			})
		});

		this.updateSequenceInfo();

	}


	removePatterns {|pattern|

		if(pattern.isKindOf(I8TPattern),{
			patterns.collect({|p,k|
				if(p==k,{
					patterns[k] = nil;
						this.removePatternEvents(k);
					})
			});
		});

		this.updateSequenceInfo();

	}

	clear {

		patterns.collect({|p,k|

			this.removePattern(k);

		});

		patterns = IdentityDictionary.new;
		patternEvents = IdentityDictionary.new;
		sequence = List.new;


	}



	getPattern{|key|

		if( key.isArray, {
			^patterns[patterns.findKey( key )];
		}, {
			^patterns[key]
		});
	}

	getPatterns{
		^patterns

	}

	setPattern{|key,play_parameters,pattern|
		// this.removePattern(pattern);
		// this.seq(pattern,play_parameters);
	}

	removePatternEvents {|key|

		patternEvents[key].collect({|pattEvent|

			var seqindex;


			sequence.reverseDo({|se,si|

				if( se == pattEvent, {
					seqindex = (sequence.size-1)-si;
				});
			});

			if(seqindex.notNil, {

				if(sequence[seqindex].notNil, {
					sequence.removeAt(seqindex);
				});
			});

		});
	}

	totalBeats {

		var totalBeatsInSeq = 0;

		sequence.collect({|e|

			var seRepeats;
			var seSpeed;

			if( e.parameters[\repeat] == nil, {
				seRepeats = track.sequencer.repeat;
			}, {
				seRepeats = e.parameters[\repeat];
			});

			if( e.parameters[\speed] == nil, {
				seSpeed = track.sequencer.speed;
			}, {
				seSpeed = e.parameters[\speed];
			});

			totalBeatsInSeq = totalBeatsInSeq + (e.pattern.pattern.size * ( seRepeats ));
			// totalBeatsInSeq = totalBeatsInSeq + (e.pattern.pattern.size * ( seRepeats / seSpeed ));

		});

		^totalBeatsInSeq;

	}

	updateSequenceInfo {

		var totalSequenceEventBeats;

		totalSequenceEventBeats = 0;

		sequenceInfo = Order.new;


		sequence.collect({|patternEvent|

			var numBeats;
			var repetitions;

			repetitions = track.sequencer.repeat;
			if( patternEvent.pattern.pattern.isArray, {

				if( patternEvent.parameters.isKindOf(Dictionary), {
					if( patternEvent.parameters[\repeat] != nil, {
						repetitions = patternEvent.parameters[\repeat];
					});
				});

				numBeats = patternEvent.pattern.pattern.size * repetitions;

				sequenceInfo[ totalSequenceEventBeats ] = patternEvent.pattern;
				totalSequenceEventBeats = totalSequenceEventBeats + numBeats;

			});

		});



	}

	getCurrentEvent {

		var nearestBeatCountKey;
		var currentIndex;



		["sequenceInfo.indices", sequenceInfo.indices].postln;

		if( sequenceInfo.notNil ) {
			nearestBeatCountKey = sequenceInfo.indices.findNearest( beats );

			currentIndex = sequenceInfo.indices.indexOfNearest( beats );

			if( nearestBeatCountKey == nil, {
				^nil;
			}, {

				if( sequence.notNil, {
					if( currentIndex.notNil, {
						if( nearestBeatCountKey > beats, {
							currentIndex = currentIndex - 1;
						});
					}, {
						currentIndex = 0;
					});
					^sequence[ currentIndex ];
				});

			})
		};

		^nil;

	}

	findArray{|pattern|
		var i;
		block{|break|
			patterns.keysValuesDo({|k,v|
				if( v.pattern == pattern.pattern, {
					i = k;
					break.value;
				});
			})
		}
		^i;
	}


	calculateSyncOffset{|a,b|

		var dA,dB;

		dA=a.collect({|item|if(item.duration.notNil, {item.duration}, {1})}).sum;
		dB=b.collect({|item|if(item.duration.notNil, {item.duration}, {1})}).sum;

		^(dA - dB);

	}

}
