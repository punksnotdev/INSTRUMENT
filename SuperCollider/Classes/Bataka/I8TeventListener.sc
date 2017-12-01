I8TeventListener
{

	var <>eventAction;

	// *new {
	// 	^super.new.init;
	// }

	execute {|event|
		if( eventAction.isKindOf(Function), {
			^eventAction.value( event )
		})
	}

}
