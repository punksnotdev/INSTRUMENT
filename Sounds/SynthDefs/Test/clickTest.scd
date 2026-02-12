SynthDef(\clickTest, {|out=0, amp=0.5, pan=0, gate=1|
	var sig = EnvGen.kr(Env.perc(0.001, 0.02), gate, doneAction: 2) * WhiteNoise.ar(amp);
	Out.ar(out, Pan2.ar(sig, pan));
}).store;
