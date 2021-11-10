SynthPlayer : SynthInstrument
{

	var nodeID;
	var nodeIDs;
	var <>mode;

	var synth_parameters;

	var pressedKeys;
	var currentPressedKey;
	var lastPressedKey;

	var autostart;

	var creatingSynth;


	*new{|synthdef_,name_|
		if(
			(
				synthdef_.isKindOf(SynthDef)
				||
				synthdef_.isKindOf(SynthDefVariant)
			)
		, {

			var instance;
			instance = super.new(synthdef_.name.asSymbol);
			^instance.init(this.graph,synthdef_,synthdef_.name.asSymbol);

		}, {

			^nil;

		});
	}

	init{|graph_,synthdef_,name_|

		nodeIDs=IdentityDictionary.new;

		mode = \poly;

		if( synthdef_.notNil, {

			if((
				synthdef_.isKindOf(SynthDef)
				||
				synthdef_.isKindOf(SynthDesc)
				||
				synthdef_.isKindOf(SynthDefVariant)
			)) {
				synthdef = synthdef_;
				name = synthdef.name.asSymbol;
			};

			if( synthdef_.isKindOf(SynthDefVariant) ) {
				synth_parameters=synth_parameters++synthdef_.parameters;
			};

			if(
				(
					synthdef_.isKindOf(Symbol)
					||
					synthdef_.isKindOf(String)
				)
			) {
				 if(graph.synths[synthdef_].notNil, {

					 synthdef = graph.synths[synthdef_.asSymbol];
					 name = synthdef_.asSymbol;

				 }, {

					 ("SynthDef "++synthdef_++" doesn't exit in Library").warn;

				 });

			};

			// this.createSynth([\out,outbus]);
			synth_parameters = IdentityDictionary.new;

			^super.init(graph_,name);

		});


		pressedKeys = IdentityDictionary.new;


		creatingSynth = false;


	}



	synthdef_{|synthdef_|

		synthdef = synthdef_;
		// if( synth_parameters.isKindOf(IdentityDictionary) == false, {
		// 	synth_parameters=IdentityDictionary.new;
		// });

		// this.createSynth([\out,outbus]);

		^synthdef

	}

	createSynth {|parameters|

		if( synthdef.notNil, {

			var s = Server.local;

			// if( synths.isKindOf(List), {
				// clean dead synths' id
					var removeKey;

					synths.collect({|synth_,key|
						if( synth_.isPlaying == false, {
							nodeIDs[synth_.nodeID]=false;
							nodeID=synth_.nodeID;
							removeKey = key;
						});
					});
					if( removeKey.notNil,{
						synths.removeAt( removeKey );
					}, {
						nodeID = nil;
					});
				// });

				if( nodeID.isNil, {

					var idIndex = nil;

					nodeIDs.collect({|id_set,key|
						if( id_set==false, { idIndex=key });
					});

					if(idIndex.notNil, {
						nodeID = idIndex;
					}, {
						nodeID = s.nextNodeID;
					});

				});


				// parameters=parameters++[\clock,main.clock];

			if( fxSynth.isKindOf(Synth), {
				synth = Synth.before( fxSynth, synthdef.name.asSymbol, parameters++[\out,fxBus] );
				synth.register;
			}, {

				synth = Synth.head( group, synthdef.name.asSymbol, parameters );

				synth.register;

			});

			nodeID = synth.nodeID;

			nodeIDs[nodeID]=true;


		}, {
			"SynthDef is nil".warn;
		});

		creatingSynth = false;


	}


	trigger {|parameter,value|

		if( value.notNil ) {


			if( value.isKindOf(Event) == false, {
				value = ( val: value, amplitude: 0.5 );
			});

			switch( parameter,

				\synthdef, {

					if( ( (value.val != \r) || (value.val != nil ) ) ) {
						// TODO: Check that this is working:
						synthdef = SynthDescLib.global.at(value.val);
						// main.postln;
					}
					// synth_parameters = IdentityDictionary.new;
				},
				\octave, { octave = value.val },
				\fx, {

					this.fx( value.val );

				},
				\fxSet, {

					value.keysValuesDo({|k,v|
						fx_parameters[k]=v;
						fxSynth.set(k,v);
					});
				},
				\note, {
					// if is Event, get params
					var event = value;
					var amp = event.amplitude;
					var use_synth_parameters;
					var note;
					var noteStrings = ['A','B','C','D','E','F','G'];

					// play comma-separated chords:

					// play I8TChords:
					if(event.val.isKindOf(String)) {

						if(event.val.includes($,)) {
							var chord;
							chord = event.val.split($,);
							chord.removeAt(0);
							if(chord.isKindOf(Array)) {
								chord.do({|n,i|
									var nE = event.copy;
									nE.val=n;
									if( i<(chord.size-1)) {
										nE.duration = 0;
									};
									this.trigger(\note,nE)
								});
							};
						};

					};

					if(event.val.isKindOf(Array)) {
						var chord = event.val.copy;
						["chord", chord, event.duration].postln;
						chord.do({|n,i|
							var nE = event.copy;
							nE.val=n;
							["n", n].postln;

							if( i<(chord.size-1)) {
								nE.duration = 0;
							};
							this.trigger(\note,nE)
						});
					};

					if( ( (event.val.isKindOf(Array)==false) && (event.val != \r) && (event.val != nil ) ) ) {

						if(noteStrings.includes(event.val.asString[0].asSymbol)==true, {
							var notes = (
								'Cb': -1,
								'C': 0,
								'C#': 1,
								'Db': 1,
								'D': 2,
								'D#': 3,
								'Eb': 3,
								'E': 4,
								'E#':5,
								'F': 5,
								'Gb': 6,
								'G': 7,
								'G#': 8,
								'Ab': 8,
								'A': 9,
								'A#': 10,
								'Bb': 10,
								'B': 11,
								'B#': 12
							);



							var noteNumber = 4;
							var noteName = "";


							event.val.do({|c|
								if(c.isDecDigit, {
									noteNumber = c.asString.asInteger;
								}, {
									noteName = noteName ++ c.asString;
								});
							});

							note = notes[noteName.asSymbol];

							if( note.notNil, {

								note = (note + ((noteNumber-4)*12)).asInteger;

							}, {
								note = 0;
							});

						}, {
							note = event.val.asFloat.min(128);
						});

						note = (octave*12)+note;



						if( (note >= 0) && (note < 128) ) {

							if( event.amplitude.isNil ) {
								amp = 0.5;
							};

							use_synth_parameters = synth_parameters;

							if( ((synth_parameters.notNil) && (synth_parameters[\amp].notNil)), {
								var computed_params;
								amp = amp * synth_parameters[\amp];

								computed_params = synth_parameters.copy;
								computed_params.removeAt(\amp);
								use_synth_parameters = computed_params;
							});

							if( amp.asFloat > 0, {

								switch(mode,

									\poly, {

										this.createSynth([
											\t_trig,1,
											\freq,(note).midicps,
											\note,note,
											\amp, amp,
											\out, outbus
											]++this.createParametersArray(use_synth_parameters)
										);

									},

									\mono, {

										if( synth.isKindOf(Synth), {

											if( synth.isPlaying == false, {
												synth = nil;
											});
										});

										pressedKeys[note] = true;

										if( synth.isNil , {
											if( creatingSynth == false, {

												creatingSynth = true;

												this.createSynth([
													\t_trig,1,
													\freq,(note).midicps,
													\note,note,
													\amp, amp,
													\legato,0,
													\out, outbus
													]++this.createParametersArray(use_synth_parameters)
												);

											});


										}, {

											if( (synth.isKindOf(Synth) && synth.isPlaying)) {
												synth.set(\amp,amp);
												synth.set(\gate,1);

												if(pressedKeys.size==1, {

													synth.set(\legato,0);

												}, {

													synth.set(\legato,use_synth_parameters[\legato]);

												});

												synth.set(\freq,note.midicps);

											}

										});

									}
								);


							}, { // note off


								switch( mode,

									\mono, {

										pressedKeys.removeAt(note);

										if(pressedKeys.size<=0, {

											if(( synth.isKindOf(Synth) && synth.isPlaying)) {

												synth.set(\gate,0);
												creatingSynth = false;
												pressedKeys = IdentityDictionary.new;
											}

										});

									}
								);

							});

						}


					}


				},
				// \speed, {
				//
				// 	if(
				// 		( value.val.asFloat > 0 )
				// 		&&
				// 		( value.val.asFloat != \r )
				// 	) {
				// 		this.clock_( value.val.asFloat );
				// 	}
				//
				// },
				\trigger, {
					var floatValue = value.val.asFloat;
					if( floatValue.asFloat > 0 ) {

						var amp = floatValue;
						var use_synth_parameters;
						use_synth_parameters = synth_parameters;
						// ["should create synth", floatValue.isKindOf(String),floatValue>0].postln;


						if( ((synth_parameters.notNil) && (synth_parameters[\amp].notNil)), {
							var computed_params;
							amp = amp * synth_parameters[\amp];
							computed_params = synth_parameters.copy;
							computed_params.removeAt(\amp);
							use_synth_parameters = computed_params;
						});
						this.createSynth([\t_trig,1,\amp,amp,\out,outbus]++this.createParametersArray(use_synth_parameters));
					}
				},
				// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value.val]); },
				// \chord, {
				// 	// ["chord",value].postln;
				// 	// proxy.setn(\notes,(octave*12)+value,\freqs,((octave*12)+value).midicps,\t_trig,1);
				// },
				{ // default:


					// if( (value.val.notNil && (value.val != 0) && (value.val.asSymbol !=\r)), {
					if( (value.val.notNil && (value.val !=\r)), {

						synth_parameters[parameter.asSymbol]=value.val.asFloat;


						if( synth.isKindOf(Synth) && synth.isPlaying ) {
							synth.set(parameter.asSymbol,value.val.asFloat);
						}

					});
				},


			);


		}
	}



	set {|parameter,value|
		if( [\low,\middle,\high].includes(parameter.asSymbol)) {
			super.set(parameter,value);
			synth_parameters[parameter] = value;
			^value;
		};

		if( ( (value != nil) && (value != \r) )) {

			switch( parameter,
			 \note, {
				this.trigger( parameter, value );
			},
			{
				synth_parameters[parameter] = value;
			});

			if( synth.isKindOf(Synth) ) {
				if( synth.isPlaying ) {
					synth.set( parameter, value );
				}
			}

		}
	}

	amp_ {|value|
		if( value.notNil ) {

			synth_parameters[\amp] = value;
			if( synth.isPlaying ) {
				synth.set( \amp, value );
			};
		}
	}

	amp {|value|
		if( value.notNil ) {
			synth_parameters[\amp] = value;
			if( synth.isPlaying ) {
				synth.set( \amp, value );
			};
		};
		^amp;
	}

	stop {
		if( mode == \mono, {
			synth.release;
		});
		super.stop();
	}




	synthdef {|pattern|
		if(pattern.notNil, {
			^this.seq(\synthdef,pattern);
		}, { ^synthdef });
	}


	autostart {
		this.start();

		super.autostart();

	}

	start {
		this.createSynth([\out,outbus]);
	}


}
