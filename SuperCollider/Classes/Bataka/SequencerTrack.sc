// instruments se va a tomar de nodegraph!

SequencerTrack
{

	classvar <>classSequencer;
	var sequencer;

	var <>instrument;
	var <>name;

	var <patterns;
	var <sequence;
	var <patternEvents;

	var <playing;

	var <>repeats;
	var <>speed;
	var currentSpeed;
	var <>beats;

	var sequenceInfo;

	*new {|instrument_|
		^super.new.init(instrument_);
	}

	init {|instrument_|

		sequencer = classSequencer;

		if( instrument_.isKindOf(Instrument), {
			instrument = instrument_;
			name = instrument.name;
		}, {
			name = instrument_;
		});

		repeats = 4;
		speed = 1;
		currentSpeed = 1;

		beats = 0;

		patterns = IdentityDictionary.new();
		patternEvents = IdentityDictionary.new();
		sequence = List.new();

	}



	fwd{|i|



		if( playing == true, {

			if( ( i % ( 32 / currentSpeed ).floor ) == 0, {

				var beatPatternIndex = beats % this.currentEvent().pattern.size;
				var beatValue = this.currentEvent().pattern[ beatPatternIndex ];

				instrument.noteOn( beatValue );

				if( this.currentEvent().parameters[\speed] != nil, {

					currentSpeed = this.currentEvent().parameters[\speed];

					}, {
						currentSpeed = speed;
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
			key = patterns.size;
		});



		eventName = ("pattern" ++ "-" ++ name ++ "-" ++ key).toLower;

		newEvent = PatternEvent.new( pattern, eventName);

		newEvent.pattern = pattern;

		if( parameters.isArray, {
			var paramDict = parameters.asDict;
			newEvent.parameters[\repeat] = paramDict[\repeat];
			newEvent.parameters[\speed] = paramDict[\speed];
		});

		sequence.add( newEvent );

		if(patternEvents[key]==nil,{
			patternEvents[key] = List.new;
		});

		patternEvents[key].add(newEvent);

		this.updateSequenceInfo();

		patterns[ key ] = pattern;

	}


	removePattern {|key|
		var eventKey;

		if(key.isKindOf(Array),{
			var pattern;
			var k;

			pattern = key;

			k = patterns.findKeyForValue( pattern );
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


	}


	removePatterns {|pattern|

		if(pattern.isKindOf(Array),{
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
				seRepeats = sequencer.repeat;
			}, {
				seRepeats = e.parameters[\repeat];
			});

			if( e.parameters[\speed] == nil, {
				seSpeed = sequencer.speed;
			}, {
				seSpeed = e.parameters[\speed];
			});

			totalBeatsInSeq = totalBeatsInSeq + (e.pattern.size * ( seRepeats ));
			// totalBeatsInSeq = totalBeatsInSeq + (e.pattern.size * ( seRepeats / seSpeed ));

		});

		^totalBeatsInSeq;

	}

	updateSequenceInfo {
		var totalSequenceEventBeats;

		totalSequenceEventBeats = 0;

		sequenceInfo = Order.new;

		sequence.collect({|e|
			if( e.pattern.isArray && e.parameters[\repeat] != nil, {
				var numBeats = e.pattern.size * e.parameters[\repeat];
				sequenceInfo[ totalSequenceEventBeats ] = e.pattern;
				totalSequenceEventBeats = totalSequenceEventBeats + numBeats;
			});
		});

	}

	currentEvent {

		var nearestIndex = sequenceInfo.indices.indexOfNearest(beats);
		var currentIndex;

		currentIndex = sequenceInfo.indices.indexOfNearest( beats - 1);

		^sequence[ currentIndex ];

	}

}
