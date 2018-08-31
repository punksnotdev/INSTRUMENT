ControllerManager {

	var controllers;
	var <>targets;

	var controlTargetMap;

	*new {

		^super.new.init();
	}

	init {
		controllers = IdentityDictionary.new;
		targets = List.new;

		controlTargetMap = IdentityDictionary.new;
	}


	set {|source, value|

		var target = controllers[source.name].target;
		var key = controllers[source.name].key;

		target.set(
			key,
			value
		);

	}

	map {|controller,target|
		var newSource;
		switch( controller.protocol,
			"midi", {
				newSource = MIDIController();
				newSource.name = controller.name;
			}
		);



		newSource.addListener(this,controller.type);
		newSource.addResponder(controller.type);

		controllers[controller.name] = (
			target: target,
			key: controller.key
		);

		// controlTargetMap[ controlTargetMap.size ] = 

	}

	addTarget {|target|

		targets.add( target );

	}

}
