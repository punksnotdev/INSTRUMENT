// instrument_tracks se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8TNode
{

	var <instrument_tracks;

	var <>speed;
	var <>repeat;

	var patterns;
	var sequence;

	var tdef;

	var clock;
	var playing;

	var beats;

	var <singleFunctions;
	var <repeatFunctions;


	*new {
		// SequencerEvent instances need to have a reference to 'this' (sequencer):

		^super.new.init();
	}

	init {

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		singleFunctions = IdentityDictionary.new;
		repeatFunctions = IdentityDictionary.new;

		instrument_tracks = Dictionary.new();

		beats = 0;
		speed = 1;
		repeat = 4;

		clock = 0;
		playing = true;
	}

	play {

		playing = true;

		tdef = Tdef(\sequencer,{

			inf.do{|i|

				if( i % 32 == 0, {

					beats = beats+1;

					if( singleFunctions[beats].isKindOf(Function), {
						singleFunctions[beats].value();
					});

					repeatFunctions.collect({|f,k|

						f.collect({|rf,l|
							var offset = 0;

							if(rf.offset.isInteger, {
								offset = rf.offset;
							});

							if( (beats - offset) % k.asInteger == 0, {

								rf.function.value();
							});
						});

					});
				});

				if( playing, {
					instrument_tracks.collect({|track|
						track.fwd( i );
					});

				});

				((1/32)*max(0.01,max(0.025,speed).reciprocal)).wait;

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

		beats = time;

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
		^this.addPattern(track,parameter,key,pattern,play_parameters);
	}

	addPattern {|track,parameter,key,pattern,play_parameters|

		var patternEvent = instrument_tracks[ track ].addPattern(parameter,key,pattern,play_parameters);

		^(
			track: track,
			beats:patternEvent.pattern.totalDuration,
			param:parameter,
			key:key,
			play_params:play_parameters,
			event: patternEvent
		);
	}

	removePattern {|track,parameter,key|
		instrument_tracks[ track ].removePattern(parameter,key);
	}
	clearPatterns {|track,parameter|
		instrument_tracks[ track ].clearPatterns(parameter);
	}

	getPattern {|track,parameter,key|
		^instrument_tracks[ track ].getPattern(parameter,key);
	}

	getPatterns {|track,parameter|
		^instrument_tracks[ track ].getPatterns(parameter);
	}

	setPatternParameters {|track,parameter,key,play_parameters|
		[track,parameter,key,play_parameters].postln;
		^instrument_tracks[ track ].setPatternParameters(parameter,key,play_parameters);
	}

	createTrack {|instrument|

		if( instrument.isKindOf(Instrument), {
["createTrack", instrument.name].postln;
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

	clearRepeatFunctions {
		repeatFunctions = IdentityDictionary.new;
	}
}
