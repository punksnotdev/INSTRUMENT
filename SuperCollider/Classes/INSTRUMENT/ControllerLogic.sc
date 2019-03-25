ControllerLogic : I8TNode
{

	// State for storing values and callbacks
	var state;
	var inputs;
	var outputs;

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


	addInput {|input|

		inputs.add(input)

	}
	addOutput {|input|

		inputs.add(input)

	}


}
