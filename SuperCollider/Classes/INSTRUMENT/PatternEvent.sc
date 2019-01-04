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

	reverse {
		pattern.pattern = pattern.pattern.reverse.postln;
	}
	mirror {
		pattern.pattern = pattern.pattern.mirror.postln;
	}
	pyramid {
		pattern.pattern = pattern.pattern.pyramid.postln;
	}
	random {
		pattern.pattern = pattern.pattern.scramble.postln;
	}


	speed {|n|
		if(n.isKindOf(Number)) {
			parameters[\speed]=n.ceil.asInteger;
		}
	}
	// 'speed' alias:
	x {|n|
		this.speed(n);
	}

	repeat {|n|
		if(n.isKindOf(Number)) {
			parameters[\repeat]=n.ceil.asInteger;
		}
	}
	// 'repeat' alias:
	do {|n|
		this.repeat(n);
	}

	one {|n|
		this.repeat(1);
	}

}
