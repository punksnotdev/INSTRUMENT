ControllerLogic : I8TNode
{

	// State for storing values and callbacks
	var state;

	*new {
		^super.new.init();
	}

	init {

		state = ();

		state.values = Array.fill(72,nil);
		state.callbacks = Array.fill(72,nil);

	}


	updateViews {

	}





}
