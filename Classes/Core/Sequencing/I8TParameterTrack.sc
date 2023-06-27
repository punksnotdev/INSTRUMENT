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

		patterns = IdentityDictionary.new();
		patternEvents = IdentityDictionary.new();
		sequence = List.new();


		durationSequencer = Tdef(("durationSequencer_" ++ main.threadID ++ "_" ++ name ++"_"++track.instrument.name).asSymbol, {
			inf.do {


				var dur = 1;


				var beatPatternIndex;
				var beatValue;
				var currentPattern;


				if( this.currentEvent().notNil, {

					var channel;

					if( this.currentEvent().initialWait.isNil, {

						if(this.currentEvent().parameters[\waitBefore].notNil, {
							(this.currentEvent().parameters[\waitBefore] / currentSpeed).wait;
						});
						this.currentEvent().initialWait = true;
					});


					currentPattern = this.currentEvent().pattern;

					beatPatternIndex = (beats-sequenceInfo.indices[this.currentEvent().time]) % currentPattern.pattern.size;
					// beatPatternIndex = beats % currentPattern.pattern.size;

					beatValue = currentPattern.pattern[ beatPatternIndex ];

					channel = ("/"++name++"/"++track.instrument.name).asString;

					// netAddr.sendMsg( channel, beatValue.val );

					if( beatValue.notNil ) {

						if((( beatValue.val != \r)&&(beatValue != nil)), {
							track.instrument.trigger( name, beatValue );
							beatValue.played=true;
							currentPattern.played=true;
							this.currentEvent().played=true;
						});


						if( beatValue.duration.notNil, {

							dur = beatValue.duration.asFloat;
						});

						if( this.currentEvent().parameters[\speed] != nil, {
							currentSpeed = this.currentEvent().parameters[\speed] * speed;
							currentSpeed = currentSpeed.max(0.001);
						}, {
							currentSpeed = speed;
						});

						dur = dur / currentSpeed;

					};

				});


				beats = beats + 1;
				beats = beats % this.totalBeats();

				if( waitOffset == 0, {

					dur.wait;

				}, {
					(dur+waitOffset).wait;
					waitOffset = 0;

				});

			}
		});

		durationSequencer.quant=[2,0.2375];
	}

	fwd{|i|
		if( playing == true, {

			if( ( i % ( 128 / currentSpeed ).floor ) == 0, {


				var beatPatternIndex;
				var beatValue;
				var currentPattern;

				if( startSeq == true ) {

					beats = 0;

					durationSequencer.stop;
					durationSequencer.play(main.clock);

					startSeq = false;

				};


				if( this.currentEvent().notNil, {

					currentPattern = this.currentEvent().pattern;

					if( currentPattern.hasDurations == true, {

						if( durationSequencer.isPlaying == false, {
							durationSequencer.play(main.clock);
						});

					}, {

						if( durationSequencer.isPlaying == false, {
							durationSequencer.stop;
						});

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



							if( this.currentEvent().notNil, {
								if( this.currentEvent().parameters[\speed].notNil, {
									currentSpeed = this.currentEvent().parameters[\speed] * speed;
								}, {
									currentSpeed = speed;
								});
							});

						});


						beats = beats + 1;
						beats = beats % this.totalBeats();

					});
				});


			});

		});

	}


	play {|position|
		
		if( position != nil, { beats = position; });
		^playing = true;
	}
	stop {|position|
		if( position != nil, { beats = position; });
		if( durationSequencer.isPlaying, {
			durationSequencer.stop;
		});

		^playing = false;
	}

	go {|time|
		durationSequencer.reset();
		beats = time;
		if(time.isNil) {
			beats = 0;
		}
	}

	addPattern {|key,pattern,play_parameters|

		var eventName;
		var newKey;
		var newPatternEvent;

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


// test
		startSeq = true;


		^newPatternEvent;

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

		["pattEvents?",key,patternEvents,patternEvents[key]].postln;
		patternEvents[key].collect({|pattEvent|

			var seqindex;

			["pattEvent",pattEvent].postln;

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

	currentEvent {

		var nearestBeatCountKey;
		var currentIndex;

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
