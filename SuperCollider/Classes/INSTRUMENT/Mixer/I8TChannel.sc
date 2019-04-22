I8TChannel : I8TNode
{

	var amp;
	var pan;
	var fxChain;
	var sends;
	var submixAmps;

	var <bus;
	var <>outbus;
	var busSynth;

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

		busSynth = Synth.tail(
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
		if( busSynth.isKindOf(Synth)) {
			busSynth.set(\inBus,bus);
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

		busSynth.set(\outBus,outbus);

	}

	getSynthGroup {
		^synthGroup
	}
	setSynthGroup {|synthGroup_|
		if(synthGroup_.isKindOf(Group)) {
			synthGroup=synthGroup_;
		}
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
