I8TControllerSpec {

  var <inputs;
  var <outputs;

  var <inputMap;
  var <outputMap;

  var <name;

  *new {|spec|

	  if(
	  	( spec.name.isKindOf(Symbol) == true)
		&&
	  	(
			( spec.inputs.isKindOf(Event) == true )
			||
		  	( spec.outputs.isKindOf(Event) == true )
		)
	  ) {
		  ^super.new.init(spec);
	  };

	  "Invalid spec".warn;

  }

  init {|spec|

	  name = spec.name;

	  inputs = ();
	  outputs = ();

	  inputMap = IdentityDictionary.new;
	  outputMap = IdentityDictionary.new;

	  if( spec.inputs.isKindOf(Event) ) {
		  inputs = spec.inputs;
	  };

	  if( spec.outputs.isKindOf(Event) ) {
		  outputs = spec.outputs;
	  };


	  this.createMaps();


  }



  createMaps {

	  inputs.collect({|group,groupKey|
	      group.controllers.keysValuesDo({|k,v|
	          inputMap[v]=(
				  name: group.name,
				  index: k
			  );
	      });
	  });

	  outputs.collect({|group,groupKey|
		  group.controllers.keysValuesDo({|k,v|
			  outputMap[v]=(
				  name: group.name,
				  index: k
			  );
		  });
	  });

  }


  getInputByCtlNum {|key|
	  ^inputMap[key]
  }

  getOutputByCtlNum {|key|
	  ^outputMap[key]
  }






}
