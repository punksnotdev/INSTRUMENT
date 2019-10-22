SynthDef(\reverbLPF, { arg inBus=0, outBus=0, cutoff=1500,q=0.1, room=0.4,damp=0.6, wet=0.5, lag=2;
    ReplaceOut.ar(
        outBus,
        Pan2.ar(

            (In.ar(inBus) * (1-wet))+
            (RLPF.ar(
                (FreeVerb.ar(In.ar(inBus),room,damp)*1.7).tanh, Lag2.kr(cutoff,lag), q
            )*wet

        ),0);
    )
}).store;
