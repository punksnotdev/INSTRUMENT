+ SynthDef {
	at {|i|
		^variants !? { (name++"."++variants.keys.asArray[i%variants.keys.size]).asString};
	}
}
