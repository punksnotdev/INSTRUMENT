I8TProxyFx : Object
{

	var <>target;
	var <>name;
	var <>paramMap;
	var <>values;

	*new {|target_, name_, paramMap_, defaults_|
		^super.new.init(target_, name_, paramMap_, defaults_);
	}

	init {|target_, name_, paramMap_, defaults_|
		target = target_;
		name = name_;
		paramMap = paramMap_ ? IdentityDictionary.new;
		values = IdentityDictionary.new;
		if(defaults_.notNil) {
			defaults_.keysValuesDo({|k, v|
				values[k.asSymbol] = v;
			});
		};
		^this;
	}

	setFxParam {|parameter, value|
		var key = parameter.asSymbol;
		var mapped = paramMap[key];
		var out = value;

		if(target.notNil && target.respondsTo(\isNumericString)) {
			if(out.isKindOf(String) && target.isNumericString(out)) {
				out = out.asFloat;
			};
		};

		if(mapped.notNil) {
			values[key] = out;
			if(target.notNil && target.proxy.notNil) {
				target.proxy.set(mapped, out);
			};
			^out;
		};

		("Proxy fxSet: unknown parameter " ++ parameter ++ " for " ++ name).warn;
		^nil;
	}

	set {|parameter, value|
		^this.setFxParam(parameter, value);
	}

	fxSet {|parameter, value|
		^this.setFxParam(parameter, value);
	}

	doesNotUnderstand {
		arg selector ... args;
		var value = args[0];

		if(selector.isSetter) {
			^this.setFxParam(selector.asGetter.asSymbol, value);
		};

		if(selector.isKindOf(Symbol)) {
			if(values.notNil) {
				^values[selector];
			};
		};

		^nil;
	}

}
