Sequenceable : I8TNode
{

	var <>sequencer;
	var <speed;

	var nextKey;

	*new{|graph_,name_|
		^super.new.init(this.graph,name_);
	}

	init{|graph_,name_|
		super.init(graph_,name_);
		speed = 1;
		nextKey = 0;
		this.play;
	}

	remove{
		this.sequencer.unregisterInstrument(this);
	}

	kill{
		this.remove(this);
	}


	seq {|parameter_,pattern,play_parameters,key|

		var seqKey = key;
		var parameter = parameter_;
		if( pattern.isNil ) {
			if( parameter.isKindOf(String)||parameter.isKindOf(Array) ) {
				pattern = parameter;
			};
			parameter = \trigger;
		};

		if( key.isNil ) {
			seqKey = nextKey;
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
		var rmKey = key;
		if( key.isNil ) {
			rmKey = nextKey;
		};
		sequencer.removePattern(name,parameter,rmKey);
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
		if( sequencer.notNil ) {
			^sequencer.playInstrument( this, position );
		}
	}
	stop {|position|
		if( sequencer.notNil ) {
			^sequencer.stopInstrument( this );
		}
	}

	go{ |time|
		if( time.isKindOf(Number)) {
			^sequencer.instrument_tracks[name].parameterTracks.collect{|track|
				track.go(time)
			};
		};
	}


	trigger {
		// do something
	}


	speed_{|sp_|
		if( speed.isKindOf(Number)) {
			speed = sp_;
			sequencer.setSpeed(name,speed);
		}
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

	at{|key|
		nextKey = key;
		^this
	}

}
