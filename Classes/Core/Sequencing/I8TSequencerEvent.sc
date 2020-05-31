SequencerEvent : I8Tevent
{
	classvar <>classSequencer;
	var <sequencer;

	*new{|parameters_, name_|
		^super.new.init( parameters_, name_ );
	}
	init{|parameters_, name_|

		var callback = {|event| ^event; };
		sequencer = classSequencer;
		super.init( sequencer, callback, name_ );

	}

	// *classSequencer_ {|sequencer_|
	// 	classSequencer = sequencer_;
	// }

}
