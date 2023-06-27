// sequencerTracks se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8TNode
{

	var <sequencerTracks;

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


	var >printBeats;


	// V2
	var <queue;
	var <tdef;
	var <timeSignature;

	// END V2



	*new {|main_|
		// ^super.new.initV2(main_);
		^super.new.init(main_);
	}

	init {|main_|

		this.initV2(main_);

		main = main_;

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		singleFunctions = IdentityDictionary.new;
		repeatFunctions = IdentityDictionary.new;

		sequencerTracks = IdentityDictionary.new();

		loopers = IdentityDictionary.new;

		beats = 0;
		speed = 1;
		repeat = 4;

		clock = 0;
		playing = true;

		printBeats = false;

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

				if( (i % (( 32 * (timeSignature.tick*2) ))) == 0, {


					// if bar start, check queue
					if( timeSignature.isKindOf(Event) ) {
						this.queueDo(\stop);
						this.queueDo(\go);
						if( beats % timeSignature.beats == 0 ) {
							this.queueDo(\play);
						};

					};

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

					if( printBeats ) {
						beats.postln;
					};

				});

				if( playing, {
					sequencerTracks.collect({|track|
						track.fwd( i, (i % (( 32 * (timeSignature.tick*2) ))) );
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

		sequencerTracks.collect({|track|
			track.go( time );
		});

		if(time == 0) {
			tdef.reset();
		};

		// ((1/32)*max(0.01,max(0.025,speed).reciprocal)).wait;


	}


	playInstrument {|instrument, position|
		this.addToQueue(\play,(
			item: sequencerTracks[instrument.name],
			data: (
				position: position
			)
		));

		main.displayTracks();
	}

	stopInstrument {|instrument|
		this.addToQueue(\stop,(
			item: sequencerTracks[instrument.name],
		));
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

		patternEvent = sequencerTracks[ track ].addPattern(parameter,key,pattern);


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
		var sequencerTrack = sequencerTracks[track];
		if(sequencerTrack.notNil) {
			sequencerTrack.parameterTracks[parameter].updateSequenceInfo;
		};
	}
	removePattern {|track,parameter,key|
		var sequencerTrack = sequencerTracks[ track ];
		sequencerTrack.removePattern(parameter,key);
	}
	clearPatterns {|track,parameter|
		var sequencerTrack = sequencerTracks[ track ];
		if(sequencerTrack.notNil) {
			sequencerTrack.clearPatterns(parameter);
		};
	}

	getPattern {|track,parameter,key|
		var sequencerTrack = sequencerTracks[ track ];
		if(sequencerTrack.notNil) {
			^sequencerTrack.getPattern(parameter,key);
		};
	}

	getPatterns {|track,parameter|
		var sequencerTrack = sequencerTracks[ track ];
		if(sequencerTrack.notNil) {
			^sequencerTrack.getPatterns(parameter);
		};
	}

	setPatternParameters {|track,parameter,key,play_parameters|

		^sequencerTracks[ track ].setPatternParameters(parameter,key,play_parameters);
	}

	createTrack {|instrument|

		if( ( instrument.isKindOf(I8TInstrument) || instrument.isKindOf(InstrumentGroup) ), {

			if( sequencerTracks[instrument.name] == nil, {
				sequencerTracks[instrument.name] = SequencerTrack.new(instrument, main );
			}, {
				sequencerTracks[instrument.name].instrument = instrument;
			});

		}, {

			if( sequencerTracks[instrument] == nil, {
				sequencerTracks[instrument] = SequencerTrack.new(instrument);
			});

		});

		// main.displayTracks();

	}

	deleteTrack {|instrument|

		if( instrument.isKindOf(I8TInstrument), {
			sequencerTracks[instrument.name].stop;
			sequencerTracks.removeAt(instrument.name);
		},{
			sequencerTracks[instrument].stop;
			sequencerTracks.removeAt(instrument);
		});

		main.displayTracks();


	}

	setSpeed{|name_,speed_|
		sequencerTracks[name_].speed = speed_;
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
