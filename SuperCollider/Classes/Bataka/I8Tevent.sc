I8Tevent
{

	var <>name;
	var <>listener;
	var <>parameters;

	*new {|name_,listener_|
		^super.new.init(name_,listener_);
	}

	init{|name_,listener_|
		name = name_;
		listener = listener_;
		parameters = Dictionary.new;
	}

	execute {
		^listener.execute( this );
	}

}
