INSTRUMENT {

    *new {|source|
        ^super.new.init(source);
    }

    init {|source|
        if( source.isNil) {
			"I N S T R U M E N T".postln;
            ^I8TMain();
        };

        if( (source.isKindOf(Symbol) ), {
            ^SynthPlayer(source);
        }, {
            if( source.isKindOf(SynthDef) ) {
                ^SynthPlayer(source.name.asSymbol);
            };
        });

        if( source.isKindOf(NodeProxy) ) {
            ^Proxy(source);
        }

    }

}
