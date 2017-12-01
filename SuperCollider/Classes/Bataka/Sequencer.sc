//defaultRepetitions

Sequencer : I8Tnode
{

	var <patterns;
	var <sequence;

	var <>repeat;

	*new {
		^super.new.init();
	}

	init {

		patterns = Dictionary.new();

		repeat = 4;

	}

	play {


		// Tdef(\batako1,{
		// 	inf.do {
        //
		// 		instruments.collect({|instrument|
		// 			// instrument.getBeat[]
		// 			// instrument[pattern][currentBeat]
		// 			// instrument[synth].trig();
		// 			// instrument.ar;
		// 			// instrument.postln;
		// 		});
        //
		// 		1.wait;
        //
		// 	}
		// }).play;


	}

	playInstrument {|instrument|
		["play ",instrument, instrument.getName()].postln;
	}

	registerInstrument {|instrument|
		( "Register: " ++ instrument.getName()).postln;
	}

	addPattern {|track,key,pattern,repetitions|

		var eventName;

		if( patterns[ track ] == nil, {
			patterns[ track ] = Dictionary.new;
		});

		patterns[ track ][ key ] = pattern;

		if( repetitions != nil && repetitions != 0 ) {

			eventName = pattern.class.name;
			eventName = eventName ++ "-" ++ track ++ "-" ++ key;
			eventName = eventName.toLower;

			// var e = new i8tEvent( this, eventName );

			sequence.add(
				"Event"
			);
			// [track][key] = repetitions;

		}


	}


	getPattern {|i|
		^patterns[i];
	}
	getBeat {|pattern,beat|
		^this.getPattern(pattern)[beat]
	}

}
