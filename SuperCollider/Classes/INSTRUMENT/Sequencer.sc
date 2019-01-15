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

	var main;


	var loopers;

	*new {|main_|
		// SequencerEvent instances need to have a reference to 'this' (sequencer):

		^super.new.init(main_);
	}

	init {|main_|

		main = main_;

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		singleFunctions = IdentityDictionary.new;
		repeatFunctions = IdentityDictionary.new;

		instrument_tracks = IdentityDictionary.new();

		loopers = IdentityDictionary.new;

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


					if( beats % 4 == 0 ) {

						loopers.collect({|state,looper|

							switch( state,
								\awaitingRec, {

									"Looper: Rec".postln;

									looper.performRec();
									loopers[looper]=\recording;
								},
								\awaitingStart, {

									"Looper: Start".postln;

									looper.performStart();
									loopers[looper]=\playing;
								},
								\awaitingStop, {

									"Looper: Stop".postln;

									looper.performStop();
									loopers[looper]=\stopped;
								}
							);

						});
					};

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
		instrument_tracks[instrument.name].play(position);
		main.displayTracks();
	}

	stopInstrument {|instrument|
		instrument_tracks[instrument.name].stop();
		main.displayTracks();
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

	addPattern {|track,parameter,key,pattern|

		var patternEvent = instrument_tracks[ track ].addPattern(parameter,key,pattern);

		if( patternEvent.pattern.totalDuration > 0 ) {
			var patternInfo = (
				track: track,
				pattern: pattern,
				beats:patternEvent.pattern.totalDuration,
				param:parameter,
				key:key,
				event: patternEvent
			);

			main.displayNextPattern(patternInfo);



		}
		^patternEvent;

	}
	updateSequenceInfo {|track,parameter|
		instrument_tracks[track].parameterTracks[parameter].updateSequenceInfo;
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

		main.displayTracks();

	}

	deleteTrack {|instrument|

		if( instrument.isKindOf(Instrument), {
			instrument_tracks[instrument.name].stop;
			instrument_tracks.removeAt(instrument.name);
		},{
			instrument_tracks[instrument].stop;
			instrument_tracks.removeAt(instrument);
		});

		main.displayTracks();


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


	recLooper {|looper|
		loopers[looper] = \awaitingRec;
	}
	startLooper {|looper|
		loopers[looper] = \awaitingStart;
	}
	stopLooper {|looper|
		loopers[looper] = \awaitingStop;
	}
}
