SynthDef(\loopRead, {| out = 0, buffer = 0, gate=1,duration=1|

	var sig, env;
	var numFrames = BufFrames.kr(buffer);
	// var bufferTime = numFrames / BufRateScale.kr(buffer);
	// var bufferTime = duration / SampleRate.ir;
	// bufferTime = bufferTime / TempoClock.default.tempo;

	sig = BufRd.ar(
		1, buffer,
		phase: LFSaw.ar(1/duration,1).linlin(-1,1,0,duration*SampleRate.ir),
		loop:0
	);

	env = EnvGen.kr(Env.asr(0.05,1,0.05),gate,doneAction:2);

	sig = sig * env;

    Out.ar(out, Pan2.ar(sig, 0) );

}).store;
