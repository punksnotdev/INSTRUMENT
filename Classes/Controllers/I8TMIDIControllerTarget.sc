MIDIControllerTarget : I8TNode
{

	var <> device;

	var <> type;
	var <> ctlNum;
	var <> channel;
	var <> sourceId;
	// var <> range;
	var <> protocol;

	var < callbacks;

	*new {|device_, name_, sourceId_, type_, ctlNum_, channel_ |
		^super.new.init(device_, name_, sourceId_, type_, ctlNum_, channel_ );
	}

	init {|device_, name_, sourceId_, type_, ctlNum_, channel_ |

		protocol = "midi";

		device = device_;
		sourceId = sourceId_;
		type = type_;
		ctlNum = ctlNum_;
		channel = channel_;

		name = name_;

		callbacks = ();

	}


	set {
		arg ...args;
		['set',name,args,callbacks].postln;
		callbacks.keysValuesDo({|k,callback|
			callback.value(this,args);
		});

	}


	addCallback{|key,callback|

		if( (key.isKindOf(Symbol) && callback.isKindOf(Function) ) ) {
			callbacks[key] = callback;
		}
	}



}
