Proxy : I8TInstrument
{

	var <proxy;
	var amp;
	var defaultParameters;

	*new{|proxy_|
		^super.new.init(this.graph,proxy_);
	}

	init{|main_,proxy_|
		if( proxy_.isKindOf(NodeProxy), {
			proxy_.postln;
			proxy = proxy_;
			defaultParameters = this.extractDefaultParameters(proxy_);
			sequencer = main_.sequencer;
			// this.createSynth();
			super.init(main_,proxy_.key,);
		},{
			"input not a nodeproxy".postln;
		});

	}

	setContent {|proxy_|

		if( proxy_.isKindOf(NodeProxy), {
			proxy = proxy_.proxy;
			defaultParameters = this.extractDefaultParameters(proxy_);
		})
	}

	proxy_{|proxy_|

		proxy = proxy_;
		defaultParameters = this.extractDefaultParameters(proxy_);

		^proxy

	}

	isNumericString {|str|
		var hasDigit = false;
		var ok = true;
		if(str.isKindOf(String) == false) { ^false; };
		str.do({|ch, i|
			if(ch.isDecDigit) {
				hasDigit = true;
			} {
				if((ch == $.) || (ch == $-) || (ch == $+)) {
					if(((ch == $-) || (ch == $+)) && (i != 0)) {
						ok = false;
					};
				} {
					ok = false;
				};
			};
		});
		^(ok && hasDigit);
	}

	coerceNumericValue {|parameter, value|
		var v = value;
		if(v.isKindOf(String)) {
			if(this.isNumericString(v)) {
				v = v.asFloat;
			};
		};
		^v
	}


	trigger {|parameter,event|
		var value = event;
		if( event.val != \r ) {

			if( event.isKindOf(Event)) {
				value = event.val;
			};
	        //
			// if( parameter == \note, {
			// 	proxy.set(\t_trig,1,\note,(octave*12)+value);
			// }, {
			// 	proxy.set(parameter.asSymbol,value);
			// });

			switch( parameter.asSymbol,

			// \trigger, {
			// 	proxy.set(\t_trig,1,\amp,value);
			// },
				\octave, { octave = value.asFloat },
				\note, {
					var note = this.parseNote(value);
					note = (octave*12)+note;
					if( (note >= 0) && (note < 128) ) {
						proxy.set(\t_trig,1,\note,note.min(128),\freq,note.min(128).midicps);
					};
				},
				\trigger, {
					proxy.set(\t_trig,1,\amp,value.asFloat);
				},
				\chord, {
					// ["chord",value].postln;
					var chord = value;
					if( chord.isKindOf(String), {
						if( chord.includes($,), {
							chord = chord.split($,);
						}, {
							if( chord.includes($ ), {
								chord = chord.split($ );
							});
						});
					});
					if( chord.isKindOf(Array), {
						chord = chord.collect({|n|
							var note = this.parseNote(n);
							((octave*12)+note).min(128);
						});
					}, {
						chord = ((octave*12)+this.parseNote(chord)).min(128);
					});
					proxy.setn(\notes,chord,\freqs,chord.midicps,\t_trig,1);
				},
				{ // default:
					proxy.set(parameter.asSymbol,this.coerceNumericValue(parameter, value));
				}
			);

		}

	}


	set {|parameter,value|
		proxy.set( parameter, this.coerceNumericValue(parameter, value) );
	}

	defaultValueForParameter {|parameter|
		if(defaultParameters.notNil) {
			^defaultParameters[parameter.asSymbol];
		};
		^nil;
	}

	extractDefaultParameters {|proxy_|
		var defaults = IdentityDictionary.new;
		var source;
		var functionDef;
		var argNames;
		var frame;

		if(proxy_.respondsTo(\source)) {
			source = proxy_.source;
		};

		if(source.isKindOf(Function)) {
			if(source.respondsTo(\def)) {
				functionDef = source.def;
			};

			if(functionDef.notNil && functionDef.respondsTo(\argNames) && functionDef.respondsTo(\prototypeFrame)) {
				argNames = functionDef.argNames;
				frame = functionDef.prototypeFrame;

				if(argNames.notNil && frame.notNil) {
					argNames.do({|argName, index|
						if(frame[index].notNil) {
							defaults[argName.asSymbol] = frame[index];
						};
					});
				};
			};
		};

		^defaults;
	}

	amp_ {|value|
		proxy.set( \amp, value );
	}

	amp {|value|
		if( value.notNil ) {
			proxy.set( \amp, value );
		};
		^amp;
	}


}
