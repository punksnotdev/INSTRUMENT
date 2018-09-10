ControllerGroup {

	var <>device;

	var <>groups;
	var <>controllers;
	var <>type;
	var <>name;
	var <>parent;

	*new {|type_, name_, device_,parent_|
		^super.new.init(type_,name_, device_, parent_);
	}

	init {|type_, name_, device_, parent_|

		type = type_;
		name = name_;
		device = device_;
		parent = parent_;

		groups = ();
		controllers = ();

	}

	addControllerGroup {|type, name|
		^groups[name.asSymbol] = ControllerGroup(type,name,device,this);
	}

	addController{|name,id,channel|
		var newController;
		var key = '';

		key = device.slug;

		if( parent.notNil, {
			key = key ++'_'++ parent.name;
		});

		key = key ++'_'++name++'_'++controllers.size;
		key = key.asSymbol;

		newController = MIDIController(
			device,
			type,
			id,
			channel,
		);

		newController.name = name;
		newController.key = key;
		newController.protocol = device.protocol;

		device.controllers[name] = newController;
		controllers[name] = newController;

		^controllers[name]

	}


}
