PatternEvent : SequencerEvent
{

	var <>pattern;

	var <> initialWait;

	var <> time;

	var <> played;

	*new{|pattern_,parameters_,name_|
		^super.new.init( pattern_, parameters_, name_ );
	}
	init{|pattern_,parameters_,name_|
		pattern = pattern_;
		played = false;
		^super.init( pattern_, parameters_, name_ );
	}


	// Play Parameters:

	speed {|n|
		if(n.isKindOf(Number)) {
			parameters[\speed]= max(n.asFloat,0.01);

		};
	}


	repeat {|n|
		if(n.isKindOf(Number)) {
			parameters[\repeat]=n.ceil.asInteger;
		};
	}


	// Pattern Transformation:

	reverse {
		pattern.pattern = pattern.pattern.reverse;
	}
	mirror {
		pattern.pattern = pattern.pattern.mirror;
	}
	mirror1 {
		pattern.pattern = pattern.pattern.mirror1;
	}
	mirror2 {
		pattern.pattern = pattern.pattern.mirror2;
	}
	pyramid {
		pattern.pattern = pattern.pattern.pyramid;
	}
	random {
		pattern.pattern = pattern.pattern.scramble;
	}
	maybe {|probability=0.5|
		pattern.pattern.collect({
			arg patternEvent,index;

			if( 1.0.rand > probability ) {
				patternEvent.val = \r;
			};

		});
	}

	rotate {|n=0|
		pattern.pattern = pattern.pattern.rotate(n);
	}

	shift {|n=0|
		pattern.pattern = pattern.pattern.shift(n);
	}

	lace {|length=0|
		pattern.pattern = pattern.pattern.lace(length);
	}

	permute {|n=0|
		pattern.pattern = pattern.pattern.permute(n);
	}

}
