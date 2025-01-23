I8TSynth : Sequenceable {

	var <>synthdef;
	var <>synth;

	*new { arg name, synthdef, args, target, addAction=\addToHead;
		^super.new.init(name, graph,synthdef, args, target, addAction)

	}


	*before { arg name, target, synthdef, args;
		^this.new(name, synthdef, args, target, \addBefore)
	}


	free { arg target, synthdef, args;
		if(synth.isKindOf(Synth)) {
			^synth.free;
		}
	}

	set { arg ...args;
		if(synth.isKindOf(Synth)) {
			^synth.set(args[0],args[1]);
		}
	}



	init { arg name_, main_, synthdef, args, target, addAction;

		super.init;

		name = name_;

		if( synthdef.notNil ) {

			parameters = IdentityDictionary.new;

			if( this.validateSynthDef(synthdef) ) {


				synthdef = synthdef;

				switch( addAction,
					\addBefore, {

						var synthdefName;

						if( parameters.notNil ) {

							if( synthdef.isKindOf(SynthDef)
							){
								synthdef.allControlNames.collect({|n|
									parameters[n.name.asSymbol]=n.defaultValue;
								});
							};
							if( synthdef.isKindOf(SynthDefVariant)
							) {
								synthdef.synthdef.allControlNames.collect({|n|
									parameters[n.name.asSymbol]=n.defaultValue;
								});
							};
						};

						synthdefName = synthdef.name.asSymbol;

						synth = Synth.before(
							target,
							synthdefName,
							args
						);

						^this
					}
				);

			};
		};

	}

	validateSynthDef {|synthdef|
		^(graph.validateSynthName(synthdef) || graph.validateSynthDef(synthdef))
	}

	doesNotUnderstand {

		arg selector ... args;

		["doesNotUnderstand: selector", selector].postln;
		["doesNotUnderstand: args", args].postln;



	}


	trigger {|name,event|

		if( name.isKindOf(Symbol) && event.isKindOf(Event) ) {
			if( event.val.notNil ) {
				synth.set(name,event.val);
			}
		}
	}

}
