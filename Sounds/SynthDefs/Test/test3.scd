SynthDef(\test3, {
	Out.ar(0,Mix.new(Silent.ar)!2)
}).store;
