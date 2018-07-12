I8Tpattern
{

	var <>parameters;
	var <>pattern;

	*new{|pattern_,parameters_|
		^super.new.init(pattern_,parameters_);
	}
	init{|pattern_,parameters_|
		pattern = pattern_;
		parameters = parameters_;
	}
}
