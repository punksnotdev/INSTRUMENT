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

	var <sequenceInfo;
	var <newSequenceInfo;
	var <sequenceDuration;

	var currentPlayingKey;

	var routine;

	*new{|track_,name_,main_|
		^super.new.init(track_,name_,main_);
	}
	init{|track_,name_,main_|

		track = track_;
		name = name_;
		main = main_;

		repeats = track.repeats;
		speed = track.speed;
		currentSpeed = speed;

		beats = 0;

		patterns = IdentityDictionary.new();
		patternEvents = IdentityDictionary.new();
		sequence = List.new();

	}

	play {|position, quantize|
		var startBeat;

		playing = true;
		quantize = quantize ? main.sequencer.timeSignature.beats;

		// Stop existing routine if any
		if(routine.notNil) { routine.stop; routine = nil };

		// Don't start if no sequence data
		if(newSequenceInfo.isNil) { ^playing };
		if(newSequenceInfo.size == 0) { ^playing };
		if(sequenceDuration.isNil) { ^playing };
		if(sequenceDuration <= 0) { ^playing };

		startBeat = position ? 0;

		routine = Routine({
			var previousBeatPos, waitBeats, remainingBeats;
			var cycleSequenceInfo, cycleDuration;

			loop {
				// Snapshot sequence data for this cycle so mid-cycle
				// hot-swaps take effect on the next loop, not mid-bar
				cycleSequenceInfo = newSequenceInfo;
				cycleDuration = sequenceDuration;

				previousBeatPos = startBeat * speed;

				cycleSequenceInfo.do {|event, beatPosition|
					waitBeats = (beatPosition - previousBeatPos) / speed;

					if(waitBeats > 0) {
						waitBeats.wait;
					};

					if(waitBeats >= 0) {
						main.server.makeBundle(main.server.latency, {
							track.instrument.trigger(name, event);
						});
					};

					previousBeatPos = beatPosition;
				};

				// Wait remaining time to complete the cycle
				remainingBeats = (cycleDuration - previousBeatPos) / speed;
				if(remainingBeats > 0) { remainingBeats.wait };

				startBeat = 0;
			};
		}).play(main.clock, quant: quantize);

		^playing;
	}

	stop {|position|
		playing = false;
		if(routine.notNil) { routine.stop; routine = nil };
		^playing;
	}

	go {|time|
		if(time.isNil) { time = 0 };
		beats = time;
		if(playing == true) {
			// Restart routine at new position
			this.stop;
			playing = true;
			this.play(time);
		};
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
			patterns.do({|p,k|
				if(p==k,{
					patterns[k] = nil;
						this.removePatternEvents(k);
					})
			});
		});

		this.updateSequenceInfo();

	}

	clear {

		patterns.do({|p,k|

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

		patternEvents[key].do({|pattEvent|

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

	updateSequenceInfo {

		var totalSequenceEventBeats;
		var totalSequenceDurations;

		newSequenceInfo = Order.new;

		totalSequenceEventBeats = 0;
		totalSequenceDurations = 0;

		sequenceInfo = Order.new;

		sequence.do({|patternEvent, key|

			var numBeats;
			var repetitions;

			var totalSequenceEventDurations = 0;

			repetitions = track.sequencer.repeat.max(4);

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
						modifiedEvent.key = key;

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

		// If currently playing, no restart needed — the routine snapshots
		// newSequenceInfo at each cycle boundary, so the new pattern
		// takes effect after the current loop finishes (no silence gap).

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
