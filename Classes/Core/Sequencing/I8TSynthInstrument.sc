I8TSynthInstrument : I8TInstrument
{



    var fx_parameters;

    var synthdef;

    var <fxSynth;
    var fx;
    var fxBus;

    var autostart;


    *new{|name_,main_|
        ^super.new.init(name_,main_);
    }

    init{|main_,name_|

        group = Group.new( main_.parGroup );
        group.register;
        groupID = group.nodeID;

        fx_parameters = IdentityDictionary.new;
        fxSynth = nil;
        fxBus = Bus.audio(main_.server,2);

        super.init(main_,name_);

    }




    group_ {|group_|
        group = group_;
        // "To do: check if should register??".warn;
        group.register;
        groupID = group.nodeID;
    }
    group {
        ^group;
    }



    autostart {
        autostart=true;
    }
    autostart_ {|value|
        autostart=value;
    }



    // fx_ {|synthdef_|
    //
    //     if(
    //         (
    //             synthdef_.isKindOf(SynthDef)
    //             ||
    //             (synthdef_.isKindOf(String) || synthdef_.isKindOf(Symbol))
    //         )
    //         &&
    //         (synthdef_ != "nil".asSymbol )
    //     , {
    //
    //         var synthdefName;
    //
    //         if( synthdef_.isKindOf(SynthDef) ) {
    //             synthdefName = synthdef_.name;
    //         };
    //
    //         if( synthdef_.isKindOf(Symbol)||synthdef_.isKindOf(String) ) {
    //             synthdefName = synthdef_;
    //         };
    //
    //         // if( fxSynth.notNil, {
    //         //     fxSynth.free;
    //         //     fxSynth = Synth.replace(fxSynth,synthdefName);
    //         // }, {
    //         if( fxSynth.notNil ) {
    //             fxSynth.free;
    //             fxSynth=nil;
    //         };
    //
    //         if( group.isKindOf(AbstractGroup), {
                // fxSynth = Synth.new(synthdefName,this.createParametersArray(fx_parameters)++[\inBus,fxBus,\outBus,outbus], group, \addToHead);
    //         }, {
    //              fxSynth = Synth.new(synthdefName,this.createParametersArray(fx_parameters)++[\inBus,fxBus]);
    //         });
    //         // });
    //
    // 	}, {
    // 		// "clear currentFX".postln;
    // 		fxSynth.free;
    // 		fxSynth = nil;
    // 	});
    //
    //
    //     if( autostart == true ) {
    //         this.start();
    //     }
    //
    // 	// ^fxSynth;
    //
    // }


    restart {

    }

    fxSet {|parameter,value|

        if( value.notNil, {

            fx_parameters[parameter] = value;

            if( fxSynth.notNil ) {
                fxSynth.set(parameter,value);
            };

        };, {
            // if no value, check if is valid sequence:
            if( this.checkIsValidSequence(parameter) ) {

                var pattern = parameter;
                this.seq(\fxSet,pattern);
            };

        });

    }

    checkIsValidSequence {|sequence|
        ^ (sequence.isKindOf(Collection) || sequence.isKindOf(String))
    }


    getSynthDefVariants {

        var variantsList = List.new;
        var variantsEvent = ();

        synthdef.variants.collect({|v,k|
            var newEvent = v.asEvent;
            newEvent.key=k;
            variantsEvent[k]=newEvent;
            variantsEvent[variantsList.size]=newEvent;
            variantsList.add(newEvent);
        });

        ^variantsEvent

    }

    getVariantKey {|key|

        var variant = this.getSynthDefVariants()[key];

        if( variant.isNil, {
            ^synthdef.name;
        }, {
            ^synthdef.name++"."++variant.key;
        });

    }

    setContent {|synthinstrument_|

        if(synthinstrument_.isKindOf(I8TSynthPlayer), {
            synthdef = synthinstrument_.synthdef;
        },{
            "setContent: Not a valid I8TSynthPlayer".warn;
        });

    }

    set {|parameter, value|

        switch(parameter.asSymbol,
            \low, {
                channel.set(\low,value);
            },
            \middle, {
                channel.set(\middle,value);
            },
            \high, {
                channel.set(\high,value);
            },
        )
    }

    fx {|fx|

        if( fx.notNil ) {

            if( graph.validateFx( fx ) ) {
                channel.setFxChain(fx);
            };

            ^channel.fx(fx)

        };

        ^channel.fxChain;
    }

    fx_ {|fx|

        if( fx.isNil, {

            channel.rm('fx');
            channel.setFxChain(nil);

        },{

            if( graph.validateFx( fx ) ) {
                channel.rm('fx');
                this.fx(fx);
            };

        });

    }



}
