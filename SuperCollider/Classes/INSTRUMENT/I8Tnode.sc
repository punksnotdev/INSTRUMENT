I8Tnode : I8TeventListener
{

	classvar <>graph;

	var <>name;
	var <>nodeGraph;

	var inputs;

	*new {|name_,graph_|

		if( graph_ != nil, {
			this.initClass(graph_);
			^super.new.init(name_,graph_);
		}, {
			^super.new.init(name_,this.graph);
		})

	}

	free {
		nodeGraph.free( this );
	}

	*initClass{|graph_|
		graph = graph_;
	}

	init {|name_,nodeGraph_|

		name = name_;
		nodeGraph = nodeGraph_;

		if(nodeGraph!=nil,{ nodeGraph.addNode(this) });

		inputs = ();

	}


	inputs_ {|inputs|

		if( inputs.isArray, {

			inputs.collect({|input,index|
				this.addInput( input );
			});

		});

		^this
	}

	addParameter {|name,range|

		if( inputs.parameters == nil, {
			inputs.parameters = ();
		});

		inputs.parameters[name] = (
			name: name,
			range: range
		);

	}

}
