I8TNode : I8TeventListener
{

	classvar <>graph;
	var <main;

	var <>name;
	var <>input;
	var <>output;
	var <>nodeGraph;

	var <>inputs;
	var <>outputs;

	var <parameters;
	var <parameterProxies;


	*new {|main_,name_|

		var newKey=name_;
		
		if( main_ != nil, {

			if( this.graph.isNil ) { this.initClass(main_); };			

			^super.new.init(main_,newKey);

		}, {

			^super.new.init(this.graph,newKey);

		})

	}

	free {
		nodeGraph.free( this );
	}

	*initClass{|main_|
		graph = main_;
	}

	init {|main_,name_|
		var newKey;
		
		newKey = name_;

		if(main_!=nil,{
			nodeGraph = main_;
			main = main_;
			if( name_.isNil ) {
				newKey = UniqueID.next;
			};
		});

		name = newKey;

		parameters = IdentityDictionary.new;
		parameterProxies = IdentityDictionary.new;

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
			if(parameterProxies.isNil) {
				parameterProxies = IdentityDictionary.new;
			};
			if(parameters.isNil) {
				parameters = IdentityDictionary.new;
			};
			if(this.isKindOf(Sequenceable)) {
				if(parameterProxies[selector].isNil) {
					parameterProxies[selector] = I8TParameterProxy(this, selector);
				};
				^parameterProxies[selector];
			};
			if( parameters[selector].notNil ) {
				^parameters[selector]
			};
		}
		^nil

    }

}
