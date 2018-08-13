TestB : TestA {
	// *new {|param|
	// 	^super.new;
	// }

	init {|param|
		v1 = param;
		("init B!! "++ v1).postln;
	}

}
