InstrumentNode {

	classvar test;
	classvar <>graph;

	var <>nodeGraph;
	var <>name;

	*new {|name_,graph_|

		if( graph_ != nil, {
			this.initClass(graph_);
			^super.new.init(name_,graph_);
		}, {
			^super.new.init(name_,this.graph);
		})
		// nodeGraph.postln;
		^super.new.init(name,graph_);
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
