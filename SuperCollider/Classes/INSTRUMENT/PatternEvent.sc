PatternEvent : SequencerEvent
{

	var <>pattern;

	*new{|pattern_,parameters_,name_|
		^super.new.init( pattern_, parameters_, name_ );
	}
	init{|pattern_,parameters_,name_|
		pattern = pattern_;
		">>>>>>>>>>>>>>>>>>>>>>".postln;
		"added pattern".postln;
		^super.init( pattern_, parameters_, name_ );

	}

}
