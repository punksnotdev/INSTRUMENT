SynthDef(\cymbalElectro, {
    |out = 0, pan = 0, amp = 0.3,rel=6|
    var click, clickAmp;
    var noise, noiseAmp;
    var snd;

    // noise -> resonance -> expodec envelope
    noiseAmp = EnvGen.ar(Env.perc(0.001, rel, curve: -8), doneAction: 2);
    noise = Mix(BPF.ar(ClipNoise.ar, [4010, 2100, 4151]*[1/10,1,1.5,1.66,2,1/2,3], [0.15, 0.56,0.025], [1.0, 0.6])) * 0.7 * noiseAmp;

    snd = noise;

    Out.ar(out, Pan2.ar(snd, pan, amp/2));
}).store;
