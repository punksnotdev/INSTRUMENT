INSTRUMENT {
    classvar <>instance;

    *new {|source=nil,createNew=false|
        instance = super.new.init(source,createNew);
        ^instance;
    }

    init {|source=nil,createNew=false|

        if( (source.isNil || source.isKindOf(Boolean)) ) {
			^I8TMain(createNew);
        };

        if(
            (
                source.isKindOf(String)
                ||
                source.isKindOf(Symbol)
                ||
                source.isKindOf(SynthDef)
                ||
                source.isKindOf(NodeProxy
            )
        )==false, {

			"INSTRUMENT: Not a valid SynthDef".warn;

        }, {

            if(
                source.isKindOf(String)
                ||
                source.isKindOf(Symbol)
                ||
                source.isKindOf(SynthDef)
            ) {
                ^SynthPlayer(source);
            };

            if( source.isKindOf(NodeProxy) ) {
                ^Proxy(source);
            };

        });


    }

}
