I8TChannel : Sequenceable
{

	var <inSynth;
	var <outSynth;

	var amp,inAmp,outAmp;
	var pan;

	var fxChain;

	var eq, locut, compressor;

	var sends;

	var <bus;
	var <>inbus;
	var <>outbus;

	var synthGroup;


	*new {|synthGroup_,outbus_,inbus_,eq_=true,compressor_=true,locut_=true|
		^super.new(this.graph,synthGroup_,outbus_,inbus_);
	}

	init {|graph_,synthGroup_,outbus_,inbus_,eq_=true,compressor_=true,locut_=true|


		if( synthGroup_.isKindOf(Group), {
			synthGroup = Group.tail(synthGroup_);
		}, {
			synthGroup = Group.tail(Server.default.defaultGroup);
		});

		fxChain = IdentityDictionary.new;

		bus = Bus.audio(Server.local,2);

		if(outbus_.notNil, {
			outbus=outbus_;
		}, {
			outbus=Server.local.outputBus;
		});

		if(inbus_.notNil, {
			inbus=inbus_;
		}, {
			inbus = Bus.audio(Server.local,2);
		});


		inSynth = Synth.tail(
			synthGroup,
			\audioBus,
			[\inBus,inbus,\outBus,bus]
		);


		if( eq_ === true ) {
			eq = this.setup(\eq);
		};

		if( locut_ === true ) {
			locut = this.setup(\locut);
		};

		if( compressor_ === true ) {
			compressor = this.setup(\compressor);
		};


		outSynth = Synth.tail(
			synthGroup,
			\audioBus,
			[\inBus,bus,\outBus,outbus]
		);



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

				["set",parameter.asSymbol,value].postln;

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
		if( fxChain_.isNil ) {
			fxChain.collect({|fx,key|
				fx.free;
				fxChain.removeAt(key);
			});
			^nil;
		};
		if( fxChain_.isKindOf(IdentityDictionary), {
			fxChain = fxChain_;
		}, {
			if( fxChain_.isKindOf(Collection), {
				var notValid = fxChain_.reject(
					(_.isKindOf(String)||_.isKindOf(Symbol))
				);
				if(notValid.size==0, {

					fxChain.collect({|fx,key|
						fx.free;
						fxChain.removeAt(key);
					});

					fxChain = IdentityDictionary.new;
					fxChain_.collect({|fx|
						this.addFx(fx);
					});
				}, {
					"Invalid FX Chain".warn;
				})
			});
		});
	}

	addFx {|fx_|
		if( (fx_.isKindOf(Symbol)) ) {
			this.removeFx(fx_);
			fxChain[fx_] = Synth.before(
				outSynth,
				fx_,
				[\inBus,bus,\outBus,bus]
			);
		}
	}
	removeFx {|fx_|
		if( fxChain[ fx_ ].isKindOf(Synth)) {
			fxChain[ fx_ ].free;
			fxChain.removeAt( fx_ )
		};
	}

	fx {|name|
		^fxChain[name]
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
				^compressor = Synth.tail(
					synthGroup,
					\simpleCompressor,
					[\in,bus,\out,bus]
				);
			},
			\locut, {
				if( locut.notNil ) {
					locut.free;
					locut = nil;
				};
				^locut = Synth.tail(
					synthGroup,
					\hpf,
					[\in,bus,\out,bus]
				);
			}
		);

	}



}
