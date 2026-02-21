SynthDef(\snapizz_kick2, {|amp=1|
    var snd;
    snd = DC.ar(0);
    snd = snd + (HPF.ar(Hasher.ar(Sweep.ar), 1320) * Env.perc(0.003, 0.03).ar * 0.5);
    snd = snd + (SinOsc.ar(XLine.ar(750, 161, 0.02)) * Env.perc(0.0005, 0.02).ar);
    snd = snd + (SinOsc.ar(XLine.ar(167, 52, 0.04)) * Env.perc(0.0005, 0.3).ar(doneAction: 2));
    snd = snd.tanh * amp;
    Out.ar(\out.kr(0), Pan2.ar(snd, \pan.kr(0)));
//By Nathan Ho aka Snappizz
//http://sccode.org/1-57g
}).store;
