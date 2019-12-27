TestInstrumentGroup : I8TUnitTest
{

	test_onCreation_onInvalidPairValue_returnsNil {


		main.newGroupName = (
			key0: 'some invalid value',
			key1: 'hihatElectro',
		);


		this.assert(main.newGroupName.isNil);


	}

	test_onCreation_onValidSingleItemCollection_createsGroup {

		main.newGroupName = (
			key0: 'hihatElectro',
		);


		this.assert(main.newGroupName.isKindOf(InstrumentGroup));


	}

	test_onCreation_onValidMultipleItemCollection_createsGroup {

		main.newGroupName = (
			// key0: 'hihatElectro',
			key1: 'clap1',
			key2: 'kickDamp',
		);


		this.assert(main.newGroupName.isKindOf(InstrumentGroup));


	}
	//
	// test_onCreation_onValidCollection_createsOneInstrumentPerValidPair {
	//
	//
	// 	main.newGroupName = (
	// 		key0: 'kickElectro',
	// 		hihat: 'hihatElectro',
	// 	);
	//
	// 	this.assert(true);
	//
	//
	// }

}
