// sequencerTracks se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8TNode
{

	var <sequencerTracks;

	var <>speed;
	var <>repeat;

	var patterns;
	var sequence;

	var <barRoutine;
	var <beatRoutine;

	var playing;

	var beats;

	var <singleFunctions;
	var <>repeatFunctions;

	var main;


	var loopers;


	var >printBeats;


	// V2
	var <queue;
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
		repeat = 1;

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

		// Stop existing routines if any
		if(barRoutine.notNil) { barRoutine.stop; barRoutine = nil };
		if(beatRoutine.notNil) { beatRoutine.stop; beatRoutine = nil };

		// Bar callback — fires at bar boundaries for queue/looper processing
		barRoutine = Routine({
			loop {
				this.queueDo(\stop);
				this.queueDo(\go);
				this.queueDo(\play);
				this.applyPendingRestarts;
				this.processLoopers;
				timeSignature.beats.wait;
			};
		}).play(main.clock, quant: timeSignature.beats);

		// Beat callback — fires every beat for functions and beat counting
		beatRoutine = Routine({
			loop {
				this.processSingleFunctions;
				this.processRepeatFunctions;

				if( printBeats ) { beats.postln };

				beats = beats + 1;
				1.wait;
			};
		}).play(main.clock, quant: 1);

	}

	processLoopers {
		loopers.do({|stateArray, looper|
			stateArray.do({|state, stateIndex|
				switch( state,
					\awaitingRec, {
						looper.performRec(stateIndex);
						loopers[looper][stateIndex] = \recording;
					},
					\awaitingStart, {
						looper.performStart(stateIndex);
						loopers[looper][stateIndex] = \playing;
					},
					\awaitingStop, {
						looper.performStop(stateIndex);
						loopers[looper][stateIndex] = \stopped;
					}
				);
			});
		});
	}

	applyPendingRestarts {
		sequencerTracks.do({|track|
			track.parameterTracks.do({|pt|
				if(pt.pendingRestart == true) {
					pt.pendingRestart = false;
					pt.stop;
					pt.play(0, 0);
				};
			});
		});
	}

	processSingleFunctions {
		if( singleFunctions[beats].isKindOf(Function) ) {
			singleFunctions[beats].value();
		};
	}

	processRepeatFunctions {
		repeatFunctions.do({|f, k|
			f.do({|rf, l|
				var offset = 0;
				if(rf.offset.isInteger) {
					offset = rf.offset;
				};
				if( (beats - offset) % k.asInteger == 0 ) {
					rf.function.value();
				};
			});
		});
	}

	pause {
		playing = false;
		if(barRoutine.notNil) { barRoutine.stop; barRoutine = nil };
		if(beatRoutine.notNil) { beatRoutine.stop; beatRoutine = nil };
		sequencerTracks.do({|track| track.stop });
	}

	stop {
		playing = false;
		if(barRoutine.notNil) { barRoutine.stop; barRoutine = nil };
		if(beatRoutine.notNil) { beatRoutine.stop; beatRoutine = nil };
		sequencerTracks.do({|track| track.stop });
		this.go(0);
	}

	rewind {
		this.go(0);
	}


	go {|time|
		beats = time ? 0;
		sequencerTracks.do({|track|
			track.go( time );
		});
	}


	playInstrument {|instrument, position|

		sequencerTracks[instrument.name].play;
		// this.addToQueue(\play,(
		// 	item: sequencerTracks[instrument.name],
		// 	data: (
		// 		position: position
		// 	)
		// ));

		main.displayTracks();
	}

	stopInstrument {|instrument|
		sequencerTracks[instrument.name].stop();
		// this.addToQueue(\stop,(
		// 	item: sequencerTracks[instrument.name],
		// ));
		main.displayTracks();
	}


	registerInstrument {|instrument|
		this.createTrack(instrument);
	}
	unregisterInstrument {|instrument|
		this.deleteTrack(instrument);
	}


	seq {|track,parameter,key,pattern,play_parameters,test|
		^this.addPattern(track,parameter,key,pattern,play_parameters,test);
	}

	addPattern {|track,parameter,key,pattern,parameters,test|

		var patternEvent;
		var sequencerTrack;

		sequencerTrack = sequencerTracks[ track ];


		if( sequencerTrack.notNil, {

			patternEvent = sequencerTrack.addPattern(parameter,key,pattern,parameters,test);

			if( patternEvent.isKindOf(PatternEvent)) {

				if( patternEvent.pattern.totalDuration > 0 ) {

					var patternInfo = (
						track: track,
						pattern: pattern,
						beats:patternEvent.pattern.totalDuration,
						param:parameter,
						key:key,
						event: patternEvent
					);


					if( test.asSymbol != \test, {
						("Added pattern: "++track++"."++parameter++": "++key).postln;
					}, {
						("Test pattern: "++track++"."++parameter++": "++key).postln;
					});
					("Duration: " ++ patternEvent.pattern.totalDuration).postln;

				}
				^patternEvent;

			};

		}, {

			"Sequencer Track is nil".warn;

		});

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


		if( ( instrument.isKindOf(Sequenceable) ), {
		// if( ( instrument.isKindOf(I8TInstrument) || instrument.isKindOf(InstrumentGroup) ), {

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

		sequencerTracks[instrument].play;

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
