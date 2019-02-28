TestEvent : Event {

	var <tst;

	*new {
		^super.new.init();
	}

	init {

		"init test ev".postln;

	}

	> {|something1,something2|
		["wtf",something1,something2].postln;
	}

	** {|something1,something2|
		["wtf",something1,something2].postln;

	}


	put{|key,value|

		["put",key,value].postln;
		if(value.notNil, {
			// ^value;
			^super.put(key,value);
		}, {
			^this.at(key);
		});
	}

	// at{|key,value|
	//
	// 	["at",key,value].postln;
	//
	// }

}
