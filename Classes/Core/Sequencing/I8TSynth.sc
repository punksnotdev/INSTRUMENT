I8TSynth : Sequenceable {

	var <>synthdef;
	var <>synth;
	var <>userParams;

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
			if(userParams.isNil) { userParams = IdentityDictionary.new };
			userParams[args[0]] = args[1];
			^synth.set(args[0],args[1]);
		}
	}

	reset {|param|
		if(param.notNil) {
			if(userParams.notNil) { userParams.removeAt(param) };
			if(parameters[param].notNil && synth.isKindOf(Synth)) {
				synth.set(param, parameters[param]);
			};
		} {
			if(userParams.notNil) {
				userParams.keysValuesDo({|k,v|
					if(parameters[k].notNil && synth.isKindOf(Synth)) {
						synth.set(k, parameters[k]);
					};
				});
			};
			userParams = IdentityDictionary.new;
		};
	}

	clear {|param| this.reset(param) }



	init { arg name_, main_, synthdef, args, target, addAction;

		super.init;

		name = name_;
		userParams = IdentityDictionary.new;

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


						main_.server.bind { 
							synth = Synth.new(
								synthdefName,
								args,
								target,
								\addBefore
							);
						};


						main = main_;

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
