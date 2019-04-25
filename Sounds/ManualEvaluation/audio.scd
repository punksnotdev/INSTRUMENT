SynthDef(\audioBus, {|inBus=99,outBus=99,amp=1,pan=0|

	var sig = In.ar(inBus);

	Out.ar(outBus, Pan2.ar(sig * amp,pan))

}).store;


SynthDef(\audioInput, {| inBus=0, out = 0, gate=1,amp=1|

    var in = SoundIn.ar(inBus);
	var env = EnvGen.kr(Env.asr(0.01,1,0.01),gate,doneAction:2);
    Out.ar(out, in * amp)

}).store;


SynthDef(\audioOutput, {|inBus=99,outBus=99,amp=1,pan=0|

	var sig = In.ar(inBus);

	Out.ar(outBus, Pan2.ar(sig * amp,pan))

}).store;
