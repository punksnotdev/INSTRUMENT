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


	// Play Parameters:

	speed {|n|
		if(n.isKindOf(Number)) {
			parameters[\speed]=n.ceil.asInteger;
		}
	}


	repeat {|n|
		if(n.isKindOf(Number)) {
			parameters[\repeat]=n.ceil.asInteger;
		}
	}


	// Pattern Transformation:

	reverse {
		pattern.pattern = pattern.pattern.reverse;
	}
	mirror {
		pattern.pattern = pattern.pattern.mirror;
	}
	pyramid {
		pattern.pattern = pattern.pattern.pyramid;
	}
	random {
		pattern.pattern = pattern.pattern.scramble;
	}
	maybe {|probability=0.5|
		pattern.pattern.collect({|patternEvent,index|
			[patternEvent,index].postln;
		});
	}

}
