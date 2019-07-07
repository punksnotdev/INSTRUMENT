INSTRUMENT {

    *new {|source=nil,createNew=false|
        ^super.new.init(source,createNew);
    }

    init {|source=nil,createNew=false|

        if( (source.isNil || source.isKindOf(Boolean)) ) {
			^I8TMain(createNew);
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
