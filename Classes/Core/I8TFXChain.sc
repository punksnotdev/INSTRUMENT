I8TFXChain : Event
{
	set {|parameter,value|
		this.collect({|fx|
			fx.set(parameter,value);
		})
	}

	seq_ {|pattern|
		if(this.at('channel').notNil){

			["seq",pattern].postln;

			this.at('channel').seq = pattern;

		};
	}
}
