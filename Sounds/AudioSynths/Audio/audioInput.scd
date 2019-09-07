SynthDef(\audioInput, {| inBus=0, out=99, gate=1,amp=1|

    var in = SoundIn.ar(inBus);
	var env = EnvGen.kr(Env.asr(0.01,1,0.01),gate,doneAction:2);
    Out.ar(out, in * amp)

}).store;
