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


	chord {|chord=\m|

		var chordNames, intervals;

		chordNames=[\M, \m, \M7, \m7, \Mmaj7, \mmaj7, \M9, \M9m, \m9, \m9m];

		intervals = [
		  [0,7,12,16,19,24],
		  [0,7,12,15,19,24],
		  [0,7,10,16,19,24],
		  [0,7,10,15,19,24],
		  [0,7,10,15,19,24],
		  [0,7,11,16,19,24],
		  [0,7,11,15,19,24],
		  [0,7,14,16,19,24],
		  [0,7,13,16,19,24],
		  [0,7,14,15,19,24],
		  [0,7,13,15,19,24]
		];

		if(chordNames.includes(chord))
		{
		  ^intervals[chordNames.indexOf(chord)];
		}
		{
			^0;
		};

	}

}
