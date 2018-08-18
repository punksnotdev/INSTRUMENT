SynthPlayer : Instrument
{

	var <synthdef;

	var synth_parameters;
	var fx_parameters;

	var <currentFx;

	*new{|name_,synthdef_|
		^super.new.init(name_,this.graph,synthdef_);
	}

	init{|name_,graph_,synthdef_|
		if( name_.notNil && synthdef_.notNil, {
			[name_,synthdef_].postln;

			if(synthdef_.isKindOf(Symbol), {
				synthdef = synthdef_;
			},{
				synthdef = \test;
			});

			this.createSynth();
			currentFx = nil;

			synth_parameters = IdentityDictionary.new;
			fx_parameters = IdentityDictionary.new;
			super.init(name_,graph_);

		});
	}

	synthdef_{|synthdef_|

		synthdef = synthdef_;
		synth_parameters=IdentityDictionary.new;

		this.createSynth();

		^synthdef

	}

	createSynth{|parameters|

		"synth".postln;
		synth.postln;

		if(synth.notNil, {
			// synth.free;
			synth = nil;
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

		if( value.isKindOf(Event) == false, {
			value = ( val: value, amplitude: 0.5 );
		});

		switch( parameter,

			\synthdef, {
				synthdef = value.val;
				synth_parameters = IdentityDictionary.new;
			},
			\octave, { octave = value.val },
			\fx, {

				this.createFx(value.val);

			},
			\setFx, {
				value.val.keysValuesDo({|k,v|
					fx_parameters[k]=v;
					currentFx.set(k,v);
				});
			},
			\note, {
				// if is Event, get params
				var event = value;
				this.createSynth([\t_trig,1,\freq,((octave*12)+event.val).midicps,\note,(octave*12)+event.val,\amp,event.amplitude]++this.parameters_array(synth_parameters));

			},
			\ampTrig, {
				if( value.val > 0 ) {
					this.createSynth([\t_trig,1,\amp,value.val]++this.parameters_array(synth_parameters));
				}
			},
			// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value.val]); },
			\chord, {
				// synth.set(\t_trig,1,\note,(octave*12)+value.val);
			},
			{ // default:
				synth_parameters[parameter.asSymbol]=value.val;
				if( value.val.isNil || value.val == 0, {}, { synth.set(parameter.asSymbol,value.val) });
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
