SynthPlayer : Instrument
{

	var <synthdef;

	*new{|name_,synthdef_|
		^super.new.init(name_,synthdef_,this.graph);
	}

	init{|name_,synthdef_,graph_|

		if(synthdef_.isKindOf(Symbol), {
			synthdef = synthdef_;
		},{
			synthdef = \test;
		});

		this.createSynth();

		super.init(name_,graph_);

	}


	synthdef_{|synthdef_|

		synthdef = synthdef_;

		this.createSynth();

		^synthdef

	}

	createSynth{

		if(synth.notNil, {
			synth.free;
		});

		synth = Synth( synthdef.asSymbol );

	}

	trigger {|parameter,value|

		if( parameter == \note, {
			synth.set(\t_trig,1,\note,(octave*12)+value);
		}, {
			synth.set(parameter.asSymbol,value);
		});


	}

}
