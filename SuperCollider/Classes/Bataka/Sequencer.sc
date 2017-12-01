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
		sequence = List.new();

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
		var newEvent;

		if( patterns[ track ] == nil, {
			patterns[ track ] = Dictionary.new;
		});



		if( patterns[ track ][ key ] == nil, {

			eventName = pattern.class.name;
			eventName = eventName ++ "-" ++ track ++ "-" ++ key;
			eventName = eventName.toLower;

			newEvent = I8Tevent.new( this, {|e,l| [e,l].postln; }, eventName);

			sequence.add( newEvent );

			// if( repetitions != nil && repetitions != 0 ) {
			// [track][key] = repetitions;
			// }

		});


		patterns[ track ][ key ] = pattern;


	}


	getPattern {|i|
		^patterns[i];
	}
	getBeat {|pattern,beat|
		^this.getPattern(pattern)[beat]
	}

}
