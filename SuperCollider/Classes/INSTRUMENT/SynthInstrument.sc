SynthInstrument : Instrument
{

    var group;
	var groupID;


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


	outBus = 0;

    super.init(graph_,name_);

  }



  	outBus_ {|outBus_|
  		outBus = outBus_;
  	}
  	outBus {
  		^outBus;
  	}

  	group_ {|group_|
  		group = group_;
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

			fxSynth = Synth.new(synthdef_.asSymbol,[\inBus,fxBus]++this.parameters_array(fx_parameters));


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

            [
            "fxSynth.set(parameter,value);",
            fxSynth,
            parameter,value
            ].postln;

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
