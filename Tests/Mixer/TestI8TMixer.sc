TestI8TMixer : I8TUnitTest
{
	test_afterInit_hasMasterLR {
		var mixer = main.mixer;
		this.assert(
			(
				(mixer.master.size==2)
				&&
				(mixer.master[0].isKindOf(I8TChannel))
				&&
				(mixer.master[1].isKindOf(I8TChannel))
			)
		)
	}


	// test_addChannel_onGroupUpdate_destroysUnusedChannels {
	// 	this.assert(false);
	// }
	// test_addChannel_onGroupUpdate_createsOnlyNewChannels {
	// 	this.assert(false);
	// }
}
