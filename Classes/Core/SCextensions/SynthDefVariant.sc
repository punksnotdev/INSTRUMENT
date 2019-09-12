SynthDefVariant : Event {
	var <parameters;
	var <synthdef;
	var <name;

	*new {|name_,parameters_,synthdef_|
		^super.new.init(name_,parameters_,synthdef_);
	}

	init {|name_,parameters_,synthdef_|
		name = name_;
		parameters = parameters_;
		synthdef = synthdef_;
	}
	
}
