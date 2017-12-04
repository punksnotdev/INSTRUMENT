Sequenceable : I8Tnode
{

	var <>sequencer;
	var <speed;

	*new{|name_,graph_|
		^super.new.init(name_,this.graph);
	}

	init{|name_,graph_|
		super.init(name_,graph_);
		speed = 1;
	}


	addPattern {|key,pattern,parameters|
		sequencer.addPattern(name,key,pattern,parameters);
	}
	removePattern {|key|
		sequencer.removePattern(name,key);
	}


	seq {|pattern,key,parameters|
		if( key.isArray, {
			this.addPattern(nil,pattern,key);
		}, {
			this.addPattern(key,pattern,parameters);
		});
	}
	rm {|key|
		this.removePattern(key);
	}
	set {|pattern,parameters|
		this.removePattern(pattern);
		this.seq(pattern,parameters);
	}


	getPattern {|key|
		^sequencer.getPattern(name,key);
	}
	setPattern {|key,parameters,pattern|
		^sequencer.setPattern(name,key,parameters,pattern);
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


	speed_{|sp_|
		speed = sp_;
		sequencer.setSpeed(name,speed);
	}



	patterns{|parameter|
		^sequencer.instruments[name].parameterTracks[parameter].patterns;
	}
	sequence{|parameter|
		^sequencer.instruments[name].parameterTracks[parameter].sequence;
	}
	sequenceInfo{|parameter|
		^sequencer.instruments[name].parameterTracks[parameter].sequenceInfo;
	}

}
