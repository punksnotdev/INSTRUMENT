I8TChannel : Sequenceable
{

	var <inSynth;
	var <outSynth;

	var amp,inAmp,outAmp;
	var pan;

	var fxChain;

	var sends;

	var <bus;
	var <>inbus;
	var <>outbus;

	var synthGroup;


	*new {|synthGroup_|
		^super.new(this.graph,synthGroup_);
	}

	init {|graph_,synthGroup_|


		if( synthGroup_.isKindOf(Group), {
			synthGroup = Group.tail(synthGroup_);
		}, {
			synthGroup = Group.tail(Server.default.defaultGroup);
		});

		fxChain = IdentityDictionary.new;

		bus = Bus.audio(Server.local,2);
		inbus = Bus.audio(Server.local,2);

		outbus=Server.local.outputBus;


		inSynth = Synth.tail(
			synthGroup,
			\audioBus,
			[\inBus,inbus,\outBus,bus]
		);


		fxChain[\eq]
		= Synth.after(
			inSynth,
			\eq,
			[\inBus,bus,\outBus,outbus]
		);


		// fxChain[\lpf] = Synth.tail(
		// 	synthGroup,
		// 	\lpf,
		// 	[\in,bus,\out,bus]
		// );


		// fxChain[\dist] = Synth.tail(
		// 	synthGroup,
		// 	\gateDistort,
		// 	[\in,bus,\out,bus]
		// );

		// fxChain[\comp] = Synth.tail(
		// 	synthGroup,
		// 	\simpleCompressor,
		// 	[\in,bus,\out,bus]
		// );


		outSynth = Synth.tail(
			synthGroup,
			\audioBus,
			[\inBus,bus,\outBus,bus]
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
						this.fxSet(\eq,parameter,value.asFloat);
						^this
					},
					\middle, {
						this.fxSet(\eq,parameter,value.asFloat);
						^this
					},
					\high, {
						this.fxSet(\eq,parameter,value.asFloat);
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
		fxChain = fxChain_;
	}

	addFx {|fx_|
		if( (fx_.isKindOf(Symbol)) ) {
			this.removeFx(fx_);
			fxChain[fx_] = Synth.before(
				outSynth,
				fx_,
				[\inBus,bus,\outBus,outbus]
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



}
