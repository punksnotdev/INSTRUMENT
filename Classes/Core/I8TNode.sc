I8TNode : I8TeventListener
{

	classvar <>graph;

	var <>name;
	var <>input;
	var <>output;
	var <>nodeGraph;

	var <>inputs;
	var <>outputs;

	var <parameters;


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

		parameters = IdentityDictionary.new;

	}

	setContent {|content_| }



	play {}




	setName{|name_|
		name=name_;
	}


	setInput{|input_|
		input=input_;
	}
	setOutput{|output_|
		output=output_;
	}


	addInput{|input_|

		if(inputs.isNil) {
			inputs=();
		};

		inputs[input_.name]=input_;

	}

	addOutput{|output_|

		if(outputs.isNil) {
			outputs=();
		};

		outputs[output_.name]=output_;

	}


	removeInput{|key|
		inputs.removeAt(key);
	}
	removeOutput{|key|
		outputs.removeAt(key);
	}



    doesNotUnderstand {

        arg selector ... args;

		var value = args[0];


        if (selector.isSetter) {

			if( parameters.isNil ) {
				parameters = IdentityDictionary.new;
				parameters[selector.asGetter] = value;
			};

			// TODO: check for  existing params only
			// if( parameters.keys.includes(selector.asGetter) ) {
				parameters[selector.asGetter] = value;
				this.set(selector.asGetter, value);
				^""
			// };


			// ^nil

		};

		if (selector.isKindOf(Symbol)) {
			if( parameters[selector].notNil ) {
				^parameters[selector]
			};
		}
		^nil

    }

}
