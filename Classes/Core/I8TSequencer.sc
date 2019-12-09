// sequencer_tracks se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8TNode
{

	var <sequencer_tracks;

	var <>speed;
	var <>repeat;

	var patterns;
	var sequence;

	var tdef;

	var clock;
	var playing;

	var beats;

	var <singleFunctions;
	var <>repeatFunctions;

	var main;


	var loopers;

	*new {|main_|
		^super.new.init(main_);
	}

	init {|main_|

		main = main_;

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		singleFunctions = IdentityDictionary.new;
		repeatFunctions = IdentityDictionary.new;

		sequencer_tracks = IdentityDictionary.new();

		loopers = IdentityDictionary.new;

		beats = 0;
		speed = 1;
		repeat = 4;

		clock = 0;
		playing = true;
	}



	setupLooper{|looper|
		loopers[looper]=IdentityDictionary.new;
		8.do{|j|
			loopers[looper][j]=IdentityDictionary.new;
		}
	}


	play {

		playing = true;

		tdef = Tdef(( "sequencer" ++ "_" ++ main.threadID).asSymbol,{

			inf.do{|i|

				if( (i % 32) == 0, {

					if( beats % 1 == 0 ) {

						loopers.collect({|stateArray,looper|

							stateArray.collect({|state,stateIndex|

								switch( state,
									\awaitingRec, {
										looper.performRec(stateIndex);
										loopers[looper][stateIndex]=\recording;
									},
									\awaitingStart, {
										looper.performStart(stateIndex);
										loopers[looper][stateIndex]=\playing;
									},
									\awaitingStop, {
										looper.performStop(stateIndex);
										loopers[looper][stateIndex]=\stopped;
									}
								);

							});
						});
					};



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


					beats = beats+1;


				});

				if( playing, {
					sequencer_tracks.collect({|track|
						track.fwd( i );
					});
				});

				((1/32)*max(0.01,max(0.025,speed).reciprocal)).wait;

			}


		});

		tdef.quant=4;
		tdef.play(main.clock);

	}

	pause {
		playing = false;
		tdef.pause;
	}

	stop {
		playing = false;
		this.rewind();
		tdef.stop;
	}

	rewind {
		this.go(0);
		clock = 0;
	}


	go {|time|

		beats = time;

		sequencer_tracks.collect({|track|
			track.go( time );
		});

		if(time == 0) {
			tdef.reset();
		};

		// ((1/32)*max(0.01,max(0.025,speed).reciprocal)).wait;


	}


	playInstrument {|instrument, position|
		sequencer_tracks[instrument.name].play(position);
		main.displayTracks();
	}

	stopInstrument {|instrument|
		sequencer_tracks[instrument.name].stop();
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

		var patternEvent;

		patternEvent = sequencer_tracks[ track ].addPattern(parameter,key,pattern);


		if( patternEvent.pattern.totalDuration > 0 ) {

			var patternInfo = (
				track: track,
				pattern: pattern,
				beats:patternEvent.pattern.totalDuration,
				param:parameter,
				key:key,
				event: patternEvent
			);


			// main.displayNextPattern(patternInfo);

			Task.new({
				// 0.1.wait;
				// ["pattern",parameter,patternInfo].postln;
				0.1.wait;
				(track++"."++parameter++": "++key).postln;
				("New pattern duration: " ++ patternEvent.pattern.totalDuration).postln;
			}).play;

		}
		^patternEvent;

	}
	updateSequenceInfo {|track,parameter|
		var parameterTrack = sequencer_tracks[track];
		if(parameterTrack.notNil) {
			parameterTrack.parameterTracks[parameter].updateSequenceInfo;
		};
	}
	removePattern {|track,parameter,key|
		var parameterTrack = sequencer_tracks[ track ];
		if(parameterTrack.notNil) {
			parameterTrack.removePattern(parameter,key);
		};
	}
	clearPatterns {|track,parameter|
		var parameterTrack = sequencer_tracks[ track ];
		if(parameterTrack.notNil) {
			parameterTrack.clearPatterns(parameter);
		};
	}

	getPattern {|track,parameter,key|
		var parameterTrack = sequencer_tracks[ track ];
		if(parameterTrack.notNil) {
			^parameterTrack.getPattern(parameter,key);
		};
	}

	getPatterns {|track,parameter|
		var parameterTrack = sequencer_tracks[ track ];
		if(parameterTrack.notNil) {
			^parameterTrack.getPatterns(parameter);
		};
	}

	setPatternParameters {|track,parameter,key,play_parameters|

		^sequencer_tracks[ track ].setPatternParameters(parameter,key,play_parameters);
	}

	createTrack {|instrument|

		if( ( instrument.isKindOf(I8TInstrument) || instrument.isKindOf(InstrumentGroup) ), {

			if( sequencer_tracks[instrument.name] == nil, {
				sequencer_tracks[instrument.name] = SequencerTrack.new(instrument, main );
			}, {
				sequencer_tracks[instrument.name].instrument = instrument;
			});

		}, {

			if( sequencer_tracks[instrument] == nil, {
				sequencer_tracks[instrument] = SequencerTrack.new(instrument);
			});

		});

		// main.displayTracks();

	}

	deleteTrack {|instrument|

		if( instrument.isKindOf(I8TInstrument), {
			sequencer_tracks[instrument.name].stop;
			sequencer_tracks.removeAt(instrument.name);
		},{
			sequencer_tracks[instrument].stop;
			sequencer_tracks.removeAt(instrument);
		});

		main.displayTracks();


	}

	setSpeed{|name_,speed_|
		sequencer_tracks[name_].speed = speed_;
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


	recLooper {|looper,layer|
		if( layer.isNil ) {
			layer = 0;
		};
		if( loopers[looper].isNil, {
			this.setupLooper( looper );
		});
		loopers[looper][layer] = \awaitingRec;
	}
	startLooper {|looper,layer|
		if( layer.isNil ) {
			layer = 0;
		};
		loopers[looper][layer] = \awaitingStart;
	}
	stopLooper {|looper,layer|
		if( layer.isNil ) {
			layer = 0;
		};
		loopers[looper][layer] = \awaitingStop;
	}
}
