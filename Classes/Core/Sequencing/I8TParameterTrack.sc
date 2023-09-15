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
	var <sequenceInfo2;
	var <sequenceInfo2Meta;
	var <sequenceDuration;

	var waitOffset;


	var currentTick;
	var lastTick;
	var nextDuration;

	var startSeq;

	var currentPlayingKey;

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


		sequenceInfo2 = Order.new;
		sequenceInfo2Meta = Order.new;


		durationSequencer = {

			var currentPattern;
			var currentEvent = this.getCurrentEventNew();
			
			if( currentEvent.notNil ) {

				var channel;
				currentPlayingKey = currentEvent.key;

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

			if( startSeq == false, {

				durationSequencer.value();
			
			}, {

				if( currentTick % main.sequencer.tickTime == 0 ) {

					durationSequencer.value();

					startSeq = false

				} 

			});

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
			var keysArray = patterns.keys.asArray.sort;
			
			var largest = 0;

			if( keysArray.size > 0 ) {
				largest = keysArray.at( keysArray.size -1 );
			};

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

			// if( newPatternEvent.pattern.pattern.size > 128 ) {
			// 	( "Pattern too long: " ++ newPatternEvent.pattern.pattern.size ++ ". Max size: 128" ).warn;

			// 	newPatternEvent = PatternEvent.new( I8TPattern(" "), eventName);

		
			// };


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


			// TODO: refactor: "test" conditional should not wrap more important things
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

				this.updateSequenceInfo( newPatternEvent, key );


			};

			if( currentPlayingKey.isNil || key == currentPlayingKey ) {

				startSeq = true;
	
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

	// TODO: revisar nomenclatura de función, porque los elementos son de dur variable
	// el sistema podría aparentar estar en beats homogeneos, pero quizás no lo está, verificar:
	// (sin embargo funciona bien)
	totalBeats {

		var totalBeatsInSeq = 0;

		sequence.do({|e|

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
			totalBeatsInSeq = totalBeatsInSeq.max(4);
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


	updatePatternEvent  {|patternEvent, key, startMoment|

		var task;
		

		// TODO: revisar nomenclatura numbeats, quizas no son "beats" propiamente
		var numBeats;
		var repetitions;

		var totalPatternEventDurations = 0;

		var patternInfo = Order.new;

		var eventMoment;

		if( startMoment.isNil == true ) {

			startMoment = 0;

		};
		// ["patternEvent, key",patternEvent, key].postln;

		repetitions = track.sequencer.repeat.max(4);

		if( patternEvent.pattern.notNil && patternEvent.pattern.pattern.isArray, {
			

			if( patternEvent.parameters.isKindOf(Dictionary), {
				if( patternEvent.parameters[\repeat] != nil, {
					repetitions = patternEvent.parameters[\repeat];
				});
			});

			numBeats = patternEvent.pattern.pattern.size * repetitions;



			["numBeats", numBeats].postln;
			["repetitions", repetitions].postln;
			["startMoment", startMoment].postln;
			["patternEvent", patternEvent.pattern].postln;

			// task = Task.new({
		
			eventMoment = startMoment;

			repetitions.do({|index|



				patternEvent.pattern.pattern.do({|event|

					var modifiedEvent = event.copy;
					modifiedEvent.duration = this.getScaledDuration(event,patternEvent);
					modifiedEvent.key = key;
					
					totalPatternEventDurations = totalPatternEventDurations + modifiedEvent.duration;
										
					patternInfo[ eventMoment ] = modifiedEvent;

					eventMoment = eventMoment + modifiedEvent.duration;

					["eventMoment!!", eventMoment].postln;

				});

			});


			^(
				patternInfo: patternInfo,
				duration: totalPatternEventDurations
			);




		});
	
	}
	
	updateSequenceInfo {|patternEvent, key|

		var updatePattern;
		var totalSequenceDurations = 0;

		var lastMoment = 0;

		var updated;

		if( patternEvent.notNil == true ) {

			updated = this.updatePatternEvent(patternEvent, key, lastMoment);


			sequenceInfo2 = sequenceInfo2 ++ updated.patternInfo;
			
			sequenceInfo2Meta[ key ] = (
				duration: updated.duration,
				moment: lastMoment 
			);

			["lastMoment", sequenceInfo2.indices].postln;

			// ["patternEvent", patternEvent].postln;
			["updated", updated.duration ].postln;

		};

	}

	showSequenceInfo {

		Task.new({

			1.wait;

			1.wait;

			sequenceInfo2.do({|event,index|
				["sequenceInfo2", index, sequenceInfo2[ index ]].postln;
				0.1.wait;
			});

		}).play;
	}

	getCurrentEvent {

		var nearestBeatCountKey;
		var currentIndex;



		if( sequenceInfo.notNil ) {


			// TODO: cambiar algoritmo:
			// 1. Arreglo está ordenado
			// 1. Arreglo está ordenado

			



			currentIndex = sequenceInfo.indices.indexOfNearestIrregularIndex( beats );

			nearestBeatCountKey = sequenceInfo.indices.at( currentIndex );

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

		patternPosition = (currentTick / main.sequencer.tickTime);
		patternPosition = patternPosition * speed;



		nearestEventKey = sequenceInfo2.indices.findNearest( patternPosition % sequenceDuration );

		currentIndex = sequenceInfo2.indices.indexOfNearestIrregularIndex( patternPosition % sequenceDuration );

		if( nearestEventKey.notNil ) {
			// reset all events when sequence restarts after it is done:
			if( (currentIndex == 0) && (sequenceInfo2[ nearestEventKey ].notNil) ) {
				if( ( sequenceInfo2[ nearestEventKey ].played == true ) ) {
					sequenceInfo2.do({|e, i|
						if( (  i > 0  ) && ( e.notNil ) ) {
							e.played = false;
						};
					});
				};
			};

			// if last event, prepare first
			if( ( currentIndex == (sequenceInfo2.size-1) ) && ( sequenceInfo2[0].notNil ) ) {
				sequenceInfo2[0].played = false;
			};
			// check if last read event has been played
			if( (patternPosition % sequenceDuration) > nearestEventKey ) {
				if( ( sequenceInfo2[ nearestEventKey ].notNil ) && ( sequenceInfo2[ nearestEventKey ].played != true ) ) {
					currentEvent = sequenceInfo2[ nearestEventKey ];
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
