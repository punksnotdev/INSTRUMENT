TestEvent : Event {

	var <tst;

	*new {
		^super.new.init();
	}

	init {

		"init test ev".postln;

		this.abc = 123;
		tst = 456;
	}


	put{|key,value|

		["put",key,value].postln;

	}
}
