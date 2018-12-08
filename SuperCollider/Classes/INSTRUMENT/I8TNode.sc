I8TNode : I8TeventListener
{

	classvar <>graph;

	var <>name;
	var <>nodeGraph;

	var inputs;

	*new {|graph_,name_|

		var newKey=name_;

		if( graph_ != nil, {

			this.initClass(graph_);

			^super.new.init(graph_,newKey);

		}, {


			^super.new.init(this.graph,newKey);
		})

	}

	free {
		nodeGraph.free( this );
	}

	*initClass{|graph_|
		graph = graph_;
	}

	init {|nodeGraph_,name_|
		var newKey;
		newKey = name_;

		if( name_.isNil && nodeGraph_.notNil ) {

			newKey = UniqueID.next//nodeGraph_.nextKey;
		};

		if( name_.isNil && nodeGraph_.isNil ) {
			newKey = UniqueID.next;
		};


		name = newKey;
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
