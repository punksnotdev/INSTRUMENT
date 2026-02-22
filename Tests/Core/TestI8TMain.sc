TestI8TMain : I8TUnitTest
{

	test_afterInit_hasServer {
		this.assert(main.server.isKindOf(Server));
	}

	test_afterInit_isBooted {
		this.assert(main.isBooted==true);
	}

	test_afterInit_hasMixer {
		this.assert(main.mixer.isKindOf(I8TMixer));
	}

	test_namedClearRestore_doesNotRestartStoppedTrack {
		var track;

		main.key0 = "test";
		main.key0.seq("1");
		main.key0.stop;

		track = main.sequencer.sequencerTracks[main.key0.name];

		main.clear("slotA");
		main.restore("slotA");

		this.assert(track.playing == false);
	}

	test_unnamedClearRestore_doesNotRestartStoppedTrack {
		var track;

		main.key0 = "test";
		main.key0.seq("1");
		main.key0.stop;

		track = main.sequencer.sequencerTracks[main.key0.name];

		main.clear;
		main.restore;

		this.assert(track.playing == false);
	}


	// test_onPut_createsCorrectItemDependingOnInput {
	// 	this.assert(false);
	// }
	//
	// test_onPut_onValidName_createsCorrectItem {
	// 	this.assert(false);
	// }



}
