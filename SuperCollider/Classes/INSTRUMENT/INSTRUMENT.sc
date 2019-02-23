INSTRUMENT {

    *new {|source,autostart=false|
        ^super.new.init(source,autostart);
    }

    init {|source,autostart=false|
        if( source.isNil) {
			"I N S T R U M E N T".postln;
            ^I8TMain();
        };

        if( (source.isKindOf(Symbol) ), {
            ^SynthPlayer(source,autostart);
        }, {
            if( source.isKindOf(SynthDef) ) {
                ^SynthPlayer(source.name.asSymbol,autostart);
            };
        });

        if( source.isKindOf(NodeProxy) ) {
            ^Proxy(source);
        }

    }

}
