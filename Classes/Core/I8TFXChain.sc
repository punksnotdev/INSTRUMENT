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
			if(this.at('channel').nextPatternKey.isNil) {
				this.at('channel').nextPatternKey=0;
			};
			^this.at('channel').clock=clock;
		};
	}

	put {|key,value|
		if( (
			// value.isKindOf(SynthDef)
			// ||
			// value.isKindOf(SynthDefVariant)
			// ||
			value.isKindOf(String)
			||
			value.isKindOf(Symbol)
		), {

			var variantName = key.asString ++ "." ++ value.asString;

			variantName = variantName.asSymbol;

			this.at('channel').addFx( variantName );

		}, {

			if( (
				value.isKindOf(SynthDef)
				||
				value.isKindOf(SynthDefVariant)
			), {
				["fxChain: got synth", key, value.class ]
			},{

				^super.put(key,value);

			});

		});
	}

}
