InstrumentController {

	var < target;

	var <> parameters;

	*new {|target_|
		^super.new.init(target_);
	}

	init {|target_|
		parameters = IdentityDictionary.new;
		this.target_(target_);
	}

	target_ {|t_|
		if( t_.isKindOf(I8TInstrument), {
			target = t_;
		}, {
			target = nil;
		});
	}

	set {|parameter,value|

		parameters[parameter] = value;

		if( target.notNil, {

			target.set(parameter,value);

		}, {

			"Target not set".postln;

		});

	}

}
