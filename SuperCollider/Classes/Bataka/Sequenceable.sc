Sequenceable : I8Tnode
{

	classvar <>classSequencer;
	var <>sequencer;

	*new{|name_,graph_|
		^super.new.init(name_,this.graph);
	}

	seq {|pattern,key,parameters|
		if( key.isArray, {
			this.addPattern(nil,pattern,key);
		}, {
			this.addPattern(key,pattern,parameters);
		});
	}
	addPattern {|key,pattern,parameters|
		sequencer.addPattern(name,key,pattern,parameters);
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

	trigger {
		// do something
	}

}
