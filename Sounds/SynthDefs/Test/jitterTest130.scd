SynthDef(\jitterTest130, {|out=0, freq=440, amp=0.2, gate=1|
	var sig = SinOsc.ar(freq) * amp * EnvGen.kr(Env.perc(0.001, 0.05), gate, doneAction: 2);
	SendReply.kr(Impulse.kr(0), '/noteOn130', [0]);
	Out.ar(out, sig ! 2);
}).store;
