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

			if( test.value, {
				"[   OK!   ] <- ".post;
			}, {
				"[ FAILURE ] <- ".post;
			});

			key.postln;

		});

		2.do{"".postln;};
		"Tests done".postln;
		"-------------------------".postln;
		4.do{"".postln;};

	}

}
