SynthDef(\bassBroken,
{|freq,att=0.01,rel=0.5,out=0,amp=1,gain=0,wet=0.5|
    var snd, string, delay;
    var gainMapped;
    string = { |f|
        var delay;
        delay = f.reciprocal;
        Pluck.ar(SinOsc.ar(Line.ar(1000, 50, 0.01)) * Env.perc(0.001, 0.01).ar, Impulse.ar(0), delay, delay, 5, 0.5)
    };

    gainMapped = 2 + (14*gain);
    snd = string.(freq/4);
    snd = (snd * gainMapped).tanh;
    snd = snd + RLPF.ar(snd, 220, 0.5);
    snd = snd + RLPF.ar(snd, 2200, 0.5);
    snd = (snd * gainMapped).tanh;
    snd = snd + RLPF.ar(snd, 500, 0.5);
    // snd = (snd * gainMapped).clip2(1/10)*10;
    snd = (snd * gainMapped).tanh;

    snd = (snd*(1-wet)) + (BHiShelf.ar(snd, 3200, 1, -3.0)*wet);

    snd = (snd*(1-wet)) + (LeakDC.ar(snd)*wet);
    // LocalOut.ar(snd);
    snd = DelayN.ar(snd, 0.1, SinOsc.kr(1/8, [0, 1pi]).range(0, 1e-4));
    snd = EnvGen.kr(Env.perc(att,rel), doneAction: 2)*snd;

    snd = (snd * 1).tanh;

    snd = snd * amp;
    // uncomment for reverb 3.10
    // snd = snd + (NHHall.ar(snd, 1) * -5.dbamp);
    Out.ar(out,snd);
}).store;

