I8TChannel : Sequenceable
{

	var <inSynth;
	var <inputsSynth;
	var <outSynth;

	var <>sequencer;

	var amp,inAmp,outAmp;
	var pan;

	var <fxChain;

	var eq, locut, compressor;

	var sends;

	var <bus;
	var <>inbus;
	var <>outbus;

	var <>firstOutbus;

	var <inputsBus;

	var synthGroup;

	var sourceListeners;



	*new {|synthGroup_,outbus_,inbus_,eq_=true,compressor_=true,locut_=true|
		^super.new.init(this.graph,synthGroup_,outbus_,inbus_);
	}

	init {|graph_,synthGroup_,outbus_,inbus_,eq_=true,compressor_=true,locut_=true|



		if( graph_.notNil, {
			sequencer = graph_.sequencer;
		});

		if( outbus_.notNil, {

			if( synthGroup_.isKindOf(Group), {
				synthGroup = Group.head(synthGroup_);
			}, {
				synthGroup = Group.head(Server.default.defaultGroup);
			});

			fxChain = I8TFXChain.new;
			fxChain.channel = this;


			this.setupListeners();


			if(inbus_.notNil, {
				inbus=inbus_;
			}, {
				inbus = Bus.audio(Server.local,1);
			});

			bus = Bus.audio(Server.local,1);


			inSynth = Synth.tail(
				synthGroup,
				\audioBus,
				[\inBus,inbus,\outBus,bus]
			);


			if( eq_ === true ) {
				eq = this.setup(\eq);
			};


			if( compressor_ === true ) {
				compressor = this.setup(\compressor);
			};


			if( locut_ === true ) {
				locut = this.setup(\locut);
			};


			outSynth = Synth.tail(
				synthGroup,
				\audioBus,
				[\inBus,bus,\outBus,outbus]
			);


			^this;


		}, {
			// "no outbus"
		});


	}


	setupSequencer {|sequencer_|

		sequencer = sequencer_;

	}

	at {|index|

		// ["at", index].postln;

	}


	put {|index, something|

		// ["put", index, something].postln;

	}



	getBus {
		^bus
	}

	setBus {|bus_|
		bus = bus_;
		if( inSynth.isKindOf(Synth)) {
			inSynth.set(\inBus,bus);
		}
	}



	getInput {
		^input
	}


	setInput {|input_|

		super.setInput(input_);
		input.group=synthGroup;
		input.outbus=inbus;
		input.synth.set(\out,inbus);

	}

	group_ {|group_|
		"channel group CHANGE".postln;
		super.group=group_;
	}

	getOutbus {
		^outbus
	}

	setOutbus {|outbus_|

		outbus = outbus_;

		outSynth.set(\outBus,outbus);

	}

	getInbus {
		^inbus
	}

	setInbus {|inbus_|
		inbus = inbus_;
	}

	getSynthGroup {
		^synthGroup
	}

	setSynthGroup {|synthGroup_|
		if(synthGroup_.isKindOf(Group)) {
			synthGroup=synthGroup_;
		}
	}


	set {|parameter,value|

		if( ( parameter.notNil && value.notNil ) ) {
			if( (value.isKindOf(Number)||value.isKindOf(String)), {

				switch( parameter.asSymbol,
					\amp, {
						this.setAmp(value.asFloat);
						^this
					},
					\pan, {
						this.setPan(value.asFloat);
						^this
					},
					\low, {
						eq.set(parameter.asSymbol,value.asFloat);
						^this
					},
					\middle, {
						eq.set(parameter.asSymbol,value.asFloat);
						^this
					},
					\high, {
						eq.set(parameter.asSymbol,value.asFloat);
						^this
					}
				);

				["Channel: parameter", parameter, "not handled"].postln;

			}, {

				"Set value not a number".warn;

			});
		};

		"Must provide non-nil 'parameter' or 'value'".warn;
	}


	getAmp {
		^amp
	}

	setAmp {|amp_|
		outAmp = amp_;
		outSynth.set(\amp,outAmp)
	}


	getPan {
		^pan
	}

	setPan {|pan_|
		pan = pan_;
		outSynth.set(\pan,pan)
	}

	amp_{|amp_|
		this.setAmp(amp_);
	}
	amp{|amp_|
		^amp;
	}


	getFxChain {
		^fxChain
	}


	setFxChain {|fxChain_|


		if( ((fxChain_===false) || fxChain_.isNil) ) {
			fxChain.collect({|fx,key|
				fx.free;
			});
			fxChain=I8TFXChain.new;
			fxChain.channel = this;

		};

		if( (
			graph.validateFolderName(fxChain_)
			||
			graph.validateSynthName(fxChain_)
			||
			graph.validateSynthDef(fxChain_)
		), {

			fxChain.collect({|fx,key|
				fx.free;
			});

			fxChain = I8TFXChain.new;
			fxChain.channel = this;

			this.addFx(fxChain_);

		}, {

			if( fxChain_.isKindOf(Array), {


				var notValid = fxChain_.reject(
					(
						graph.validateFolderName(_)
						||
						graph.validateSynthName(_)
						||
						graph.validateSynthDef(_)
					)
				);

				if(notValid.size==0, {
					// Task.new({

					fxChain.collect({|fx,key|
						fx.free;
					});


					fxChain = I8TFXChain.new;
					fxChain.channel = this;

					fxChain_.collect({|fx|
						this.addFx(fx);
					});
				}, {
					"Invalid FX Chain".warn;
				})
			});

		});


		^fxChain;

	}

	addFx {|fx_|

		var fx = this.createFxSynthDef(fx_);


		if( (
			fx.synthdef.notNil
			&&
			fx.synthdefKey.notNil
			)
		) {

			this.removeFx(fx.synthdefKey);

			^this.setupFx(fx);

		};

	}


	createFxSynthDef {|fx_|

		var synthdef;
		var synthdefName;
		var synthdefKey;


		if( graph.validateSynthName(fx_), {

			synthdef = graph.synths.at(fx_.asString.uncapitalize.asSymbol);

			if( synthdef.isKindOf(I8TFolder) ) {
				var def = synthdef.values.detect(_.isKindOf(SynthDef));

				// sy	nthdef.values.collect({|v,k| });

				if(def.isNil) {
					var variant = synthdef.values.detect(_.isKindOf(SynthDefVariant));
					def = variant;
					// if(def.notNil) {
					// };
				};
				if(def.notNil) {
					// synthdefName = def.name;
					synthdef=def;
				};

			};

			synthdefName = fx_.asSymbol;

			synthdefKey = fx_.asSymbol;

			if(synthdefName.asString.contains($.)){
				synthdefKey=synthdefName.asString.split($.)[0].asSymbol;
			};

		}, {


			if( graph.validateSynthDef(fx_), {

				if( fx_.isKindOf(SynthDefVariant), {
					synthdef = fx_;
					synthdefName = fx_.name.asSymbol;
					synthdefKey = fx_.name.asSymbol;
					if(synthdefName.asString.contains($.)){
						synthdefKey=synthdefName.asString.split($.)[0].asSymbol;
					};
				},{

					if( fx_.isKindOf(I8TFolder), {

						synthdef = fx_.getMainSynthDef;

						if(synthdef.notNil) {
							synthdefName = synthdef.name;
							synthdefKey = synthdef.name.asSymbol;
						};

					}, {

						if( fx_.isKindOf(SynthDef), {
							synthdef = fx_;
							synthdefName = fx_.name.asSymbol;
							synthdefKey = synthdefName;
						});

					});

				});
			}, {

				if( graph.validateFolderName(fx_) ) {

					var folder = graph.getFolderByName(fx_);

					synthdef = folder.getMainSynthDef;

					if(synthdef.notNil) {
						synthdefName = synthdef.name;
						synthdefKey = synthdef.name.asSymbol;
					};

				};

			});

		});


		^(
			synthdef: synthdef,
			synthdefKey: synthdefKey
		)

	}


	setupFx {|fx_|

		var fxSynthName;
		var fxSynth;

		fxSynthName = (name ++ '_fx_' ++ fx_.synthdefKey);

		fxSynth = I8TSynth.before(
			fxSynthName,
			outSynth,
			fx_.synthdef,
			[\inBus,bus,\outBus,bus]
		);

		fxChain[fx_.synthdefKey.asSymbol] = fxSynth;

		fxSynth.setupSequencer( sequencer );

		^fxSynth;

	}


	removeFx {|key|

		if(
			fxChain[ key ].isKindOf(Synth)
			||
			fxChain[ key ].isKindOf(I8TSynth)
	 	) {
			fxChain[ key ].free;
			fxChain.removeAt( key )
		};
		if( fxChain[ key ].isKindOf(Collection)) {

			fxChain[ key ].collect({|v,k|
				v.free;
				fxChain[key].removeAt( k )
			});

		};
	}

	fx {|key|
		if(key.isNil, {
			^fxChain
		}, {
			if( fxChain[key].isNil, {
				if( graph.validateSynthName(key) ) {
					var fxKey;
					// var synthdefKeys =
					fxChain.keysValuesDo({|k,v|
						if(k.isKindOf(SynthDef)) {
							if( k.name.asSymbol == key ) {
								^v;
							}
						};
						if(k.isKindOf(I8TFolder)) {
							if(k[key].isKindOf(SynthDef)) {
								if(k[key].name==k) {
									^v;
								};
							}
						};
					});

				};
			}, {
				^fxChain[key];
			});
		})
	}

	fxSet {|name,parameter,value|

		if(fxChain[name].isKindOf(Synth)){
			if( (parameter.isKindOf(Symbol)&&value.isKindOf(Number)) ) {

				fxChain[name].set(parameter.asSymbol,value);

			};
		}
	}

	getSends {
		^sends
	}
	setSends {|sends_|
		sends = sends_;
	}
	addToSends {|key,send_|
		//add to sends
	}
	removeFromSends {|key|
		//remove from sends
	}


	toggle {|module, on|

		switch(module,
			\eq, {
				if( on.isNil, {
					if( eq.notNil,
						{ this.free(\eq); },
						{ eq = this.setup(\eq); }
					);
				}, {
					if( on === false ) { this.free(\eq); };
					if( on === true ) { eq = this.setup(\eq); };
				});
			},
			\compressor, {
				if( on.isNil, {
					if( compressor.notNil,
						{ this.free(\compressor); },
						{ compressor = this.setup(\compressor); }
					);
				}, {
					if( on === false ) { this.free(\compressor); };
					if( on === true ) { compressor = this.setup(\compressor); };
				});
			},
			\locut, {
				if( on.isNil, {
					if( locut.notNil,
						{ this.free(\locut); },
						{ locut = this.setup(\locut); }
					);
				}, {
					if( on === false ) { this.free(\locut); };
					if( on === true ) { locut = this.setup(\locut); };
				});
			}
		);

	}



	free {|module|
		switch(module,
			\eq, {
				eq.free; eq=nil;
			},
			\compressor, {
				compressor.free; compressor=nil;
			},
			\locut, {
				locut.free; locut=nil;
			}
		);
	}


	setup {|module|

		switch(module,
			\eq, {

				if( eq.notNil ) {
					eq.free;
					eq = nil;
				};
				^eq = Synth.after(
					inSynth,
					\eq,
					[\inBus,bus,\outBus,bus]
				);
			},
			\compressor, {
				if( compressor.notNil ) {
					compressor.free;
					compressor = nil;
				};
				^compressor = Synth.after(
					inSynth,
					\simpleCompressor,
					[\in,bus,\out,bus]
				);
			},
			\locut, {
				if( locut.notNil ) {
					locut.free;
					locut = nil;
				};
				^locut = Synth.after(
					inSynth,
					\hpf,
					[\in,bus,\out,bus]
				);
			}
		);

	}


	addSource {|source|

		if( source.isKindOf(I8TInstrument)) {
			source = source.channel;
		};

		if( source.isKindOf(I8TChannel) ) {

			var sl;
			var key = source.name;

			sl = this.createSourceListener( source );

			if( sl.isKindOf(Synth) ) {
				sourceListeners[key] = sl;
			};

		};

	}


	removeSource {|source|

		if( source.isKindOf(I8TInstrument)) {
			source = source.channel;
		};

		if( source.isKindOf(I8TChannel) ) {

			var sl;
			var key = source.name;

			sl = sourceListeners[key];

			if( sl.isKindOf(Synth) ) {
				sl.free;
				sourceListeners.removeAt(key);
			};

		};

	}


	setupListeners {

		sourceListeners = IdentityDictionary.new;

		inputsBus = Bus.audio(Server.local,1);

		inputsSynth = Synth.before(
			inSynth,
			\audioBus,
			[\inBus,inputsBus,\outBus,bus]
		);

	}


	createSourceListener{|source|
		if( inputsBus.isKindOf(Bus) ) {
			var synth;

			synth = Synth.after(
				source.outSynth,
				\audioBus,
				[\inBus, source.bus,\outBus,inputsBus]
			);

			^synth;
		};
	}





	send {|targetChannel,connect=true|

        if( targetChannel.isKindOf(I8TChannel) == false ) {
            "Not a valid Channel".warn;
            ^nil
        };

		if( connect == false ) {
	        ^targetChannel.removeSource(this);
		};

        ^targetChannel.addSource(this);

    }

    connect {|targetChannel|

		if( targetChannel.isKindOf(I8TChannel) == false ) {
            "Not a valid Channel".warn;
            ^nil
        };

		firstOutbus=outbus;

		this.setOutbus(targetChannel.inbus);

    }

    disconnect {
		if( firstOutbus.isKindOf(Bus) ) {
	        this.setOutbus(firstOutbus);
		};

    }



	kill {

		this.setFxChain(nil);

		sourceListeners.collect(_.free);

		inSynth.free;
		inputsSynth.free;
		outSynth.free;

		eq.free;
		locut.free;
		compressor.free;


	}

}
