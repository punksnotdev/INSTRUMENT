TestSynthPlayer : I8TUnitTest
{

	test_onStringCreation_failsIfDoesntExist {

		var instrument;

		main.someName = "invalid synth name";

		instrument = main.someName;

		instrument.class.postln;

		this.assert(instrument.isKindOf(SynthPlayer)==false);
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
