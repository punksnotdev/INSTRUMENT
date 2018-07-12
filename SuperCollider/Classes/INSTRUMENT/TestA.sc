TestA {
	classvar <cv1;
	var <>v1;

	*new {|param|
		^super.new.init(param);
	}
	*initClass{
		cv1=18;
	}

	init {|param|
		v1 = param;
		("init A!! "++ v1).postln;
	}

}
