I8TChannel : Sequenceable
{

	var amp;
	var pan;
	var fxChain;
	var sends;
	var submixAmps;

	var <bus;
	var <>outbus;
	var synth;
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

		bus = Bus.audio(Server.local,2);

		synth = Synth.tail(
			synthGroup,
			\audioBus,
			[\inBus,bus]
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
		if( synth.isKindOf(Synth)) {
			synth.set(\inBus,bus);
		}
	}



	getInput {
		^input
	}
	setInput {|input_|
		super.setInput(input_);
		input.group=synthGroup;
		input.outbus=bus;
		input.set(\amp,amp);
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

		synth.set(\outBus,outbus);

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

		if( parameter == \amp, {
			if( value.isKindOf(Number), {
				this.setAmp(value)
			}, {
				"Amp value not a number".warn;
			});
		}, {
			["Channel: parameter", parameter, "not handled"].postln;
		});

	}

	getAmp {
		^amp
	}
	setAmp {|amp_|
		amp = amp_;
		input.set(\amp,amp)
	}


	getPan {
		^pan
	}
	setPan {|pan_|
		pan = pan_;
	}


	getFxChain {
		^fxChain
	}
	setFxChain {|fxChain_|
		fxChain = fxChain_;
	}
	addToFxChain {|key,fxChain_|
		//add to fxChains
	}
	removeFromFxChain {|key|
		//remove from fxChains
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


	getSubmixAmps {
		^submixAmps
	}
	setSubmixAmps {|submixAmps_|
		submixAmps = submixAmps_;
	}
	addToSubmixAmps {|key,submixAmp_|
		//add to submixAmps
	}
	removeFromSubmixAmps {|key|
		//remove from submixAmps
	}

}