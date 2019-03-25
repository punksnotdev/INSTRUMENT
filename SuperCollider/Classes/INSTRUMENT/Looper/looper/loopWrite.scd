SynthDef(\loopWrite, {| inBus=0, out = 0, buffer = 0, rate=1|

    var in = SoundIn.ar(inBus);
    
    BufWr.ar(
        in, buffer,
        phase: Phasor.ar(0, BufRateScale.kr(buffer) * rate, 0, BufFrames.kr(buffer)),
        loop:1
    )

}).store;
