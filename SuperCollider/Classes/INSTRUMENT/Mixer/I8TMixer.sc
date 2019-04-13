I8TMixer : I8TNode
{

	var channelGroups;
	var master;
	var submixes;
	var sends;
	var returns;



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


}
