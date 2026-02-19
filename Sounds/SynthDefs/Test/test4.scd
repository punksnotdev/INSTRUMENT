SynthDef(\test4, {
	Out.ar(0, Pan2.ar(Mix.new(Silent.ar), 0))
}).store;
