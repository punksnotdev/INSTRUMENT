I8TSynth : I8TNode {

	var <>synthdef;
	var <>synth;

	*new { arg synthdef, args, target, addAction=\addToHead;

		^super.new.init(graph)

	}

	init { arg graph_, synthdef, args, target, addAction;

		if( this.validateSynth(synthdef) ) {

			synthdef = synthdef;

			switch( addAction,
				\addBefore, {
					synth = Synth.before(
						target,
						synthdef,
						args
					);
					^synth
				}
			);

		};

	}

	*before { arg target, synthdef, args;
		^this.new(synthdef, args, target, \addBefore)
	}


	validateSynth {|synthName|

		if( graph.validateSynthName(synthName) ) {
			var key = synthName.asString.toLower.asSymbol;
			var def = graph.synths.at(key);

			if( def.notNil ) {
				if( graph.validateSynthDef(def) ) {
					^true;
				}
			};
		};

		^false

	}

}
