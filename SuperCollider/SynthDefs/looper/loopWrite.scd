
SynthDef(\loopWrite, {| inBus=0, out = 0, buffer = 0|

    var in;
    var numFrames = BufFrames.kr(buffer);
    var bufferTime = numFrames / BufRateScale.kr(buffer);
    bufferTime = bufferTime / s.sampleRate;
    bufferTime.poll;


    in = SoundIn.ar(inBus);



    // Out.ar(out,
        BufWr.ar(
            in, buffer,
            phase: LFSaw.ar(1/bufferTime,1).linlin(-1,1,0,numFrames),
            loop:1
        )
    // )


}).add;
