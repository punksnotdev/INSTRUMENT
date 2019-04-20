I8TChannel : I8TNode
{

	var amp;
	var pan;
	var fxChain;
	var sends;
	var submixAmps;

	var bus;
	var outbus;
	var busSynth;

	var synthGroup;

	*new {|synthGroup_|
		^super.new.init(this.graph,synthGroup_);
	}

	init {|graph_,synthGroup_|

		if( synthGroup_.isKindOf(Group), {
			synthGroup = synthGroup_;
		}, {
			synthGroup = Group.new;
		});


		bus = Bus.audio(Server.local,numChannels:2);

		busSynth = Synth(
			\audioBus,
			[\inBus,bus],
			synthGroup,
			\addToTail
		);
		["Group", synthGroup, busSynth].postln;


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



	getInput {
		^input
	}
	setInput {|input_|
		super.setInput(input_);
		input.set(\outBus,bus);
	}


	getOutbus {
		^outbus
	}
	setOutbus {|outbus_|
		outbus = outbus_;
		busSynth.set(\out,outbus);
	}

	getSynthGroup {
		^synthGroup
	}
	setSynthGroup {|synthGroup_|
		synthGroup.set(\outBus,bus);
	}

	getAmp {
		^amp
	}
	setAmp {|amp_|
		amp = amp_;
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
