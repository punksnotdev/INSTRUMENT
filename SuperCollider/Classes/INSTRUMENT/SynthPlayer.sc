SynthPlayer : Instrument
{

	var <synthdef;

	var synth_parameters;

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

		synth_parameters = IdentityDictionary.new;
		super.init(synthdef_,graph_);

	}

	currentFx_{|synthdef_|
		this.createFx(synthdef_);
	}

	synthdef_{|synthdef_|

		synthdef = synthdef_;

		this.createSynth();

		^synthdef

	}

	createSynth{|parameters|

		if(synth.notNil, {
			synth.free;
		}, {});

		if( currentFx.notNil, {
			// [currentFx,"synthfx"].postln;
			synth = Synth.before( currentFx, synthdef.asSymbol, parameters );
		}, {
			synth = Synth( synthdef.asSymbol, parameters );
		});

	}

	trigger {|parameter,value|

		switch( parameter,

			\synthdef, { synthdef = value },
			\octave, { octave = value },
			\fx, {

				this.createFx(value);

			},
			\setFx, {
				currentFx.set(parameter,value);
			},
			\note, { this.createSynth([\t_trig,1,\note,(octave*12)+value]); },
			\amp_trig, { this.createSynth([\t_trig,1,\amp,value]); },
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

		if( currentFx.notNil, {
			currentFx.free;
		});

		if( synthdef_.notNil,{

			fx = Synth(synthdef_);

			if( fx.isKindOf(Synth), {
				currentFx=fx;
			}, {
				currentFx = nil;
			});
			currentFx.postln;
		}, {
			currentFx = nil;
		});

	}

}
