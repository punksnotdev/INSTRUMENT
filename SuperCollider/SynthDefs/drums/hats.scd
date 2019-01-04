(


SynthDef(\hh2, {
    |out = 0, pan = 0, amp = 0.5|
    var click, clickAmp;
    var noise, noiseAmp;
    var snd;

    // noise -> resonance -> expodec envelope
    noiseAmp = EnvGen.ar(Env.perc(0.001, 0.3, curve: -8), doneAction: 2);
    noise = Mix(BPF.ar(ClipNoise.ar, [4010, 4151], [0.15, 0.56], [1.0, 0.6])) * 0.7 * noiseAmp;

    snd = noise;

    Out.ar(out, Pan2.ar(snd, pan, amp) );
}).store;


SynthDef(\hhOpen, {|amp=0.5,out=0|

    var hatosc, hatenv, hatnoise, hatoutput;

    hatnoise = {LPF.ar(WhiteNoise.ar(1),6000)};

    hatosc = {HPF.ar(hatnoise,2000)};
    hatenv = {Line.ar(1, 0, 0.3,doneAction: 2)};

    hatoutput = (hatosc * hatenv);

    Out.ar(out,
    Pan2.ar(hatoutput*amp, 0)

    )

}).store;

SynthDef(\hhClosed, {|amp=0.5,out=0|

    var hatosc, hatenv, hatnoise, hatoutput;

    hatnoise = {LPF.ar(WhiteNoise.ar(1/2),6000)};

    hatosc = {HPF.ar(hatnoise,2000)};
    hatenv = {Line.ar(1, 0, 0.1,doneAction: 2)};

    hatoutput = (hatosc * hatenv);

    Out.ar(out,
    Pan2.ar(hatoutput*amp, 0)

    )

}).store;
)
