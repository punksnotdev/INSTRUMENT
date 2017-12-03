I8Tpattern
{

	var <>target;
	var <>parameters;
	var <>pattern;

	*new{|target_,pattern_,parameters_|
		^super.new.init(target_,pattern_,parameters_);
	}
	init{|target_,pattern_,parameters_|
		target = target_;
		parameters = parameters_;
		pattern = pattern_;
	}
}
