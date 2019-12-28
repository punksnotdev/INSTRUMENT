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


	// test_onPut_createsCorrectItemDependingOnInput {
	// 	this.assert(false);
	// }
	//
	// test_onPut_onValidName_createsCorrectItem {
	// 	this.assert(false);
	// }

}
