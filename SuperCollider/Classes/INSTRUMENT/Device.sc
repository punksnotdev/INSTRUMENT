Device {

	var controllers;
	var outputs;
	var <>controllerGroups;

	var key;
	var device;
	var name;
	var id;

	*new {|device|
		^super.new.init();
	}

	init {|device|

		// key = key;
		device = device.device;
		name = device.name;
		id = device.uid;

		controllers = IdentityDictionary.new;
		controllerGroups = IdentityDictionary.new;

		outputs = IdentityDictionary.new;

	}

	send{|key,value|

		outputs[key].send(value);

	}

	set {|source, value|

		var min, max;
		var outRange, minOutVal;
		var normalizedValue;
		var outValue;

		var target = controllers[source.name].target;
		var key = controllers[source.name].key;
		var range = controllers[source.name].range;
		var protocol = controllers[source.name].protocol;

		switch( protocol,
			"midi", {

				min = 0; max = 127;

				normalizedValue = (value / 127).asFloat;

				outRange = (range[1] - range[0]).abs;

				minOutVal = range[0];

				outValue = minOutVal + outRange*normalizedValue;

			}
		);

		["set:",target,key,outValue].postln;

		target.set(
			key,
			outValue
		);

	}


}
