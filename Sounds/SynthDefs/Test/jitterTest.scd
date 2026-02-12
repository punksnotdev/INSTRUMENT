SynthDef(\jitterTest, {|out=0, freq=440, amp=0.3, gate=1|
	var timer = Sweep.kr(1);
	var sig = SinOsc.ar(freq) * amp * EnvGen.kr(Env.perc(0.001, 0.05), gate, doneAction: 2);
	SendReply.kr(Impulse.kr(0), '/noteOn', [timer]);
	Out.ar(out, sig ! 2);
}).store;
