Sequenceable : I8Tnode
{

	classvar <>classSequencer;
	var <>sequencer;

	*new{|name_,graph_|
		^super.new.init(name_,this.graph);
	}

	seq {|pattern,repetitions,key|
		this.addPattern(key,pattern,repetitions);
	}
	addPattern {|key,pattern,repetitions|
		sequencer.addPattern(name,key,pattern,repetitions);
	}
	removePattern {|key|
		sequencer.removePattern(name,key);
	}


	play {|position|
		^sequencer.playInstrument( this, position );
	}
	stop {|position|
		^sequencer.stopInstrument( this );
	}

}
