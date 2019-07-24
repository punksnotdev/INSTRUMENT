SynthDef(\kickSyn3, {|amp=0.5, out=0|
    var snd;
    snd = DC.ar(0);
    snd = snd + (SinOsc.ar(XLine.ar(1500, 800, 0.01)) * Env.perc(0.0005, 0.01, curve: \lin).ar);
    snd = snd + (BPF.ar(Impulse.ar(0) * SampleRate.ir / 28000, 6100, 1.0) * 3.dbamp);
    snd = snd + (BPF.ar(Hasher.ar(Sweep.ar), 300, 0.9) * Env.perc(0.001, 0.02).ar);
    snd = snd + (SinOsc.ar(XLine.ar(472, 60, 0.045)) * EnvGen.kr(Env.perc(0.0001, 0.3, curve: \lin).delay(0.005),doneAction: 2));
    snd = snd.tanh;
    Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp) / 2);
}).store;
