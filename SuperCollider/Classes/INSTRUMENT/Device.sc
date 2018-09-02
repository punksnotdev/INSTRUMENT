Device {

	var <>controllerGroups;

	*new {
		^super.new.init();
	}

	init {
		controllerGroups = IdentityDictionary.new;
	}

}
