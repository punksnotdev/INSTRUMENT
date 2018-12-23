SynthDef(\loopRead, {| out = 0, buffer = 0|

	var numFrames = BufFrames.kr(buffer);
	var bufferTime = numFrames / BufRateScale.kr(buffer);

	bufferTime = bufferTime / s.sampleRate;

    Out.ar(out,
        BufRd.ar(
			1, buffer,
			phase: LFSaw.ar(1/bufferTime,1).linlin(-1,1,0,numFrames),
			loop:1
		)
    )

}).store;
