
SynthDef(\reverb, { arg inBus=0, rv1 = 0.7, rv2=0.8, wet=0.75;
    Out.ar(
        [0,1],
        (In.ar(inBus) * (1-wet))+
        (FreeVerb.ar(
            In.ar(inBus) * wet,
            rv1,
            rv2
        )*wet)
    )
}).store;
