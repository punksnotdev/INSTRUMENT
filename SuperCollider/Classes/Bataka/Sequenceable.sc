Sequenceable : InstrumentNode
{

	var <>sequencer;

	play {
		sequencer.playInstrument( this );
	}

}
