I8TControllerSpec {

  var <inputs;
  var <outputs;

  var <name;

  *new {|spec|

	  if(
	  	(
            ( spec.name.isKindOf(Symbol) == true)
            ||
            ( spec.name.isKindOf(String) == true)
        )
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

	  name = spec.name.asSymbol;

	  inputs = ();
	  outputs = ();

	  if( spec.inputs.isKindOf(Event) ) {
		  inputs = spec.inputs;
	  };

	  if( spec.outputs.isKindOf(Event) ) {
		  outputs = spec.outputs;
	  };


  }




  getInputGroupByNumber {|type,num|

      switch( type,
          \note, {
              ^inputs.detect({|group| group.channel == num })
          },
          \cc, {
              ^inputs.detect({|group| group.controllers.includes(num) })
          },
      );

  }


  getInput {|type,num|
      ^inputs[type].controllers[num]
  }

  getOutput {|type,num|
      ^outputs[type].controllers[num]
  }






}
