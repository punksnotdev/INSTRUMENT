Looper : SynthInstrument
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

			main = graph_;

			super.init(graph_,"looper_"++bus);

		}



		rec {|layer|

			main.sequencer.recLooper( this, layer );
			// nextLayer = layer;
			// this.startRecording(layer);
		}

		start {|layer|
			main.sequencer.startLooper( this, layer );
			// nextLayer = layer;

		}

		stop {|layer|
			main.sequencer.stopLooper( this, layer );
			// nextLayer = layer;
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
				recSynths[nextLayer].free;
				recDuration = TempoClock.default.beats - lastDuration;
				// recDuration = recDuration / TempoClock.default.tempo;
				durations[nextLayer]=recDuration;
				recSynths[nextLayer] = nil;
				["Duration:", recDuration, nextLayer,TempoClock.default.beats, lastDuration].postln;
			};

		}


		recordBuffer{|nextLayer|
			// if buffer for nextLayer not allocated,
			// if( buffers[ nextLayer ].isKindOf(Buffer) == false ) {
				buffers[ nextLayer ] = Buffer.alloc(
					Server.local,
					Server.local.sampleRate * maxDuration,
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

					[nextLayer, synth].postln;


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


[nextLayer, synth].postln;				});

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


		set {|parameter,value|

			if( parameter == \amp ) {
				amp = value;
				this.amp_(amp);
			};


		}


		amp_ {|value,layer|
			if( value.notNil && value != \r ) {

				if( layer.isNil, {

					playSynths.collect({|synth,i|
						["set amp", value,i].postln;
						synth.set( \amp, value );
					});

				}, {

					if(playSynths[layer].notNil ) {
						["set amp", value,layer].postln;
						playSynths[layer].set( \amp, value );
					}

				});
				amp = value;
			}
		}

		rate_ {|value,layer|
			if( value.notNil && value != \r ) {

				value.postln;
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
