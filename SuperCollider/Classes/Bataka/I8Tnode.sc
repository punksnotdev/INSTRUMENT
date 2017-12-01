I8Tnode : I8TeventListener
{

	classvar <>graph;

	var <>name;
	var <>nodeGraph;

	*new {|name_,graph_|

		if( graph_ != nil, {
			this.initClass(graph_);
			^super.new.init(name_,graph_);
		}, {
			^super.new.init(name_,this.graph);
		})

	}

	*initClass{|graph_|
		graph = graph_;
	}

	init {|name_,nodeGraph_|

		name = name_;
		nodeGraph = nodeGraph_;

		if(nodeGraph!=nil,{ nodeGraph.addNode(this) });

	}

}
