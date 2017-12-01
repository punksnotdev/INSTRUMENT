// instruments se va a tomar de nodegraph!
//defaultRepetitions

SequencerTrack
{

	var <>instrument;
	var <>name;

	var <patterns;
	var <sequence;

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

		patterns = Dictionary.new();
		sequence = List.new();

	}

	play {
	}

	addPattern {|key,pattern,repetitions|

		var eventName;
		var newEvent;

		if( key == nil, {
			key = patterns.size;
		});

		if( patterns[ key ] == nil, {

			eventName = ("pattern" ++ "-" ++ name ++ "-" ++ key).toLower;

			newEvent = I8Tevent.new( this, {|e,l| [e,l].postln; }, eventName);
			newEvent.parameters["repetitions"] = repetitions;

			sequence.add( newEvent );

		});


		patterns[ key ] = pattern;

	}


}
