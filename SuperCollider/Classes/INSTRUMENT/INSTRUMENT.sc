INSTRUMENT
{

	var <nodes;
	var <rootNode;

	var <>sequencer;

	var <>instrument;

	var <speed;


	*new {
		// rootNode.graph_(this);
		^super.new.init();
	}

	init {

		nodes = Dictionary.new;
		sequencer = Sequencer.new;
		instrument = IdentityDictionary.new;

		rootNode = I8Tnode.new("rootNode",this);

		this.play;

	}

	addNode {|node|
		if( node.name != "rootNode", {
			nodes[node.name] = node;

			if( node.isKindOf(Sequenceable), {
				node.sequencer = sequencer;
				sequencer.registerInstrument(node);
			})

		})
	}

	removeNode {|node|
		if( node.name != "rootNode", {
			nodes[node.name] = node;

			if( node.isKindOf(Sequenceable), {
				node.sequencer = sequencer;
				sequencer.unregisterInstrument(node);
			})

		})
	}

	free {|node|
		if( node.isKindOf(Sequenceable), {
			sequencer.instruments[node.name] = nil;
		});
		nodes[node.name] = nil;
	}

	speed_ {|speed_|
		sequencer.speed = speed_;
	}

	play {
		sequencer.play;
	}
	pause {
		sequencer.pause;
	}
	stop {
		sequencer.stop;
	}

	go {|time=0|

		sequencer.go(time);

	}

	when {|time, function|
		if( ((time.notNil) && ( function.notNil ) && ( function.isKindOf(Function) )),{

			sequencer.singleFunctions[time] = function;

		});
	}
	every {|time, function|
		if( ((time.notNil) && ( function.notNil ) && ( function.isKindOf(Function) )),{

			sequencer.repeatFunctions[time] = function;

		});
	}


}
