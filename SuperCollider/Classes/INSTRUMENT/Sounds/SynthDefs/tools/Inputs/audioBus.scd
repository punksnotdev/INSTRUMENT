SynthDef(\audioBus, {|inBus=99,outBus=99|

	var sig = In.ar(inBus);

	ReplaceOut.ar(outBus, Pan2.ar(sig,0))

}).store;
