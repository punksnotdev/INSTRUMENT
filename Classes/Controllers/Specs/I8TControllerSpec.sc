I8TControllerSpec {

  var <inputs;
  var <outputs;

  var <inputMap;
  var <outputMap;

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


	  inputMap = this.createMap(inputs);
	  outputMap = this.createMap(outputs);

      outputMap.postln;

  }



  createMap {|source|

      var map = IdentityDictionary.new;

	  source.keysValuesDo({|groupKey,group|
          switch(group.type,
              \note, {
        	      map[('note_'++group.channel).asSymbol]=(
    				  name: groupKey
    			  );
              },
              \cc, {
        	      if(group.controllers.notNil) {
                      group.controllers.keysValuesDo({|k,v|
            	          map[v]=(
            				  name: groupKey,
            				  index: k
            			  );
            	      });
                  };
              },
          )
	  });

      ^map

  }


  getInputByCtlNum {|ctlNum|
	  ^inputMap[ctlNum]
  }

  getOutputByCtlNum {|ctlNum|
	  ^outputMap[ctlNum]
  }


  getInputByChannel {|channel|
	  ^inputMap[('note_'++channel).asSymbol]
  }

  getOutputByChannel {|channel|
	  ^outputMap[('note_'++channel).asSymbol]
  }






}
