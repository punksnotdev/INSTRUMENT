PatternEvent : SequencerEvent
{

	var <>pattern;

	*new{|pattern_,parameters_,name_|
		^super.new.init( pattern_, parameters_, name_ );
	}

}
