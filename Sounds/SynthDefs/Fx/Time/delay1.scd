SynthDef(\delay1, { arg inBus=0, outBus=0, delay = 0.25, wet=0.75,lag=1.5;
    var sig = In.ar(inBus, 2);
    var dly = DelayN.ar(sig, 8, Lag2.kr(delay, lag));
    ReplaceOut.ar(outBus, (sig * (1-wet)) + (dly * wet))
}).store;
