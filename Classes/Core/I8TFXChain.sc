I8TFXChain : Event
{

	var <seq;

	set {|parameter,value|
		this.collect({|fx|
			fx.set(parameter,value);
		})
	}

	seq_ {|pattern|
		if(this.at('channel').notNil){
			seq=this.at('channel').seq(\fx, pattern);
			^seq;
		};

	}
	clock_ {|clock|
		if(this.at('channel').notNil){
			^this.at('channel').clock=clock;
		};
	}

}
