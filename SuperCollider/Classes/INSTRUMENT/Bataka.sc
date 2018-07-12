Bataka
{

	var < instruments;
	var < sequencer;

	*new {

		^super.new.init();


	}

	init {

		sequencer = Sequencer.new();
		instruments = Dictionary.new();
		instruments[\kick] = Instrument.new();
		instruments[\kick].synth = Kick.new;

	}

	play {
	}



	getInstruments {
		^ instruments;
	}

	addInstrument {| name, instrument |
		instruments[name] = instrument;
	}



}
