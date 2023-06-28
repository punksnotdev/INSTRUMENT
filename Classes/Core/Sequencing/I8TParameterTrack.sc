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
	var <newSequenceInfo;
	var <sequenceDuration;

	var waitOffset;


	var currentTick;
	var lastTick;
	var nextDuration;

	var startSeq;

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




		durationSequencer = {

			var currentPattern;
			var currentEvent = this.getCurrentEventNew();


			if( currentEvent.notNil ) {

				var channel;

				currentPattern = currentEvent.pattern;

				track.instrument.trigger( name, currentEvent );
				currentEvent.played=true;

			};

		};

	}

	fwd {|i|


		// TODO: ver si se puede restaurar tras merge
		// if( startSeq == true ) {

		// 	beats = 0;

		// 	durationSequencer.stop;
		// 	durationSequencer.play(main.clock);

		// 	startSeq = false;

		// };

		if( playing == true ) {

			durationSequencer.value();

  		};

  		currentTick = main.sequencer.ticks;

  	}


	play {|position|

	    if( position != nil, {
	      beats = position;
	    }, {
		});

	    ^playing = true;

	}

	stop {|position|
		if( position != nil, { beats = position; });
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

		startSeq = true;


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


	getScaledDuration {| event, patternEvent |

		var patternSpeed = patternEvent.parameters[\speed];
		var duration = event.duration;

		if( patternSpeed.isNil ) { patternSpeed = 1 };
		if( duration.isNil ) { duration = 1 };

		^(duration / patternSpeed)

	}

	updateSequenceInfo {

		var totalSequenceEventBeats;
		var totalSequenceDurations;

		newSequenceInfo = Order.new;

		totalSequenceEventBeats = 0;
		totalSequenceDurations = 0;

		sequenceInfo = Order.new;

		sequence.collect({|patternEvent|

			var numBeats;
			var repetitions;

			var totalSequenceEventDurations = 0;

			repetitions = track.sequencer.repeat;
			if( patternEvent.pattern.pattern.isArray, {

				if( patternEvent.parameters.isKindOf(Dictionary), {
					if( patternEvent.parameters[\repeat] != nil, {
						repetitions = patternEvent.parameters[\repeat];
					});
				});

				numBeats = patternEvent.pattern.pattern.size * repetitions;

				// sequenceInfo[ totalSequenceEventBeats ] = patternEvent.pattern;

				patternEvent.pattern.pattern.do({|event|

					totalSequenceEventDurations = totalSequenceEventDurations  + this.getScaledDuration(event,patternEvent);

				});


				repetitions.do({|index|
					var repetitionStart = totalSequenceDurations + (totalSequenceEventDurations * index);

					var lastEventMoment = 0;


					patternEvent.pattern.pattern.do({|event|

						var modifiedEvent = event.copy;
						var eventMoment = repetitionStart + lastEventMoment;

						modifiedEvent.duration = this.getScaledDuration(event,patternEvent);

						// modifiedEvent.durationModified = true;

						// this.getScaledDuration(event,patternEvent);

						newSequenceInfo[ eventMoment ] = modifiedEvent;

						lastEventMoment = lastEventMoment + modifiedEvent.duration;

					});

				});

				totalSequenceDurations = totalSequenceDurations + (totalSequenceEventDurations * repetitions);

				sequenceDuration = totalSequenceDurations;

				totalSequenceEventBeats = totalSequenceEventBeats + numBeats;

			});



		});


	}

	showSequenceInfo {

		Task.new({

			1.wait;

			1.wait;

			newSequenceInfo.do({|event,index|
				["newSequenceInfo", index, newSequenceInfo[ index ]].postln;
				0.1.wait;
			});

		}).play;
	}

	getCurrentEvent {

		var nearestBeatCountKey;
		var currentIndex;



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

	getCurrentEventNew {

		var patternPosition;

		var nearestEventKey;
		var currentIndex;
		var currentEvent;

		patternPosition = currentTick / main.sequencer.tickTime;
		patternPosition = patternPosition * speed;



		nearestEventKey = newSequenceInfo.indices.findNearest( patternPosition % sequenceDuration );

		currentIndex = newSequenceInfo.indices.indexOfNearest( patternPosition % sequenceDuration );

		if( nearestEventKey.notNil ) {
			// reset all events when sequence restarts after it is done:
			if( (currentIndex == 0) && (newSequenceInfo[ nearestEventKey ].notNil) ) {
				if( ( newSequenceInfo[ nearestEventKey ].played == true ) ) {
					newSequenceInfo.collect({|e, i|
						if( (  i > 0  ) && ( e.notNil ) ) {
							e.played = false;
						};
					});
				};
			};

			// if last event, prepare first
			if( ( currentIndex == (newSequenceInfo.size-1) ) && ( newSequenceInfo[0].notNil ) ) {
				newSequenceInfo[0].played = false;
			};
			// check if last read event has been played
			if( (patternPosition % sequenceDuration) > nearestEventKey ) {
				if( ( newSequenceInfo[ nearestEventKey ].notNil ) && ( newSequenceInfo[ nearestEventKey ].played != true ) ) {
					currentEvent = newSequenceInfo[ nearestEventKey ];
				};
			};


			^currentEvent;
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
