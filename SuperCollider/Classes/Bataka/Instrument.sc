Instrument : Sequenceable
{

	var <>synth;
	var <>volume;
	var <>octave;

	*new{|name_,graph_|
		^super.new.init(name_,this.graph);
	}

	init{|name_,graph_|
		super.init(name_,graph_);
		volume = 1;
		octave = 4;
	}

	trigger {|pattern,value|
		
		if( pattern.isKindOf(I8Tpattern), {
			if( pattern.target == \note, {
				synth.set(\t_trig,1,\note,(octave*12)+value);
			});
		});

	}

}
