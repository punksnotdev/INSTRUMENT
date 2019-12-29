TestInstrumentGroup : I8TUnitTest
{

	test_onCreation_onInvalidPairValue_returnsNil {


		main.newGroupName = (
			key0: 'some invalid value',
			key1: 'test',
		);


		this.assert(main.newGroupName.isNil);


	}

	test_onCreation_onValidSingleItemCollection_createsGroup {

		main.newGroupName = (
			key0: 'test',
		);


		this.assert(main.newGroupName.isKindOf(InstrumentGroup));


	}

	test_onCreation_onValidMultipleItemCollection_createsGroup {

		main.newGroupName = (
			key0: 'test',
			key1: 'test2',
		);


		this.assert(main.newGroupName.isKindOf(InstrumentGroup));


	}



	test_onCreation_onRepeatedKey_newPairValues_updatesGroup {

		var group1, group2;

		main.newGroupName = (
			key0: 'test',
			key1: 'test2',
		);

		group1 = main.newGroupName;

		main.newGroupName = (
			key0: 'test3',
			key1: 'test4',
		);

		group2 = main.newGroupName;

		this.assert(
			(
				group1.isKindOf(InstrumentGroup)
				&&
				group2.isKindOf(InstrumentGroup)
				&&
				group1 === group2
			)
		);


	}


	//
	// test_onCreation_onRepeatedKey_newPairKeys_createsGroup {
	//
	// 	var group1, group2;
	//
	// 	main.newGroupName = (
	// 		key0: 'test',
	// 		key1: 'test2',
	// 	);
	//
	// 	group1 = main.newGroupName;
	//
	// 	main.newGroupName = (
	// 		key2: 'test',
	// 		key3: 'test2',
	// 	);
	//
	// 	group2 = main.newGroupName;
	//
	// 	this.assert(
	// 		(
	// 			group1.newGroupName.isKindOf(InstrumentGroup)
	// 			&&
	// 			group2.newGroupName.isKindOf(InstrumentGroup)
	// 			&&
	// 			group1 !== group2
	// 		)
	// 	);
	//
	//
	// }


	// test_onCreation_onRepeatedKey_newPairs_createsGroup {
	//
	// 	var group1, group2;
	//
	// 	main.newGroupName = (
	// 		key0: 'test',
	// 		key1: 'test2',
	// 	);
	//
	// 	group1 = main.newGroupName;
	//
	// 	main.newGroupName = (
	// 		key2: 'test3',
	// 		key3: 'test4',
	// 	);
	//
	// 	group2 = main.newGroupName;
	//
	// 	this.assert(
	// 		(
	// 			group1.newGroupName.isKindOf(InstrumentGroup)
	// 			&&
	// 			group2.newGroupName.isKindOf(InstrumentGroup)
	// 			&&
	// 			group1 !== group2
	// 		)
	// 	);
	//
	//
	// }


	//
	// test_onCreation_onValidCollection_createsOneInstrumentPerValidPair {
	//
	//
	// 	main.newGroupName = (
	// 		key0: 'test',
	// 		key1: 'test2',
	// 	);
	//
	// 	this.assert(true);
	//
	//
	// }




	test_onNodeAdd_nodeNamesAreCorrect {

		var group = main.newGroupName = (
			key0: 'test',
			key1: 'test2',
		);
		
		this.assert(
			(
				(group.key0.name == 'newgroupname_key0')
				&&
				(group.key1.name == 'newgroupname_key1')
			)
		)


	}


}
