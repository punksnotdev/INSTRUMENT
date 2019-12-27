TestSynthPlayer : I8TUnitTest
{

	test_onStringCreation_createsSynthPlayerByName {

		main.kick = "kickDamp";

		this.assert(main.kick.isKindOf(SynthPlayer));

	}

	test_onStringCreation_returnsNilIfNameNotFound {

		main.someName = "invalid synth name";
		this.assert(main.someName.isNil);

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
