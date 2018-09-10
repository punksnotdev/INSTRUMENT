SynthPlayer : Instrument
{
	var group;
	var groupID;

	var nodeID;
	var nodeIDs;
	var <synthdef;

	var synth_parameters;
	var fx_parameters;

	var <fxSynth;
	var <fx;
	var fxBus;

	*new{|name_,synthdef_|
		^super.new.init(name_,this.graph,synthdef_);
	}

	init{|name_,graph_,synthdef_|

		nodeIDs=IdentityDictionary.new;

		group = Group.new;
		group.register;
		groupID = group.nodeID;

		if( name_.notNil && synthdef_.notNil, {
			// [name_,synthdef_].postln;

			if(synthdef_.isKindOf(Symbol), {
				synthdef = synthdef_;
			},{
				synthdef = \test;
			});

			// this.createSynth();
			fxSynth = nil;
			fxBus = Bus.audio(Server.local,2);
			synth_parameters = IdentityDictionary.new;
			fx_parameters = IdentityDictionary.new;
			super.init(name_,graph_);

		});
	}

	synthdef_{|synthdef_|

		synthdef = synthdef_;
		// if( synth_parameters.isKindOf(IdentityDictionary) == false, {
		// 	synth_parameters=IdentityDictionary.new;
		// });

		this.createSynth();

		^synthdef

	}

	createSynth{|parameters|

		if( synthdef != \r, {

			var s = Server.local;

				// clean dead synths' id
				// if( synths.isKindOf(List), {
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

			if( fxSynth.isKindOf(Synth), {
				synth = Synth.before( fxSynth, synthdef.asSymbol, [\out,fxBus]++parameters );
				synth.register;
			}, {

				synth = Synth.basicNew( synthdef.asSymbol, s, nodeID );
				synth.register;
				synths.add(synth);
				nodeIDs[nodeID]=true;
				// [[\out,fxBus]++parameters].postln;
				// s.sendBundle(0,synth.addToHeadMsg(group, [\freq,300]));
				s.sendBundle(0,synth.addToHeadMsg(group, parameters));

			});
			nodeID = synth.nodeID;
			
		});


	}

	parameters_array{|array|
		var parameters_array = List.new;

		array.keysValuesDo({|key,value|
			parameters_array.add(key.asSymbol);
			parameters_array.add(value);
		})

		^parameters_array
	}

	trigger {|parameter,value|

		if( value.isKindOf(Event) == false, {
			value = ( val: value, amplitude: 0.5 );
		});

		switch( parameter,

			\synthdef, {
				synthdef = value.val;
				// synth_parameters = IdentityDictionary.new;
			},
			\octave, { octave = value.val },
			\fx, {

				this.fx = value.val;

			},
			\setFx, {
				value.val.keysValuesDo({|k,v|
					fx_parameters[k]=v;
					fxSynth.set(k,v);
				});
			},
			\note, {
				// if is Event, get params
				var event = value;
				var amp = event.amplitude;

				if( ((synth_parameters.notNil) && (synth_parameters[\amp].notNil)), {
					var computed_params;
					amp = amp * synth_parameters[\amp];

					computed_params = synth_parameters.copy;
					computed_params.removeAt(\amp);

					this.createSynth([
						\t_trig,1,
						\freq,((octave*12)+event.val).midicps,
						\note,(octave*12)+event.val,
						\amp, amp
						]++this.parameters_array(computed_params));
				}, {
					this.createSynth([
						\t_trig,1,
						\freq,((octave*12)+event.val).midicps,
						\note,(octave*12)+event.val,
						\amp, amp
						]++this.parameters_array(synth_parameters));
				});



			},
			\ampTrig, {
				if( value.val > 0 ) {

					var amp = value.val;

					if( ((synth_parameters.notNil) && (synth_parameters[\amp].notNil)), {
						var computed_params;
						amp = amp * synth_parameters[\amp];

						computed_params = synth_parameters.copy;
						computed_params.removeAt(\amp);

						this.createSynth([\t_trig,1,\amp,amp]++this.parameters_array(computed_params));

					}, {
						this.createSynth([\t_trig,1,\amp,amp]++this.parameters_array(synth_parameters));
					});

				}
			},
			// \t_trig, { this.createSynth([\t_trig,1,\note,(octave*12)+value.val]); },
			\chord, {
				// synth.set(\t_trig,1,\note,(octave*12)+value.val);
			},
			{ // default:
				synth_parameters[parameter.asSymbol]=value.val;
				if( value.val.isNil || value.val == 0, {}, { synth.set(parameter.asSymbol,value.val) });
			},


		);


	}

	fx_{|synthdef_|

		var fx;


		if( synthdef_.notNil,{
			if( fxSynth.notNil, {
				fxSynth.free;
				// fxSynth = Synth.replace(fxSynth,synthdef_);
			}, {
				// fxSynth = Synth.new(synthdef_);
			});

			fxSynth = Synth.new(synthdef_,[\inBus,fxBus]++this.parameters_array(fx_parameters));

		}, {
			"clear currentFX".postln;
			fxSynth.free;
			fxSynth = nil;
		});

		^fxSynth;

	}



	fxSynth_{|synthdef_|
		this.fx(synthdef_);
	}

	setFx{|parameter,value|
		fx_parameters[parameter] = value;
		fxSynth.set(parameter,value);
	}

	set {|parameter,value|

		synth_parameters[parameter] = value;
		// synth.set( parameter, value );
	}


}
