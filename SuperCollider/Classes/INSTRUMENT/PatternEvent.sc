PatternEvent : SequencerEvent
{

	var <>pattern;
	var <> initialWait;

	*new{|pattern_,parameters_,name_|
		^super.new.init( pattern_, parameters_, name_ );
	}
	init{|pattern_,parameters_,name_|
		pattern = pattern_;
		^super.init( pattern_, parameters_, name_ );
	}

}
