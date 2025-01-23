I8TInstrument : Sequenceable
{


    var group;
	var groupID;

	var outbus;

	var <>synth;
	var <>synths;
	var <>volume;
	var <>octave;
	var <main;
    var <>channel;
	// var synthGroup;
	var outGroup;


	*new{|name_,main_|
		^super.new.init(name_,main_);
	}


	init{|main_,name_|

		volume = 1;
		octave = 4;
		synths = List.new;
		main = main_;
		super.init(main_,name_);


	}

	trigger {

	}

	set {|parameter,value|

	}


	createParametersArray {|array|
		var parameters_array = List.new;

		array.keysValuesDo({|key,value|
			parameters_array.add(key.asSymbol);
			parameters_array.add(value);
		})

		^parameters_array
	}


	group_ {|group_| }
  	group {
  		^group;
  	}


  	outbus_ {|outbus_|
  		outbus = outbus_;
  	}
  	outbus {
  		^outbus;
  	}


    send {|targetChannel,connect=true|
        ^channel.send(targetChannel,connect);
    }

    connect {|targetChannel|
        ^channel.connect(targetChannel);
    }

    disconnect {|targetChannel|
        ^channel.disconnect(targetChannel);
    }



}
