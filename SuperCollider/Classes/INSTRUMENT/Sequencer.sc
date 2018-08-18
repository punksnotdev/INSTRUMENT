// instrument_tracks se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8Tnode
{

	var <instrument_tracks;

	var <>speed;
	var <>repeat;

	var patterns;
	var sequence;

	var tdef;

	var clock;
	var playing;

	*new {
		// SequencerEvent instances need to have a reference to 'this' (sequencer):

		^super.new.init();
	}

	init {

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		instrument_tracks = Dictionary.new();


		speed = 1;
		repeat = 4;

		clock = 0;
		playing = true;
	}

	play {

		playing = true;

		tdef = Tdef(\sequencer,{

			inf.do{|i|

				if( playing, {

					instrument_tracks.collect({|track|
						track.fwd( i );
					});

				});

				(1/32).wait;

			}


		}).play;

	}

	pause {
		playing = false;
	}

	stop {
		tdef.stop;
	}

	rewind {
		clock = 0;
	}


	go {|time|

		instrument_tracks.collect({|track|
			track.go( time );
		});

	}


	playInstrument {|instrument, position|
		^instrument_tracks[instrument.name].play(position);
	}
	stopInstrument {|instrument|
		^instrument_tracks[instrument.name].stop();
	}


	registerInstrument {|instrument|
		this.createTrack(instrument);
	}
	unregisterInstrument {|instrument|
		this.deleteTrack(instrument);
	}


	seq {|track,parameter,key,pattern,play_parameters|
		this.addPattern(track,parameter,key,pattern,play_parameters);
	}

	addPattern {|track,parameter,key,pattern,play_parameters|
		instrument_tracks[ track ].addPattern(parameter,key,pattern,play_parameters);
	}

	removePattern {|track,parameter,key|
		instrument_tracks[ track ].removePattern(parameter,key);
	}

	getPattern {|track,parameter,key|
		^instrument_tracks[ track ].getPattern(parameter,key);
	}

	setPatternParameters {|track,parameter,key,play_parameters|
		^instrument_tracks[ track ].setPatternParameters(parameter,key,play_parameters);
	}

	createTrack {|instrument|

		if( instrument.isKindOf(Instrument), {

			if( instrument_tracks[instrument.name] == nil, {
				instrument_tracks[instrument.name] = SequencerTrack.new(instrument);
			}, {
				instrument_tracks[instrument.name].instrument = instrument;
			});

		},{

			if( instrument_tracks[instrument] == nil, {
				instrument_tracks[instrument] = SequencerTrack.new(instrument);
			});

		});
	}

	deleteTrack {|instrument|

		if( instrument.isKindOf(Instrument), {
			instrument_tracks[instrument.name] = nil;
		},{
			instrument_tracks[instrument] = nil;
		});

	}

	setSpeed{|name_,speed_|
		instrument_tracks[name_].speed = speed_;
	}


	makeCanonVoice {|voice|

		var voiceStr = "";

	    voice.notes.do({|n,i|
			voiceStr = voiceStr ++ n.asString;
			if( voice.durs[i] > 0, {
				voiceStr = voiceStr ++ ":"++voice.durs[i].asString
			});
			voiceStr = voiceStr ++ " "
	    });

		^voiceStr

	}
}
