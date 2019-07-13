SynthPlayer : SynthInstrument
{

	var nodeID;
	var nodeIDs;
	var synthdef;
	var <>mode;

	var synth_parameters;

	var pressedKeys;
	var currentPressedKey;
	var lastPressedKey;

	var autostart;

	var creatingSynth;


	*new{|synthdef_,name_|
		^super.new.init(this.graph,synthdef_,name_);
	}

	init{|graph_,synthdef_,name_|

		nodeIDs=IdentityDictionary.new;

		mode = \poly;

		if( synthdef_.notNil, {
			// [name_,synthdef_].postln;

			if(synthdef_.isKindOf(Symbol), {
				synthdef = synthdef_;
			},{
				synthdef = \test;
			});

			// this.createSynth([\out,outbus]);
			synth_parameters = IdentityDictionary.new;
			super.init(graph_,name_);

		});


		pressedKeys = IdentityDictionary.new;


		creatingSynth = false;


	}

	setContent {|synthplayer_|


		if(synthplayer_.isKindOf(SynthPlayer), {
			synthdef = synthplayer_.synthdef;
		},{
			synthdef = \test;
		});
	}


	synthdef_{|synthdef_|

		synthdef = synthdef_;
		// if( synth_parameters.isKindOf(IdentityDictionary) == false, {
		// 	synth_parameters=IdentityDictionary.new;
		// });

		// this.createSynth([\out,outbus]);

		^synthdef

	}

	createSynth{|parameters|

		if( synthdef != \r, {

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

				synth = Synth.before( fxSynth, synthdef.asSymbol, parameters++[\out,fxBus] );
				synth.register;

			}, {
				synth = Synth.head( group, synthdef.asSymbol, parameters );

				synth.register;

			});

			nodeID = synth.nodeID;

			nodeIDs[nodeID]=true;


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

					if( value.val != \r ) {

						synthdef = value.val;

					}
					// synth_parameters = IdentityDictionary.new;
				},
				\octave, { octave = value.val },
				\fx, {
					this.fx_( value.val );

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


					if(noteStrings.includes(event.val.asString[0].asSymbol)==true, {
						var notes = (
							'C': 0,
							'Db': 1,
							'D': 2,
							'Eb': 3,
							'E': 4,
							'F': 5,
							'Gb': 6,
							'G': 7,
							'Ab': 8,
							'A': 9,
							'Bb': 10,
							'B': 11,
						);

						note = notes[event.val.asSymbol];

					}, {
						note = event.val.asFloat;
					});


					if( (note > 0) && (note < 128) ) {

						if( event.amplitude.isNil ) {
							amp = 0.5;
						};

						if( event.val != \r ) {

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
											\freq,((octave*12)+note).midicps,
											\note,(octave*12)+note,
											\amp, amp,
											\out, outbus
											]++this.parameters_array(use_synth_parameters)
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
													\freq,((octave*12)+note).midicps,
													\note,(octave*12)+note,
													\amp, amp,
													\legato,0,
													\out, outbus
													]++this.parameters_array(use_synth_parameters)
												);

											});


										}, {

											if( synth.isKindOf(Synth) ) {
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

											if( synth.isKindOf(Synth) ) {

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
						this.createSynth([\t_trig,1,\amp,amp,\out,outbus]++this.parameters_array(use_synth_parameters));
					}
				},
				// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value.val]); },
				// \chord, {
				// 	// ["chord",value].postln;
				// 	// proxy.setn(\notes,(octave*12)+value,\freqs,((octave*12)+value).midicps,\t_trig,1);
				// },
				{ // default:

					synth_parameters[parameter.asSymbol]=value.val;

					if( value.val.isNil || value.val == 0, {}, {
						if( synth.isKindOf(Synth) ) {
							synth.set(parameter.asSymbol,value.val.asFloat);
						}
					});
				},


			);


		}
	}



	set {|parameter,value|


		switch( parameter,
		 \note, {
			this.trigger( parameter, value );
		},
		\low, {
			["implement low",mixerChannel].postln;
			synth_parameters[\low] = value;
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

	amp_ {|value|
		if( value.notNil ) {

			synth_parameters[\amp] = value;
			synth.set( \amp, value );
		}
	}

	amp {|value|
		if( value.notNil ) {
			synth_parameters[\amp] = value;
			synth.set( \amp, value );
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
