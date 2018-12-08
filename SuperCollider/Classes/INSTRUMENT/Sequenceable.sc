Sequenceable : I8TNode
{

	var <>sequencer;
	var <speed;

	var nextIndex;

	*new{|name_,graph_|
		^super.new.init(name_,this.graph);
	}

	init{|name_,graph_|
		super.init(name_,graph_);
		speed = 1;
		nextIndex = 0;
		this.play;
	}

	remove{
		this.sequencer.unregisterInstrument(this);
	}

	kill{
		this.remove(this);
	}


	seq {|parameter,pattern,play_parameters,key|

		var seqKey = key;

		if( key.isNil ) {
			seqKey = nextIndex;
		};

		^sequencer.addPattern(
			name,
			parameter,
			seqKey,
			pattern,
			play_parameters
		);

	}
	rm {|parameter,key|
		sequencer.removePattern(name,parameter,key);
	}
	// set {|pattern,parameters|
	// 	this.removePattern(pattern);
	// 	this.seq(pattern,parameters);
	// }


	get {|parameter,key|
		^sequencer.getPattern(name,parameter,key);
	}
	set {|parameter,key,parameters|
		^sequencer.setPatternParameters(name,parameter,key,parameters);
	}


	play {|position|
		^sequencer.playInstrument( this, position );
	}
	stop {|position|
		^sequencer.stopInstrument( this );
	}

	go{ |time|
		sequencer.postln;
		^sequencer.instrument_tracks[name].parameterTracks.collect{|track|
			track.go(time)
		};
	}


	trigger {
		// do something
	}


	speed_{|sp_|
		speed = sp_;
		sequencer.setSpeed(name,speed);
	}



	patterns{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].patterns;
	}
	sequence{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequence;
	}
	sequenceInfo{|parameter|
		^sequencer.instrument_tracks[name].parameterTracks[parameter].sequenceInfo;
	}

	at{|index|
		nextIndex = index;
		^this
	}

}
