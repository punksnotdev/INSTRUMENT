I8TLooper : SynthInstrument
{


		var <buffers;
		var maxDuration;
		var numChannels;

		var <>bus;
		var <recSynth;
		var <playSynths;
		var <durations;
		var lastDuration;
		var main;

		var rate;

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
			durations = IdentityDictionary.new;

			maxDuration = 60;
			numChannels = 1;

			main = graph_;

			super.init(graph_,"looper_"++bus);

		}



		rec {|layer|
			main.sequencer.postln;
			this.startRecording(layer);
		}

		startRecording {|layer|
			if( layer.notNil, {
				// if buffer for layer not allocated,
				if( buffers[ layer ].isKindOf(Buffer) == false ) {
					buffers[ layer ] = Buffer.alloc( Server.local, Server.local.sampleRate * maxDuration, 1,
						{|buffer|
							recSynth = Synth(\loopWrite, [
								\inBus, bus,
								\buffer, buffer
							]);
							lastDuration = TempoClock.default.beats;
						});
				};




			}, {

				// if buffer for layer not allocated,
				if( buffers[ buffers.size ].isKindOf(Buffer) == false ) {
					buffers[ buffers.size ] = Buffer.alloc( Server.local, Server.local.sampleRate * maxDuration, 1, {|buffer|
						recSynth = Synth(\loopWrite, [
							\inBus, bus,
							\buffer, buffer
						]);
						lastDuration = TempoClock.default.beats;
					});
				};



			});
		}

		delete {|layer|
			// if no layers selected
			// delete all available layers

				// if layer exists
				// delete it
		}


		stopRecording {


			if( recSynth.isKindOf(Synth)) {
				var recDuration;
				recSynth.free;
				recDuration = TempoClock.default.beats - lastDuration;
				// recDuration = recDuration / TempoClock.default.tempo;
				durations[buffers.size-1]=recDuration;
				recSynth = nil;
				["Duration:", recDuration,buffers.size-1,TempoClock.default.beats, lastDuration;].postln;
			};

		}

		start {|layer|

			this.stopRecording();

			// if no layers selected
			if( layer.isNil, {
				// play all available layers:
				// first stop all
				playSynths.collect({|synth|
					if( synth.isKindOf(Synth) ) {
						synth.release;
					};
				});
				// then create new synths:
				buffers.collect({|buffer,key|
					if( buffer.isKindOf(Buffer) ) {

						var synth;

						if( fxSynth.isKindOf(Synth), {

							synth = Synth.before( fxSynth, \loopRead,
								[
								\out,fxBus,
								\buffer, buffer,
								\duration, durations[key],
								\amp, amp,
								\rate, rate
							]);
							synth.register;
						}, {

							synth = Synth.head( group, \loopRead,[
								\buffer, buffer,
								\duration, durations[key],
								\amp, amp,
								\rate, rate
							]);

							synth.register;

						});


						playSynths[key]=synth;

					};
				});
			}, {

				// if layer exists
				if( buffers[layer].isKindOf(Buffer), {


					var synth;

					// play it:
					// first stop it if running

					if( playSynths[layer].isKindOf(Synth)) {
						playSynths[layer].release;
					};
					// then create new synth:

					if( fxSynth.isKindOf(Synth), {

						synth = Synth.before( fxSynth, \loopRead,
							[
							\out,fxBus,
							\buffer, buffers[layer],
							\duration, durations[layer],
							\amp, amp,
							\rate, rate
						]);


						synth.register;
					}, {

						synth = Synth.head( group, \loopRead,[
							\buffer, buffers[layer],
							\duration, durations[layer],
							\amp, amp,
							\rate, rate
						]);

						synth.register;

					});

					playSynths[layer] = synth;


				}, {
					"I8TLooper: layer id not found".postln;
				});

			});

		}

		stop {|layer|


			this.stopRecording();


			// if no layers selected
			if( layer.isNil, {
				// stop all available layers
				buffers.collect({|buffer,key|
					if( buffer.isKindOf(Buffer) ) {
						playSynths[key].release;
					};
				});
			}, {

				playSynths.collect({|synth|
					if(synth.isKindOf(Synth)) {
						synth.release;
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
						"set rate".postln;
						this.rate_( value.val );

					}
				)

			}
		}


		amp_ {|value,layer|
			if( value.notNil && value != \r ) {
				if( layer.isNil, {

					playSynths.collect({|synth|
						synth.set( \amp, value.asFloat );
					});

				}, {

					if(playSynths[layer].notNil ) {
						playSynths[layer].set( \amp, value.asFloat );
					}

				});
				amp = value;
			}
		}

		rate_ {|value,layer|
			if( value.notNil && value != \r ) {
				if( layer.isNil, {

					playSynths.collect({|synth|
						synth.postln;
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

		amp  {|pattern,index|
			var isPattern = (
				(pattern.isKindOf(String)==true) || (pattern.isKindOf(Array)==true)
			);

			if( (index.isNil && (isPattern)), {
				^this.seq(\amp,pattern);
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

}
