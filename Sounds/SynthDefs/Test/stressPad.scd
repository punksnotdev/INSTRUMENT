SynthDef(\stressPad, {|out=0, freq=440, amp=0.05, gate=1|
	var sig = SinOsc.ar(freq) * amp * EnvGen.kr(Env.perc(0.001, 0.1), gate, doneAction: 2);
	Out.ar(out, sig ! 2);
}).store;
