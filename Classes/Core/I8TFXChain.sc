I8TFXChain : Event
{
	set{|parameter,value|
		this.collect({|fx|
			fx.set(parameter,value);
		})
	}
}
