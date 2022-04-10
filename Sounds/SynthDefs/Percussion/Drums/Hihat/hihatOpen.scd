SynthDef(\hihatOpen, {|amp=0.5,out=0|

    var hatosc, hatenv, hatnoise, hatoutput;

    hatnoise = {LPF.ar(WhiteNoise.ar(1),6000)};

    hatosc = {HPF.ar(hatnoise,2000)};
    hatenv = {Line.ar(1, 0, 0.3,doneAction: 2)};

    hatoutput = ((hatosc * hatenv)/1.5).tanh;

    Out.ar(out,
    Pan2.ar(hatoutput*amp, 0)

    )

}).store;
