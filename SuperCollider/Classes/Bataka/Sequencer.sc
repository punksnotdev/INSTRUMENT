Sequencer : InstrumentNode
{
	var <patterns;

	*new {
		^super.new.init();
	}

	init {
		patterns = Dictionary.new();
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


	addPattern {|pattern|
		patterns.add(
			pattern
		);
	}


	getPattern {|i|
		^patterns[i];
	}
	getBeat {|pattern,beat|
		^this.getPattern(pattern)[beat]
	}

}
