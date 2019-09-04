SynthInstrument : Instrument
{



  var fx_parameters;

  var <fxSynth;
  var fx;
  var fxBus;

  var autostart;

  *new{|name_|
    ^super.new.init(name_,this.graph);
  }

  init{|graph_,name_|

      group = Group.new;
      group.register;
      groupID = group.nodeID;

    fx_parameters = IdentityDictionary.new;
    fxSynth = nil;
    fxBus = Bus.audio(Server.local,2);


	outbus = 0;

    super.init(graph_,name_);

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

	fx {|pattern|

        if(pattern.notNil, {
        	^this.seq(\fx,pattern);
		});

		^fx;

	}


	fx_ {|synthdef_|
        ["fx_ got:",synthdef_].postln;

        if( (synthdef_.isKindOf(SynthDef) || (synthdef_.isKindOf(Symbol)&&(synthdef_ != "nil".asSymbol ))  ), {

            var synthdefName;

            if( synthdef_.isKindOf(SynthDef) ) {
                synthdefName = synthdef_.name;
            };

            if( synthdef_.isKindOf(Symbol) ) {
                synthdefName = synthdef_;
            };

            // if( fxSynth.notNil, {
            //     fxSynth.free;
            //     fxSynth = Synth.replace(fxSynth,synthdefName);
            // }, {
            if( fxSynth.notNil ) {
                fxSynth.free;
                fxSynth=nil;
            };

            if( group.isKindOf(Group), {
                fxSynth = Synth.head(group,synthdefName,this.parameters_array(fx_parameters)++[\inBus,fxBus,\outBus,outbus]);
            }, {
                 fxSynth = Synth.new(synthdefName,this.parameters_array(fx_parameters)++[\inBus,fxBus]);
            });
            // });

		}, {
			"clear currentFX".postln;
			fxSynth.free;
			fxSynth = nil;
		});


        if( autostart == true ) {
            this.start();
        }

		// ^fxSynth;

	}


    restart {

    }

	fxSet {|parameter,value|

        if( fxSynth.notNil, {
            if( value.notNil, {

            	fx_parameters[parameter] = value;
    			fxSynth.set(parameter,value);

            }, {
                // if no value, check if is valid sequence:
                if( this.checkIsValidSequence(parameter) ) {

        			var pattern = parameter;
        			this.seq(\fxSet,pattern);
                };

    		});
		});
	}

    checkIsValidSequence {|sequence|
        ^ (sequence.isKindOf(Collection) || sequence.isKindOf(String))
    }

}
