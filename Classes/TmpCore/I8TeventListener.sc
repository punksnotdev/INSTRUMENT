I8TeventListener
{

	executeEvent {|event|
		if( event.action.isKindOf(Function), {
			^event.action.value( event, this )
		})
	}

}
