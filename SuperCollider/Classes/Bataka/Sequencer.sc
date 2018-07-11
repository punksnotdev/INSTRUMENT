// instruments se va a tomar de nodegraph!
//defaultRepetitions

Sequencer : I8Tnode
{

	var <instruments;

	var <>speed;
	var <>repeat;

	var patterns;
	var sequence;

	var tdef;

	*new {
		// SequencerEvent instances need to have a reference to 'this' (sequencer):

		^super.new.init();
	}

	init {

		SequencerTrack.classSequencer = this;
		SequencerEvent.classSequencer = this;

		instruments = Dictionary.new();


		speed = 1;
		repeat = 4;

	}

	play {

		tdef = Tdef(\sequencer,{

			var beat = 0;

			inf.do{|i|

				instruments.collect({|track|
					track.fwd(i);
				});

				(1/32).wait;
			}


		}).play;

	}

	stop {
		tdef.stop;
	}


	playInstrument {|instrument, position|
		^instruments[instrument.name].play(position);
	}
	stopInstrument {|instrument|
		^instruments[instrument.name].stop();
	}


	registerInstrument {|instrument|
		this.createTrack(instrument);
	}
	unregisterInstrument {|instrument|

	}


	seq {|track,key,pattern,parameters|
		"seq in sequencer called".postln;
		this.addPattern(track,key,pattern,parameters);
	}

	addPattern {|track,key,pattern,parameters|
		instruments[ track ].addPattern(key,pattern,parameters);
	}

	removePattern {|track,key|
		instruments[ track ].removePattern(key);
	}

	getPattern {|track,key|
		^instruments[ track ].getPattern(key);
	}

	setPattern {|track,key,parameters|
		^instruments[ track ].setPattern(key,parameters);
	}

	createTrack {|instrument|

		if( instrument.isKindOf(Instrument), {

			if( instruments[instrument.name] == nil, {
				instruments[instrument.name] = SequencerTrack.new(instrument);
			}, {
				instruments[instrument.name].instrument = instrument;
			});

		},{

			if( instruments[instrument] == nil, {
				instruments[instrument] = SequencerTrack.new();
			});

		});
	}

	deleteTrack {|instrument|

		if( instrument.isKindOf(Instrument), {
			instruments[instrument.name] = nil;
		},{
			instruments[instrument] = nil;
		});

	}

	setSpeed{|name_,speed_|
		instruments[name_].speed = speed_;
	}


}
