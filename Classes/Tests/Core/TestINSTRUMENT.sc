TestINSTRUMENT : UnitTest
{

	var main;

	setUp {

		var options = ServerOptions.new.maxLogins_(4);
		var server = Server(this.class.name, nil, options, 3);

		server.bootSync;

		main = INSTRUMENT(server);

	}

	tearDown {
		I8TUnitTest.main = main;

		TestI8TSynthLoader.run(true,false);
		TestI8TMain.run(true,false);

		
		TestAudio.run(true,false);

		TestSynthPlayer.run(true,false);

	}

	test_onTestSetup_isCorrectClass {
		this.assert(main.isKindOf(I8TMain))
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
