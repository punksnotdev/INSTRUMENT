I8TFXChain : Event
{
	set {|parameter,value|
		this.collect({|fx|
			fx.set(parameter,value);
		})
	}

	seq_ {|pattern|
		if(this.at('channel').notNil){
			this.at('channel').seq(\fx, pattern);
		};
	}
	clock_ {|clock|
		if(this.at('channel').notNil){
			this.at('channel').clock=clock;
		};
	}

}
