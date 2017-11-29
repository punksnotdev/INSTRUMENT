Tests {

	var <>tests;

	*new {
		^super.new.init();
	}

	init {
		tests = Dictionary.new;
	}

	run {


		4.do{"".postln;};
		"Run Tests".postln;
		"-------------------------".postln;
		2.do{"".postln;};

		tests.collect({|test,key|
			( key ++ " -> " ++ test.value ).postln;
		});

		2.do{"".postln;};
		"Tests done".postln;
		"-------------------------".postln;
		4.do{"".postln;};

	}

}
