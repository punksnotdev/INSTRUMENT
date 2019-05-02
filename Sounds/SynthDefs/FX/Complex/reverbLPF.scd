SynthDef(\reverbLPF, { arg inBus=0, outBus=0, cutoff=1500,q=0.075, room=0.4,damp=0.6, wet=0.5, lag=2;
    Out.ar(
        outBus,(

            (In.ar(inBus) * (1-wet))+
            (RLPF.ar(
                (FreeVerb.ar(In.ar(inBus),room,damp)*3).tanh, Lag2.kr(cutoff,lag), q
            )*wet

        )*0.75);
    )
}).store;
