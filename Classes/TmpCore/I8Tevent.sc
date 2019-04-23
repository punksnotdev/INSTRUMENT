I8Tevent
{

	var <>name;
	var <>listener;
	var <>parameters;
	var <>action;


	*new {|listener_,callback_,name_|
		^super.new.init(listener_,callback_,name_);
	}

	init{|listener_,callback_,name_|
		listener = listener_;
		action = callback_;
		name = name_;
		parameters = Dictionary.new;
	}

	execute {
		if( listener.isKindOf(I8TeventListener), {
			^listener.executeEvent( this );
		});
	}

}
