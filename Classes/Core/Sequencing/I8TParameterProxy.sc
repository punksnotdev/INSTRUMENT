I8TParameterProxy : Object
{

	var <>target;
	var <>parameter;

	*new {|target_, parameter_|
		^super.new.init(target_, parameter_);
	}

	init {|target_, parameter_|
		target = target_;
		parameter = parameter_.asSymbol;
		^this;
	}

	rawValue {
		if(target.notNil && target.parameters.notNil) {
			^target.parameters[parameter];
		};
		^nil;
	}

	value {|newValue|
		if(newValue.notNil) {
			if(target.notNil && target.respondsTo(\coerceNumericValue)) {
				newValue = target.coerceNumericValue(parameter, newValue);
			};
			target.set(parameter, newValue);
			^newValue;
		};
		^this.rawValue;
	}

	value_ {|newValue| ^this.value(newValue); }

	seq {|pattern, test|
		^target.seq(parameter, pattern, test);
	}

	seq_ {|pattern, test| ^this.seq(pattern, test); }

	reset {
		var defaultValue;

		if(target.notNil && target.respondsTo(\rm)) {
			target.rm(parameter);
		};

		if(target.notNil && target.respondsTo(\defaultValueForParameter)) {
			defaultValue = target.defaultValueForParameter(parameter);
		};

		if(defaultValue.notNil && target.notNil) {
			target.perform((parameter.asString ++ "_").asSymbol, defaultValue);
			^defaultValue;
		};

		^this.rawValue;
	}

	clear {
		^this.reset;
	}

	rm {|key|
		^target.rm(parameter, key);
	}

	get {|key|
		^target.get(parameter, key);
	}

	set {|key, parameters|
		^target.set(parameter, key, parameters);
	}

	patterns {
		^target.patterns(parameter);
	}

	sequence {
		^target.sequence(parameter);
	}

	doesNotUnderstand {
		arg selector ... args;
		var value = this.rawValue;
		if(value.notNil) {
			^value.performList(selector, args);
		};
		^nil;
	}

}
