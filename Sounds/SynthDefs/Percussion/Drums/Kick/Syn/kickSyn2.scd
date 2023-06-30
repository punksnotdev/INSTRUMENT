SynthDef(\kickSyn2, {|amp=1, out=0|
    var snd;
    snd = DC.ar(0);
    snd = snd + (HPF.ar(Hasher.ar(Sweep.ar), 1320) * Env.perc(0.003, 0.03).ar);
    snd = snd + (SinOsc.ar(XLine.ar(750, 161, 0.02)) * Env.perc(0.0005, 0.02).ar);
    snd = snd + (SinOsc.ar(XLine.ar(167, 52, 0.04)) * EnvGen.kr(Env.perc(0.0005, 0.3),doneAction: 2));
    snd = snd.tanh;
    Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp*1.15));
}).store;
