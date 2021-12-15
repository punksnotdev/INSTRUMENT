I8TSynth : Sequenceable {

	var <>synthdef;
	var <>synth;

	*new { arg synthdef, args, target, addAction=\addToHead;
		^super.new.init(graph,synthdef, args, target, addAction)

	}


	*before { arg target, synthdef, args;
		^this.new(synthdef, args, target, \addBefore)
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



	init { arg graph_, synthdef, args, target, addAction;

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

		["selector", selector].postln;
		["args", args].postln;



	}

}
