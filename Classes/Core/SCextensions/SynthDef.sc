+ SynthDef {
	at {|key|
		^variants !? {
			if(key.isNumber) {
				(name++"."++variants.keys.asArray[key%variants.keys.size]).asString
			};
			if(key.isKindOf(Symbol)) {
				(name++"."++variants.keys[key%variants.keys.size]).asString
			};
		}

	}
}
