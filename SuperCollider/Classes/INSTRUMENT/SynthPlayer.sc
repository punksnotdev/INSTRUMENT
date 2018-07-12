SynthPlayer : Instrument
{

	var <synthdef;

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

		super.init(synthdef_,graph_);

	}


	synthdef_{|synthdef_|

		synthdef = synthdef_;

		this.createSynth();

		^synthdef

	}

	createSynth{|parameters|

		if(synth.notNil, {
			synth.free;
		});

		synth = Synth( synthdef.asSymbol, parameters );

	}

	trigger {|parameter,value|

		switch( parameter,

			\synthdef, { synthdef = value },
			\octave, { octave = value },
			\note, { this.createSynth([\t_trig,1,\note,(octave*12)+value]); },
			\amp_trig, { this.createSynth([\t_trig,1,\amp,value]); },
			// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value]); },
			\chord, {
				// synth.set(\t_trig,1,\note,(octave*12)+value);
			},
			{ // default:
				if( value.isNil || value == 0, {}, { this.createSynth(); });
			}
		);


	}

}
