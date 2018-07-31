INSTRUMENT
{

	var <nodes;
	var <rootNode;

	var <>sequencer;

	*new {
		// rootNode.graph_(this);
		^super.new.init();
	}

	init {

		nodes = Dictionary.new;
		sequencer = Sequencer.new;

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

	free {|node|
		if( node.isKindOf(Sequenceable), {
			sequencer.instruments[node.name] = nil;
		});
		nodes[node.name] = nil;
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
}
