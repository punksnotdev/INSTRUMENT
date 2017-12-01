Sequenceable : I8Tnode
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

	seq {|pattern,repetitions,key|
		this.addPattern(key,pattern,repetitions);
	}
	addPattern {|key,pattern,repetitions|
		if( key == nil, {
			key = sequencer.sequence[name].size;
		});
		sequencer.addPattern(name,key,pattern,repetitions);
	}


	play {
		sequencer.playInstrument( this );
	}

}
