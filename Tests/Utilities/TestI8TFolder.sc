TestI8TFolder : I8TUnitTest
{
	test_makeChildKeyWithoutParent_splitsChild {

		var f = I8TFolder.new;

		f.name = "parentName";

		f.put( 'parentNameChild0', main.synths.test);
		f.put( 'parentNameChild1', main.synths.test);
		f.put( 'parentNameChild2', main.synths.test);

		this.assert( f.makeChildKeyWithoutParent( 'parentNameChild0' ) ==  'child0' );

	}
	
}
