TestI8TSequencer : I8TUnitTest
{


	tearDown {

	}


	test_isSequencer {
		this.assert(main.sequencer.isKindOf(Sequencer));
	}

	test_hasTdef {
		this.assert(main.sequencer.tdef.isKindOf(Tdef));
	}


	test_afterInit_hasCorrectTimeSignature {

		var seq = main.sequencer;

		this.assert(
			(seq.timeSignature.beats == 4) &&
			(seq.timeSignature.tick == (1/4))
		)

	}


}
