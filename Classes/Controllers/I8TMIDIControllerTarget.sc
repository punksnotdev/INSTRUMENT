MIDIControllerTarget : I8TNode
{

	var <> device;

	var <> name;
	var <> type;
	var <> ctlNum;
	var <> channel;
	var <> sourceId;
	// var <> range;
	var <> protocol;

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

	}

	set {
		arg ...args;
		device.send(name,args);
	}




}
