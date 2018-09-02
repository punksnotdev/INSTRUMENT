ControllerGroup {

	var <>controllers;
	var <>type;
	var <>name;

	*new {|type_, name_|
		^super.new.init(type_,name_);
	}

	init {|type_, name_|

		type = type_;
		name = name_;

		controllers = IdentityDictionary.new;

	}

}
