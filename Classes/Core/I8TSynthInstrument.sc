SynthInstrument : Instrument
{

    var group;
	var groupID;

    var outbus;

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



  	outbus_ {|outbus_|
  		outbus = outbus_;
  	}
  	outbus {
  		^outbus;
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


        if( ((synthdef_.notNil) && (synthdef_ != "nil".asSymbol )), {
			if( fxSynth.notNil, {
				fxSynth.free;
				// fxSynth = Synth.replace(fxSynth,synthdef_);
			}, {
				// fxSynth = Synth.new(synthdef_);
			});

            if( group.isKindOf(Group), {
                fxSynth = Synth.head(group,synthdef_.asSymbol,this.parameters_array(fx_parameters)++[\inBus,fxBus,\outBus,outbus]);
            }, {
	             fxSynth = Synth.new(synthdef_.asSymbol,this.parameters_array(fx_parameters)++[\inBus,fxBus]);
            });


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
	}

    checkIsValidSequence {|sequence|
        ^ (sequence.isKindOf(Collection) || sequence.isKindOf(String))
    }

}
