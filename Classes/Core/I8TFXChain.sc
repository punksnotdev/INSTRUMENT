I8TFXChain : IdentityDictionary
{
	set{|parameter,value|
		this.collect({|fx|
			fx.set(parameter,value);
		})
	}
}
