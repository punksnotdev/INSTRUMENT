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


	set {|type,key_,value_|

		var key = key_;
		var value = value_;

		if( type == \note ) {
			key = key_.val;
			value = key_.amplitude;
		};

		callbacks.keysValuesDo({|k,callback|
			callback.value(this,type,key,value);
		});

	}


	addCallback{|key,callback|

		if( (key.isKindOf(Symbol) && callback.isKindOf(Function) ) ) {
			callbacks[key] = callback;
		}
	}



}
