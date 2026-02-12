TestI8TSequencer : I8TUnitTest
{


	tearDown {

	}


	test_isSequencer {
		this.assert(main.sequencer.isKindOf(Sequencer));
	}

	test_afterInit_hasCorrectTimeSignature {

		var seq = main.sequencer;

		this.assert(
			(seq.timeSignature.beats == 4) &&
			(seq.timeSignature.tick == (1/4))
		)

	}

	test_afterPlay_hasBarRoutine {
		main.sequencer.play;
		this.assert(main.sequencer.barRoutine.isKindOf(Routine));
		main.sequencer.stop;
	}

	test_afterPlay_hasBeatRoutine {
		main.sequencer.play;
		this.assert(main.sequencer.beatRoutine.isKindOf(Routine));
		main.sequencer.stop;
	}

	test_afterStop_barRoutineIsNil {
		main.sequencer.play;
		main.sequencer.stop;
		this.assert(main.sequencer.barRoutine.isNil);
	}

	test_afterStop_beatRoutineIsNil {
		main.sequencer.play;
		main.sequencer.stop;
		this.assert(main.sequencer.beatRoutine.isNil);
	}

	test_serverLatency_isSet {
		this.assert(main.server.latency == 0.05);
	}

}
