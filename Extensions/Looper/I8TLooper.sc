Looper : I8TSynthInstrument
{


		var <buffers;
		var maxDuration;
		var numChannels;

		var <>bus;
		var recSynths;
		var <playSynths;
		var <durations;
		var lastDuration;
		var main;

		var rate;

		// var }<nextLayer;

		*new{|bus_|
			^super.new.init(this.graph,bus_);
		}

		init{|graph_,bus_|

			if( bus_.isInteger, {
				bus = bus_;
			}, {
				bus = 0;
			});

			rate = 1;
			amp = 1;

			buffers = IdentityDictionary.new;
			playSynths = IdentityDictionary.new;
			recSynths = IdentityDictionary.new;
			durations = IdentityDictionary.new;

			maxDuration = 60;
			numChannels = 1;

			this.amp=1;

			main = graph_;

			super.init(graph_,"looper_"++bus);
		}



		rec {|layer|
			main.sequencer.recLooper( this, layer );
		}

		start {|layer|
			if( layer.notNil, {
				main.sequencer.startLooper( this, layer );
			}, {
				buffers.do({|buffer,index|

					// if( playSynths[index].isKindOf(Synth) == false ) {
						main.sequencer.startLooper( this, index );
					// };


				});
			});
		}

		stop {|layer|
			if( layer.notNil, {
				main.sequencer.stopLooper( this, layer );
			}, {
				playSynths.collect({|playSynth,index|
					main.sequencer.stopLooper( this, index );
				});
			});
		}
		play {|layer|
			this.start(layer);
			super.play;
		}


		// delete {|layer|
		// 	// if no layers selected
		// 	// delete all available layers
		//
		// 		// if layer exists
		// 		// delete it
		// }


		stopRecording {|nextLayer|


			if( recSynths[nextLayer].isKindOf(Synth)) {
				var recDuration;
				recDuration = TempoClock.default.beats - lastDuration;
				recSynths[nextLayer].free;
				// recDuration = recDuration / TempoClock.default.tempo;
				durations[nextLayer]= recDuration / TempoClock.default.tempo;
				recSynths[nextLayer] = nil;
				["Duration:", recDuration].postln;
			};

		}


		recordBuffer{|nextLayer|
			// if buffer for nextLayer not allocated,
			if( main.server.sampleRate.notNil && maxDuration.isKindOf(Number) ) {
				if( maxDuration > 0 ) {
				// if( buffers[ nextLayer ].isKindOf(Buffer) == false ) {
					buffers[ nextLayer ] = Buffer.alloc(
						main.server,
						main.server.sampleRate * maxDuration,
						1,
						{|buffer|
							recSynths[ nextLayer ] = Synth(\loopWrite, [
								\inBus, bus,
								\buffer, buffer
							]);
							lastDuration = TempoClock.default.beats;
						}
					);
				// };
				};
			};


		}

		performRec {|nextLayer|
			["performRec",nextLayer].postln;
			if( nextLayer.notNil, {

				this.recordBuffer(nextLayer)

			});
		}



		performStart {|nextLayer|

			["performStart",nextLayer].postln;

			if( nextLayer.notNil, {

				this.stopRecording(nextLayer);

				if( buffers[nextLayer].isKindOf(Buffer), {


					var synth;

					// play it:
					// first stop it if running

					if( playSynths[nextLayer].isKindOf(Synth)) {
						playSynths[nextLayer].release;
					};
					// then create new synth:

					if( fxSynth.isKindOf(Synth), {

						synth = Synth.before( fxSynth, \loopRead,
							[
							\out,fxBus,
							\buffer, buffers[nextLayer],
							\duration, durations[nextLayer],
							\amp, amp,
							\rate, rate
						]);


						synth.register;
					}, {

						synth = Synth.head( group, \loopRead,[
							\buffer, buffers[nextLayer],
							\duration, durations[nextLayer],
							\amp, amp,
							\rate, rate
						]);

						synth.register;

					});


					playSynths[nextLayer] = synth;

				}, {
					"I8TLooper: layer id not found".postln;
				});

			});

		}

		performStop {|nextLayer|
			["performStop",nextLayer].postln;

			if( nextLayer.notNil, {

				this.stopRecording(nextLayer);

				if( playSynths[nextLayer].isKindOf(Synth), {

					playSynths[nextLayer].release;

				}, {
					"I8TLooper: layer id not found".postln;

				});

			}, {
				playSynths.collect({|playSynth|
					if( playSynth.isKindOf(Synth)) {
						playSynth.release;
					};
				});
			});

		}

		seek {|layer|

			// if no layers selected
			// seek all available layers

				// if layer exists
				// seek it

		}


		trigger {|parameter,value|

			if( value.notNil ) {


				if( value.isKindOf(Event) == false, {
					value = ( val: value, amplitude: 0.5 );
				});

				switch( parameter,

					\amp, {

						this.amp_( value.val );

					},
					\rate, {
						this.rate_( value.val );

					}
				)

			}
		}


		set {|parameter,value|

			if( parameter == \amp ) {
				amp = value;
				this.amp_(amp);
			};


		}


		amp_ {|value,layer|

			if( value.notNil && value != \r ) {

				amp = value.asFloat;

				if( layer.isNil, {

					playSynths.collect({|playSynth|
						playSynth.set( \amp, amp );
					});

				}, {

					if(playSynths[layer].notNil ) {
						playSynths[layer].set( \amp, amp );
					}

				});
			}
		}

		rate_ {|value,layer|
			if( value.notNil && value != \r ) {

				if( layer.isNil, {

					playSynths.collect({|synth|
						synth.set( \rate, value.asFloat );
					});

				}, {

					if(playSynths[layer].notNil ) {
						playSynths[layer].set( \rate, value.asFloat );
					}

				});
				rate = value;
			}
		}

		// .seq shorthands:

		vol {|pattern,index|
			var isPattern = (
				(pattern.isKindOf(String)) || (pattern.isKindOf(Array))
			);

			if( isPattern, {
				["is Pattern", pattern].postln;
				if( index.isNil, {
					^this.seq(\amp,pattern);
				}, {
					^this.seq(\amp,pattern);
				});
			}, {
				var amp = pattern;
				this.amp_(amp,index);
			});
		}


		rate {|pattern,index|
			var isPattern = (
				(pattern.isKindOf(String)==true) || (pattern.isKindOf(Array)==true)
			);

			if( (index.isNil && (isPattern)), {
				^this.seq(\rate,pattern);
			}, {
				var rate = pattern;
				this.rate_(rate,index);
			});
		}




	fx_ {|synthdef|

		super.fx_(synthdef);

		playSynths.collect({|synth|
			if( synth.isKindOf(Synth), {
				if( fxSynth.notNil, {
					synth.moveBefore(fxSynth);
					synth.set(\out,fxBus);
				}, {
					synth.moveBefore(group);
					synth.set(\out,0);
				});
			});
		})
	}

	fxSet {|parameter,value|

		^super.fxSet(parameter,value)

	}


}
