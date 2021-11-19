SynthDef(\powerChord,
{|freq,att=0.1,rel=0.5,out=0,amp=1|
    var snd, string, delay;
    string = { |f|
        var delay;
        delay = f.reciprocal;
        Pluck.ar(SinOsc.ar(Line.ar(1000, 50, 0.01)) * Env.perc(0.001, 0.01).ar, Impulse.ar(0), delay, delay, 5, 0.5)
    };
    snd = string.(freq/2) + string.(freq/2*1.5) + string.(freq/2*2);
    snd = snd + (HPF.ar(LocalIn.ar(1), 3000) * -50.dbamp);
    snd = (snd * 32.dbamp).tanh;
    snd = RLPF.ar(snd, 3000, 0.5);
    snd = (snd * 32.dbamp).tanh;
    snd = RLPF.ar(snd, 500, 0.5);
    // snd = (snd * 32.dbamp).clip2(1/10)*10;
    snd = (snd * 32.dbamp).tanh;
    snd = BHiShelf.ar(snd, 3200, 1, -3.0);
    snd = LeakDC.ar(snd);
    // LocalOut.ar(snd);
    snd = DelayN.ar(snd, 0.1, SinOsc.kr(2, [0, 1pi]).range(0, 1e-4));
    snd = EnvGen.kr(Env.perc(att,rel), doneAction: 2)*snd;
    snd = snd / 2;
    snd = snd * amp;
    // uncomment for reverb 3.10
    // snd = snd + (NHHall.ar(snd, 1) * -5.dbamp);
    Out.ar(out,snd/2);
}).store;
