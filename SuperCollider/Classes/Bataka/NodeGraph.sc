NodeGraph
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

		rootNode = I8Tnode.new(\rootNode,this);

	}

	addNode {|node|
		if( node.name != \rootNode, {
			nodes[node.name] = node;
		})
	}

}
