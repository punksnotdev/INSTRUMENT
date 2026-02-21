Proxy : I8TInstrument
{

	var <proxy;
	var amp;
	var defaultParameters;
	var fx;
	var fxSlotCount;
	var fxHandles;

	*new{|proxy_|
		^super.new.init(this.graph,proxy_);
	}

	init{|main_,proxy_|
		if( proxy_.isKindOf(NodeProxy), {
			proxy_.postln;
			proxy = proxy_;
			fxSlotCount = 0;
			fxHandles = IdentityDictionary.new;
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
			fxSlotCount = 0;
			fxHandles = IdentityDictionary.new;
			defaultParameters = this.extractDefaultParameters(proxy_);
		})
	}

	proxy_{|proxy_|

		proxy = proxy_;
		fxSlotCount = 0;
		fxHandles = IdentityDictionary.new;
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
					\fx, {
						this.fx_(value);
					},
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
		switch(parameter.asSymbol,
			\fx, {
				this.fx_(value);
			},
			{
				proxy.set( parameter, this.coerceNumericValue(parameter, value) );
			}
		);
	}

	resolveFxEntry {|fx_|
		var fxName;
		var parts;
		var base;
		var variant;

		if( fx_.isKindOf(Function), {
			^(spec: (\filter -> fx_));
		});

		if( fx_.isKindOf(Association), {
			^(spec: fx_);
		});

		if( fx_.isKindOf(String) || fx_.isKindOf(Symbol), {
			fxName = fx_.asString;
			parts = fxName.split($.);
			base = parts[0].stripWhiteSpace.asSymbol;
			if(parts.size > 1) {
				variant = parts[1].stripWhiteSpace.asSymbol;
			};
			^this.proxyFxPreset(base, variant);
		});

		^nil;
	}

	clearFxSlots {
		var count = fxSlotCount;
		if(count.isNil) {
			count = 0;
		};
		if(count > 0) {
			count.do({|i|
				proxy[i + 1] = nil;
			});
		};
		fxSlotCount = 0;
	}

	clearFxHandles {
		if(fxHandles.isNil) {
			fxHandles = IdentityDictionary.new;
		} {
			fxHandles.clear;
		};
	}

	registerFxHandle {|fxEntry|
		var key;
		var paramMap;
		var defaults;

		key = fxEntry[\key];
		paramMap = fxEntry[\paramMap];
		defaults = fxEntry[\defaults];

		if(key.notNil && paramMap.notNil) {
			if(fxHandles.isNil) {
				fxHandles = IdentityDictionary.new;
			};
			fxHandles[key.asSymbol] = I8TProxyFx(this, key.asSymbol, paramMap, defaults);
		};
	}

	proxyFxPreset {|name, variant|
		var entry;
		var defaults;

		switch(name.asSymbol,
			\reverb, {
				defaults = (
					wet: 0.45,
					room: 0.75,
					damp: 0.55,
					gain: 1.2
				);

				switch(variant,
					\small, { defaults = (wet: 0.25, room: 0.35, damp: 0.35, gain: 1.05); },
					\medium, { defaults = (wet: 0.45, room: 0.65, damp: 0.5, gain: 1.2); },
					\large, { defaults = (wet: 0.7, room: 0.9, damp: 0.65, gain: 1.3); },
					\infinite, { defaults = (wet: 1.0, room: 1.0, damp: 0.98, gain: 1.35); }
				);

				entry = (
					key: \reverb,
					spec: (\filter -> {|in, reverbWet=0.45, reverbRoom=0.75, reverbDamp=0.55, reverbGain=1.2|
						var rev;
						var mixed;
						rev = FreeVerb.ar(in, 1, reverbRoom, reverbDamp);
						mixed = XFade2.ar(in, rev, (reverbWet * 2) - 1);
						(mixed * reverbGain).tanh;
					}),
					paramMap: (
						wet: \reverbWet,
						room: \reverbRoom,
						damp: \reverbDamp,
						gain: \reverbGain
					),
					defaults: defaults
				);
			},
			\lpf, {
				entry = (
					key: \lpf,
					spec: (\filter -> {|in, lpfFreq=1200, lpfRq=0.2|
						RLPF.ar(in, lpfFreq, lpfRq);
					}),
					paramMap: (freq: \lpfFreq, rq: \lpfRq),
					defaults: (freq: 1200, rq: 0.2)
				);
			},
			\hpf, {
				entry = (
					key: \hpf,
					spec: (\filter -> {|in, hpfFreq=180, hpfRq=0.5|
						RHPF.ar(in, hpfFreq, hpfRq);
					}),
					paramMap: (freq: \hpfFreq, rq: \hpfRq),
					defaults: (freq: 180, rq: 0.5)
				);
			},
			\distortion, {
				entry = (
					key: \distortion,
					spec: (\filter -> {|in, distortionGain=2.4|
						(in * distortionGain).tanh;
					}),
					paramMap: (gain: \distortionGain),
					defaults: (gain: 2.4)
				);
			},
			\overdrive, {
				entry = (
					key: \overdrive,
					spec: (\filter -> {|in, overdriveGain=3.0|
						(in * overdriveGain).tanh;
					}),
					paramMap: (gain: \overdriveGain),
					defaults: (gain: 3.0)
				);
			},
			\extreme, {
				entry = (
					key: \extreme,
					spec: (\filter -> {|in, extremeGain=4.0|
						(in * extremeGain).tanh;
					}),
					paramMap: (gain: \extremeGain),
					defaults: (gain: 4.0)
				);
			},
			\saturate, {
				entry = (
					key: \saturate,
					spec: (\filter -> {|in, saturateGain=1.5|
						(in * saturateGain).softclip;
					}),
					paramMap: (gain: \saturateGain),
					defaults: (gain: 1.5)
				);
			},
			\delay1, {
				entry = (
					key: \delay1,
					spec: (\filter -> {|in, delay1Wet=0.4, delay1Time=0.2|
						var d = DelayC.ar(in, 1, delay1Time);
						XFade2.ar(in, d, (delay1Wet * 2) - 1);
					}),
					paramMap: (wet: \delay1Wet, time: \delay1Time),
					defaults: (wet: 0.4, time: 0.2)
				);
			},
			\delay2, {
				entry = (
					key: \delay2,
					spec: (\filter -> {|in, delay2Wet=0.45, delay2Time=0.33, delay2Decay=1.2|
						var d = CombL.ar(in, 1, delay2Time, delay2Decay);
						XFade2.ar(in, d, (delay2Wet * 2) - 1);
					}),
					paramMap: (wet: \delay2Wet, time: \delay2Time, decay: \delay2Decay),
					defaults: (wet: 0.45, time: 0.33, decay: 1.2)
				);
			},
			\delay3, {
				entry = (
					key: \delay3,
					spec: (\filter -> {|in, delay3Wet=0.6, delay3Time=0.6, delay3Decay=2.5|
						var d = CombL.ar(in, 1, delay3Time, delay3Decay);
						XFade2.ar(in, d, (delay3Wet * 2) - 1);
					}),
					paramMap: (wet: \delay3Wet, time: \delay3Time, decay: \delay3Decay),
					defaults: (wet: 0.6, time: 0.6, decay: 2.5)
				);
			}
		);

		^entry;
	}

	applyFx {|fx_|
		var fxEntry;
		var fxEntries;
		var hasInvalid = false;

		if(proxy.isNil) {
			"Proxy.fx: proxy is nil".warn;
			^nil;
		};

		if( fx_.isNil || (fx_===false), {
			this.clearFxSlots;
			this.clearFxHandles;
			fx = nil;
			^nil;
		});

		if( fx_.isKindOf(Collection) && (fx_.isKindOf(String) == false), {
			fxEntries = List.new;

			fx_.do({|item|
				var chainEntry = this.resolveFxEntry(item);
				if(chainEntry.notNil, {
					fxEntries.add(chainEntry);
				}, {
					("Proxy.fx: unsupported fx value in chain -> "++item).warn;
					hasInvalid = true;
				});
			});

			if(hasInvalid) {
				^nil;
			};

			this.clearFxSlots;
			this.clearFxHandles;
			fxEntries.do({|chainEntry, i|
				proxy[i + 1] = chainEntry[\spec];
				this.registerFxHandle(chainEntry);
			});
			fxSlotCount = fxEntries.size;
			fx = fx_;
			^fx;
		});

		fxEntry = this.resolveFxEntry(fx_);
		if(fxEntry.notNil) {
			this.clearFxSlots;
			this.clearFxHandles;
			proxy[1] = fxEntry[\spec];
			fxSlotCount = 1;
			this.registerFxHandle(fxEntry);
			fx = fx_;
			^fx;
		};

		("Proxy.fx: unsupported fx value -> "++fx_).warn;
		^nil;
	}

	fx {|value|
		if(value.notNil) {
			^this.applyFx(value);
		};
		^fx;
	}

	fx_ {|value|
		^this.applyFx(value);
	}

	doesNotUnderstand {

		arg selector ... args;

		var value = args[0];

		if (selector.isSetter) {

			if( parameters.isNil ) {
				parameters = IdentityDictionary.new;
			};

			parameters[selector.asGetter] = value;
			this.set(selector.asGetter, value);
			^"";
		};

		if (selector.isKindOf(Symbol)) {
			if(args.size == 0) {
				if(fxHandles.notNil && fxHandles[selector].notNil) {
					^fxHandles[selector];
				};
			};

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
				^parameters[selector];
			};
		};
		^nil;

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
