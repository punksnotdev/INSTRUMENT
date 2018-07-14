SynthPlayer : Instrument
{

	var <synthdef;

	var synth_parameters;
	var fx_parameters;

	var <currentFx;

	*new{|synthdef_|
		^super.new.init(synthdef_,this.graph);
	}

	init{|synthdef_,graph_|

		if(synthdef_.isKindOf(Symbol), {
			synthdef = synthdef_;
		},{
			synthdef = \test;
		});

		this.createSynth();
		currentFx = nil;

		synth_parameters = IdentityDictionary.new;
		fx_parameters = IdentityDictionary.new;
		super.init(synthdef_,graph_);

	}

	synthdef_{|synthdef_|

		synthdef = synthdef_;
		synth_parameters=IdentityDictionary.new;

		this.createSynth();

		^synthdef

	}

	createSynth{|parameters|

		if(synth.notNil, {
			synth.free;
		}, {});

		if( currentFx.isKindOf(Synth), {

			// [currentFx,"synthfx"].postln;
			synth = Synth.before( currentFx, synthdef.asSymbol, parameters );
		}, {
			synth = Synth( synthdef.asSymbol, parameters );
		});

	}

	parameters_array{|array|
		var parameters_array = List.new;

		array.keysValuesDo({|key,value|
			parameters_array.add(key.asSymbol);
			parameters_array.add(value);
		})

		^parameters_array
	}

	trigger {|parameter,value|

		switch( parameter,

			\synthdef, {
				synthdef = value;
				synth_parameters = IdentityDictionary.new;
			},
			\octave, { octave = value },
			\fx, {

				this.createFx(value);

			},
			\setFx, {
				value.keysValuesDo({|k,v|
					fx_parameters[k]=v;
					currentFx.set(k,v);
				});
			},
			\note, {
				this.createSynth([\t_trig,1,\note,(octave*12)+value]++this.parameters_array(synth_parameters));
			},
			\amp_trig, {
				if( value > 0 ) {
					this.createSynth([\t_trig,1,\amp,value]++this.parameters_array(synth_parameters));
				}
			},
			// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value]); },
			\chord, {
				// synth.set(\t_trig,1,\note,(octave*12)+value);
			},
			{ // default:
				synth_parameters[parameter.asSymbol]=value;
				if( value.isNil || value == 0, {}, { synth.set(parameter.asSymbol,value) });
			},


		);


	}

	createFx {|synthdef_|

		var fx;


		if( synthdef_.notNil,{
			if( currentFx.notNil, {
				currentFx.free;
				// currentFx = Synth.replace(currentFx,synthdef_);
			}, {
				// currentFx = Synth.new(synthdef_);
			});
			fx_parameters.postln;
			currentFx = Synth.new(synthdef_,this.parameters_array(fx_parameters));

		}, {
			"clear currentFX".postln;
			currentFx = nil;
		});

	}



	currentFx_{|synthdef_|
		this.createFx(synthdef_);
	}

	setFx{|parameter,value|
		fx_parameters[parameter] = value;
		currentFx.set(parameter,value);
	}

	set {|parameter,value|
		synth_parameters[parameter] = value;
	}


}
