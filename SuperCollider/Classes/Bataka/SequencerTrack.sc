// instruments se va a tomar de nodegraph!

SequencerTrack
{

	var <>instrument;
	var <>name;

	var <patterns;
	var <sequence;
	var <patternEvents;

	var <playing;

	*new {|instrument_|
		^super.new.init(instrument_);
	}

	init {|instrument_|

		if( instrument_.isKindOf(Instrument), {
			instrument = instrument_;
			name = instrument.name;
		}, {
			name = instrument_;
		});

		patterns = IdentityDictionary.new();
		patternEvents = IdentityDictionary.new();
		sequence = List.new();

	}

	play {|position|
		^playing = true;
	}
	stop {|position|
		^playing = false;
	}

	addPattern {|key,pattern,parameters|

		var eventName;
		var newEvent;


		if( key == nil, {
			key = patterns.size;
		});



		eventName = ("pattern" ++ "-" ++ name ++ "-" ++ key).toLower;

		newEvent = I8Tevent.new( this, {|e,l| [e,l].postln; }, eventName);


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

		patterns[ key ] = pattern;

	}


	removePattern {|key|
		var eventKey;

		if(key.isKindOf(Array),{
			var pattern;
			// var k;

			pattern = key;
			// "isarray".postln;
			patterns.collect({|p,k|
				if(p==key,{
			// k = patterns.findKeyForValue( pattern );
			// if( patterns[k] != nil, {

				this.removePatternEvents(k);
				if( patternEvents[k].size <= 0, {
					patterns[k] = nil;
					patternEvents[k] = nil;
				});

			// });
				})
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


}
