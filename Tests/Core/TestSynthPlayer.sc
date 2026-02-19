TestSynthPlayer : I8TUnitTest
{

	test_onCreation_nodeNameEqualsInstrumentKey {
		main.key0 = "test";
		this.assert(main.key0.name=='key0');
	}

	test_onCreation_nodeStoredByInstrumentKey {
		main.key0 = "test";
		this.assert(main.nodes['key0']===main.key0);
	}

	test_onCreation_onValidSynthDefNameString_createsSynthPlayer {
		main.key0 = "test";
		this.assert(main.key0.isKindOf(I8TSynthPlayer));
	}
	test_onCreation_onValidSynthDefNameSymbol_createsSynthPlayer {
		main.key0 = 'test';
		this.assert(main.key0.isKindOf(I8TSynthPlayer));
	}
	test_onCreation_onInvalidSynthDefNameString_returnsNilIfNameNotFound {
		main.someName = "invalid synth name";
		this.assert(main.someName.isNil);
	}
	test_onCreation_onSynthDefNameSymbol_returnsNilIfNameNotFound {
		main.someName = 'invalid_synth_name';
		this.assert(main.someName.isNil);
	}

	test_parameterGetter_returnsParameterProxyOnSequenceable {
		main.key0 = "test";
		this.assert(main.key0.fm.isKindOf(I8TParameterProxy));
	}

	test_parameterProxySeq_createsParameterTrack {
		var track;
		main.key0 = "test";
		main.key0.fm.seq([20, 10, 1]);
		track = main.sequencer.sequencerTracks[main.key0.name];
		this.assert(track.parameterTracks[\fm].notNil);
	}

	test_parameterProxyClearAndReset_doNotBreak {
		main.key0 = "test";
		main.key0.fm.seq([20, 10, 1]);
		main.key0.fm.clear;
		main.key0.fm.reset;
		this.assert(main.key0.fm.isKindOf(I8TParameterProxy));
	}


}



// (
// 	s.boot;
// 	s.doWhenBooted({
// 		i=INSTRUMENT();
// 		i.kick="kickDamp";
// 		i.kick.seq="1";
//
// 	});
//
// )
//
// i.kick.channel
// i.kick.channel.bus.scope
// i.mixer.master[0].inbus.scope
//
// i.volume=3 ;
