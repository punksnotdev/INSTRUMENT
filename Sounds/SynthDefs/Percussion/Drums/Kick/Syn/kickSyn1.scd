SynthDef(\kickSyn1, {|amp=1, out=0|
    var snd;
    snd = DC.ar(0);
    snd = snd + (SinOsc.ar(XLine.ar(800, 400, 0.01)) * Env.perc(0.0005, 0.01).ar);
    snd = snd + (BPF.ar(Hasher.ar(Sweep.ar), XLine.ar(800, 100, 0.01), 0.6) * Env.perc(0.001, 0.02).delay(0.001).ar);
    snd = snd + (SinOsc.ar(XLine.ar(172, 50, 0.01)) * EnvGen.kr(Env.perc(0.0001, 0.3, 1, \lin).delay(0.005),doneAction: 2));
    snd = snd.tanh;
    Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp*1.5));
}).store;
