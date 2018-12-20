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

		if(nodeGraph_!=nil,{
			nodeGraph = nodeGraph_;
			if( name_.isNil ) {
				newKey = UniqueID.next;
			};
		});

		name = newKey;

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
