TestI8TMain : I8TUnitTest
{

	test_afterInit_hasServer {
		this.assert(main.server.isKindOf(Server));
	}

	test_afterInit_isBooted {
		this.assert(main.isBooted==true);
	}

}
