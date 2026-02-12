SynthDef(\audioOutput, {|inBus=99,outBus=99,amp=1,pan=0|

	var sig = In.ar(inBus, 2);

	Out.ar(outBus, Balance2.ar(sig[0], sig[1], pan, amp))

}).store;
