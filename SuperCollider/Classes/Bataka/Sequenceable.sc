Sequenceable : InstrumentNode
{

	classvar <>classSequencer;
	var <>sequencer;

	*new{|name_,graph_|
		this.initClass(this.graph);
		^super.new.init(name_,this.graph);
	}

	*initClass{|graph_|
		super.initClass(graph_);
		if(graph_!=nil,{
			classSequencer = graph_.sequencer;
		});
	}
	init{|name_,graph_|
		super.init(name_,graph_);
		sequencer = classSequencer;
	}

	seq {|pattern,key|
		this.addPattern(key,pattern);
	}
	addPattern {|key,pattern|
		if( key == nil, {
			key = sequencer.patterns[name].size;
		});
		sequencer.addPattern(name,key,pattern);
	}


	play {
		sequencer.playInstrument( this );
	}

}
