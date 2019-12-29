TestInstrumentGroupManagement : I8TUnitTest
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


	test_validateGroupItems_onValidInput_returnsTrue {
		var instruments = (
			key0: 'test',
			key1: 'test',
		);
		this.assert( main.validateGroupItems(instruments) == true );
	}

	test_validateGroupItems_onInvalidInput_returnsTrue {
		var instruments = (
			key0: 'test',
			key1: 'some invalid input',
		);
		this.assert( main.validateGroupItems(instruments) == false );
	}



	test_updateGroup_onValidInput_updatesOriginal{

		var group1, group2;
		var instruments1, instruments2;

		var hasAllKeys = false;


		// first, create valid group
		instruments1 = (
			key0: 'test',
			key1: 'test2',
		);
		main.newGroupName = instruments1;
		group1 = main.newGroupName;

		// now, attempt valid group update
		instruments2 = (
			key2: 'test3',
			key3: 'test4',
		);

		main.newGroupName = instruments2;
		group2 = main.newGroupName;


		hasAllKeys = (
			group1.keys.difference(instruments1.keys++instruments2.keys).size == 0
		);


		this.assert(
			(
				((group1.isKindOf(InstrumentGroup))
				&&
				(group1 === group2))
				&&
				hasAllKeys
			)
		);

	}


	test_updateGroup_onInvalidInput_returnsOriginal{

		var group1, group2;
		var instruments;
		var isOriginal;

		// first, create valid group
		instruments = (
			key0: 'test',
			key1: 'test2',
		);
		main.newGroupName = instruments;
		group1 = main.newGroupName;

		// now, attempt invalid group creation
		instruments = (
			key0: 'test',
			key1: 'some invalid input',
		);
		main.newGroupName = instruments;
		group2 = main.newGroupName;

		isOriginal = (group1.keys.difference(group2.keys).size==0);

		this.assert(
			((group1.isKindOf(InstrumentGroup))
			&&
			(group1 === group2))
			&&
			isOriginal
		);

	}



	test_createGroup_onEmptyCollection_returnsEmptyGroup {

		var instruments = ();

		var group = main.newGroupName = instruments;

		this.assert((
			(
				group.isKindOf(InstrumentGroup)
				&&
				group.keys.size==0
			)
		));

	}


	test_createGroupChildNode_onNodeInput_returnsNode {

		var instruments = (
			key0: 'test'
		);

	}


}
