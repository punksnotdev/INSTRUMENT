SynthInstrument : Instrument
{

    var group;
	var groupID;


  var fx_parameters;

  var <fxSynth;
  var fx;
  var fxBus;


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

    super.init(graph_,name_);

  }


	fx {|pattern|

		if(pattern.notNil, {
			^this.seq(\fx,pattern);
		});

		^this.fx;

	}


	fx_ {|synthdef_|

		var fx;


		if( synthdef_.notNil,{
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

		^fxSynth;

	}



	fxSynth_ {|synthdef_|
		this.fx(synthdef_);
	}

	fxSet {|parameter,value|
		if( value.notNil, {
			fx_parameters[parameter] = value;
			fxSynth.set(parameter,value);
		}, {
			var pattern = parameter;
			this.seq(\fxSet,pattern);
		});
	}

}
