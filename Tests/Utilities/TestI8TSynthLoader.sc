TestI8TSynthLoader : I8TUnitTest
{
	var synthLoader;

	setUp {
		synthLoader = main.synthLoader;
	}

	tearDown {
		TestI8TFolder.run(true,false);
	}

	test_afterInit_loadedCorrectly {
		this.assert( synthLoader.isKindOf(I8TSynthLoader));
	}
	test_validateFolderName_onNotFound_returnsFalse {
		var folderName = "some invalid name";
		var validation = synthLoader.validateFolderName( folderName );
		this.assert( validation == false );
	}

	test_getSynthDefByName_onNotFound_returnsFalse {
		// var folderName = "some invalid name";
		// var validation = synthLoader.validateFolderName( folderName );
		this.assert( false );
	}

	test_getSynthDefByName_onValidName_returnsSynthDef {
		// var synthName = "some invalid name";
		var synthName = 'clap1';
		var synthdef = synthLoader.getSynthDefByName( synthName );
		this.assert( (synthdef.isKindOf(SynthDef)||synthdef.isKindOf(SynthDefVariant)) );
	}

	test_synthdefAddedToFamilyByParentName {
		[main.synths.kick.electro, main.synths.electro.kick].postln;

		this.assert(main.synths.kick.electro === main.synths.electro.kick)
	}

}
