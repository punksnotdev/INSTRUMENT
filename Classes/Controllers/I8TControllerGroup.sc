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

	addController{|ctlNum_=nil,channel_=nil|
		var newController;
		var key = '';
		key = device.slug;

		if( parent.notNil, {
			if( parent.parent.notNil, {
				key = key ++'_'++ parent.parent.name;
			});

			key = key ++'_'++ parent.name;
		});
		// group name:
		key = key ++'_'++name.asString.toLower++'_'++controllers.size;

		// controller name:
		key = key ++'_'++controllers.size.asString++'_'++controllers.size;

		// convert to symbol
		key = key.asSymbol;

		newController = MIDIController(
			device,
			type,
			ctlNum_,
			channel_,
			device.id,
			name_
		);

		newController.key = key;
		newController.protocol = device.protocol;

		device.controllers[name_] = newController;
		controllers[name_] = newController;

		^controllers[name_]

	}


}
