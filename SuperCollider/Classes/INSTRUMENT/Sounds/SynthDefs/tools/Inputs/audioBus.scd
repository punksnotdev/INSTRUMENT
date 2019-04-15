SynthDef(\audioBus, {|in=99,out=99|

	var sig = In.ar(in);

	ReplaceOut.ar(out, Pan2.ar(sig,0))

}).store;
