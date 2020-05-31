TestINSTRUMENT : UnitTest
{

	var main;

	setUp {

		var options = ServerOptions.new.maxLogins_(6);
		var server = Server(this.class.name, nil, options, 3);


		server.bootSync;

		TestINSTRUMENT.reportPasses = false;

		main = INSTRUMENT(server);

		main.mode = "test"

	}

	tearDown {

		I8TUnitTest.main = main;

		TestI8TFolder.run(true,false);

		TestI8TSynthLoader.run(true,false);

		TestI8TMain.run(true,false);

		TestI8TChannel.run(true,false);

		TestI8TMixer.run(true,false);


		TestSynthPlayer.run(true,false);

		TestInstrumentGroup.run(true,false);

		TestInstrumentGroupManagement.run(true,false);


		TestI8TControllerSpec.run(true,false);

		// TestAudio.run(true,false);

		main.kill();

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
