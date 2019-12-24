TestAudio : I8TUnitTest
{

	test_afterInit_hasMixer {
		this.assert(main.mixer.isKindOf(I8TMixer));
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
