Sequenceable : I8Tnode
{

	classvar <>classSequencer;
	var <>sequencer;

	*new{|name_,graph_|
		// this.initClass(this.graph);
		^super.new.init(name_,this.graph);
	}

	// *initClass{|graph_|
	// 	super.initClass(graph_);
	// 	if(graph_!=nil,{
	// 		classSequencer = graph_.sequencer;
	// 	});
	// }
	// init{|name_,graph_|
	// 	super.init(name_,graph_);
		// sequencer = classSequencer;
		// sequencer.registerInstrument(this);
	// }

	seq {|pattern,repetitions,key|
		this.addPattern(key,pattern,repetitions);
	}
	addPattern {|key,pattern,repetitions|
		sequencer.addPattern(name,key,pattern,repetitions);
	}
	removePattern {|key|
		// if( key.isKindOf(Array), {
		// 	sequencer.removePattern(name,nil,key);
		// }, {
			sequencer.removePattern(name,key);
		// });
	}


	play {|position|
		^sequencer.playInstrument( this, position );
	}
	stop {|position|
		^sequencer.stopInstrument( this );
	}

}
