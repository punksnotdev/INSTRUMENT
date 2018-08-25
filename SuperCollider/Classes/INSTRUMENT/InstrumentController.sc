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
		if( t_.isKindOf(Instrument), {
			target = t_;
		}, {
			target = nil;
			"Target should be an instance of Instrument".postln;
		});
	}

	set {|parameter,value|

		parameters[parameter] = value;

		if( target.isKindOf(Instrument), {

			target.set(parameter,value);

		});

	}

}
