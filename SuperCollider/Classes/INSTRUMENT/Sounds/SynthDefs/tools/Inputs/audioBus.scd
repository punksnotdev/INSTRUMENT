SynthDef(\audioBus, {|inBus=99,outBus=99,amp=1,pan=0|

	var sig = In.ar(inBus) * amp;

	Out.ar(outBus, Pan2.ar(sig,pan))

}).store;
