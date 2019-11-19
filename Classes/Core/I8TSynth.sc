I8TSynth : I8TNode {

	var <>defName;
	var <>synth;

	*new { arg defName, args, target, addAction=\addToHead;

		^super.new.init(graph)

	}

	init { arg graph_, defName, args, target, addAction;

		defName = defName;
		switch( addAction,
			\addBefore, {
				synth = Synth.before(
					target,
					defName,
					args
				);
				^synth
			}
		);

	}

	*before { arg target, defName, args;
		^this.new(defName, args, target, \addBefore)
	}

}
