I8TChannel : I8TNode
{

	var amp;
	var pan;
	var fxChain;
	var sends;
	var submixAmps;

	* new {
		^super.new.init();
	}

	init {

		// "init mixer".postln;

	}



	at {|index|

		// ["at", index].postln;

	}


	put {|index, something|

		// ["put", index, something].postln;

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
