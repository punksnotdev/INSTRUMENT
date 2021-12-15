TestInstrumentGroup : I8TUnitTest
{


	tearDown {
		main.groups.keys.asArray.do({|k|

			var g = main.groups.at(k);

			g.collect({|child|
				child.kill;
				main.nodes.removeAt(child.name);
			});

			main.groups.removeAt(k)
		});
	}


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



	test_onCreation_onRepeatedKey_newPairKeys_updatesGroup {

		var group1, group2;
		var instrument1, instrument2;

		instrument1 = (
			key0: 'test',
			key1: 'test2',
		);

		instrument2 = (
			key2: 'test',
			key3: 'test2',
		);

		main.newGroupName = instrument1;

		group1 = main.newGroupName;

		main.newGroupName = instrument2;

		group2 = main.newGroupName;

		this.assert(
			(
				(group1.keys==(instrument1.keys++instrument2.keys))
				&&
				(group1.childrenStopped.keys==instrument1.keys)
				&&
				group1 === group2
			)
		);


	}




	test_onCreation_onRepeatedKey_newPairs_updatesGroup {

		var group1, group2;
		var instrument1, instrument2;

		instrument1 = (
			key0: 'test',
			key1: 'test2',
		);

		instrument2 = (
			key2: 'test3',
			key3: 'test4',
		);

		main.newGroupName = instrument1;

		group1 = main.newGroupName;

		main.newGroupName = instrument2;

		group2 = main.newGroupName;


		this.assert(
			(
				(group1.keys==(instrument1.keys++instrument2.keys))
				&&
				(group1.childrenStopped.keys==instrument1.keys)
				&&
				group1 === group2
			)
		);


	}




	test_onCreation_onValidCollection_createsOneInstrumentPerValidPair {


		main.newGroupName = (
			key0: 'test',
			key1: 'test2',
			key2: 'test2',
			key3: 'test2',
		);

		this.assert((
			main.newGroupName.key0.isKindOf(I8TSynthPlayer)
			&&
			main.newGroupName.key1.isKindOf(I8TSynthPlayer)
			&&
			main.newGroupName.key2.isKindOf(I8TSynthPlayer)
			&&
			main.newGroupName.key3.isKindOf(I8TSynthPlayer)
			&&
			main.newGroupName.keys.size==4
		));


	}




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
