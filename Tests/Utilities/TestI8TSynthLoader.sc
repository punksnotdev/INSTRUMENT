TestI8TSynthLoader : I8TUnitTest
{
	var synthLoader;

	setUp {
		synthLoader = main.synthLoader;
	}
	test_afterInit_loadedCorrectly {
		this.assert( synthLoader.isKindOf(I8TSynthLoader));
	}
	test_validateFolderName_onNotFound_returnsFalse {
		var folderName = "some invalid name";
		var validation = synthLoader.validateFolderName( folderName );
		this.assert( validation == false );
	}
}
