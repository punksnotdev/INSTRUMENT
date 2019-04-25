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


SynthDef(\loopRead, {| out = 0, buffer = 0, gate=1,duration=1,amp=1,rate=1|

	var sig, env;
	var numFrames = BufFrames.kr(buffer);
	// var bufferTime = numFrames / BufRateScale.kr(buffer);
	// var bufferTime = duration / SampleRate.ir;
	// bufferTime = bufferTime / TempoClock.default.tempo;

	sig = BufRd.ar(
		1, buffer,
		phase: LFSaw.ar((1/duration)*rate,1).linlin(-1,1,0,duration*SampleRate.ir),
		loop:0
	);

	env = EnvGen.kr(Env.asr(0.05,1,0.05),gate,doneAction:2);

	sig = sig * env;
	sig = sig * amp;

    Out.ar(out, Pan2.ar(sig, 0) );

}).store;


SynthDef(\loopWrite, {| inBus=0, out = 0, buffer = 0, rate=1|

    var in = SoundIn.ar(inBus);

    BufWr.ar(
        in, buffer,
        phase: Phasor.ar(0, BufRateScale.kr(buffer) * rate, 0, BufFrames.kr(buffer)),
        loop:1
    )

}).store;
