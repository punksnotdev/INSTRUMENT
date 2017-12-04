ParameterTrack
{

	var track;
	var <>name;

	var <patterns;
	var <sequence;
	var <patternEvents;

	var <playing;

	var <>repeats;
	var <>speed;
	var currentSpeed;
	var <>beats;

	var <sequenceInfo;

	*new{|track_,name_|
		^super.new.init(track_,name_);
	}
	init{|track_,name_|
		track = track_;
		name = name_;

		repeats = track.repeats;
		speed = track.speed;
		currentSpeed = speed;

		beats = 0;

		patterns = IdentityDictionary.new();
		patternEvents = IdentityDictionary.new();
		sequence = List.new();

	}

	fwd{|i|


		if( playing == true, {

			if( ( i % ( 32 / currentSpeed ).floor ) == 0, {

				var beatPatternIndex;
				var beatValue;
				var currentPattern;

				// name.postln;

				if( this.currentEvent().notNil, {

					currentPattern = this.currentEvent().pattern;

					beatPatternIndex = beats % currentPattern.pattern.size;

					beatValue = currentPattern.pattern[ beatPatternIndex ];

					if( beatValue != \r, {

						track.instrument.trigger( currentPattern.target, beatValue );

					});

					if( this.currentEvent().parameters[\speed] != nil, {
						currentSpeed = this.currentEvent().parameters[\speed];
					}, {
						currentSpeed = speed;
					});

				});

				beats = beats + 1;
				beats = beats % this.totalBeats();

			});

		});

	}


	play {|position|
		if( position != nil, { beats = position; });
		^playing = true;
	}
	stop {|position|
		if( position != nil, { beats = position; });
		^playing = false;
	}

	addPattern {|key,pattern,parameters|

		var eventName;
		var newEvent;

		if( key == nil, {

			var found = -1;
			block{|break|
				patterns.keysValuesDo({|key,item|
					if( item.pattern == pattern.pattern, {
						found = key;
						break.value;
					});
				});
			};

			key = patterns.size;

			if( found >= 0, {
				if( patterns[found] != nil, {
					key = found;
				});
			});

		});

		eventName = ("pattern" ++ "-" ++ track.name ++ "-" ++ pattern.target.asString ++ "-" ++ key).toLower;

		newEvent = PatternEvent.new( pattern, eventName);

		newEvent.pattern = pattern;

		if( parameters.isArray, {
			var paramDict = parameters.asDict;
			newEvent.parameters[\repeat] = paramDict[\repeat];
			newEvent.parameters[\speed] = paramDict[\speed];
		});

		if(patternEvents[key]==nil,{
			patternEvents[key] = List.new;
		});

		sequence.add( newEvent );

		patternEvents[key].add(newEvent);

		this.updateSequenceInfo();

		patterns[ key ] = pattern;

		// sequenceInfo.postln;

	}

	removePattern {|key|

		var eventKey;

		if(key.isKindOf(I8Tpattern),{
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

				while({patternEvents[eventKey].size>0},{
					this.removePatternEvents(key);
				});

			})
		});

		this.updateSequenceInfo();

	}

	getPattern{|key|

		// [key,patterns.findKey( key )].postln;
		if( key.isArray, {
			^patterns[patterns.findKey( key )];
		}, {
			^patterns[key]
		});
	}

	setPattern{|key,parameters,pattern|
		// this.removePattern(pattern);
		// this.seq(pattern,parameters);
	}

	removePatterns {|pattern|

		if(pattern.isKindOf(I8Tpattern),{
			patterns.collect({|p,k|
				if(p==k,{
					patterns[k] = nil;
					while({patternEvents[k].size>0},{
						this.removePatternEvents(k);
					});
				})
			});
		});

	}


	removePatternEvents {|key|

		var seqindex;
		var pattevent;

		pattevent = patternEvents[key].pop;

		sequence.reverseDo({|se,si|
			if( se == pattevent, {
				seqindex = (sequence.size-1)-si;
			});
		});

		if(seqindex!=nil, {
			if(sequence[seqindex]!=nil, {
				sequence.removeAt(seqindex);
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

		sequence.collect({|e|

			var numBeats;
			var repetitions;

			repetitions = track.sequencer.repeat;

			if( e.pattern.pattern.isArray, {
				if( e.parameters.isKindOf(Dictionary), {
					if( e.parameters[\repeat] != nil, {
						repetitions = e.parameters[\repeat];
					});
				});

				numBeats = e.pattern.pattern.size * repetitions;

				sequenceInfo[ totalSequenceEventBeats ] = e.pattern;
				totalSequenceEventBeats = totalSequenceEventBeats + numBeats;

			});
		});


	}

	currentEvent {

		var nearestBeatCountKey;
		var currentIndex;
		// sequenceInfo.postln;
		nearestBeatCountKey = sequenceInfo.indices.findNearest( beats );

		currentIndex = sequenceInfo.indices.indexOfNearest( beats );


		if( nearestBeatCountKey == nil, {
			^nil;
		}, {

			if( nearestBeatCountKey > beats, {
				currentIndex = currentIndex - 1;
			});

			^sequence[ currentIndex ];

		})

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
}
