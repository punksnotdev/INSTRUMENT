SynthDef(\delay1, { arg inBus=0, delay = 0.25, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        ((DelayN.ar(
            In.ar(inBus),
            8,
            delay
        )/2)*wet)
    )
}).store;
