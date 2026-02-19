SynthDef(\kickFull, {|amp=0.5, out=0|

    var subosc, subenv, suboutput, clickosc, clickenv, clickoutput;

    subosc = {SinOsc.ar(60)*1.5};
    subenv = {Line.ar(1, 0, 0.3, doneAction: 2)};

    clickosc = {LPF.ar(WhiteNoise.ar(1),1500)};
    clickenv = {Line.ar(1, 0, 0.02)};

    suboutput = (subosc * subenv);
    clickoutput = (clickosc * clickenv);

    Out.ar(out, Pan2.ar((suboutput + clickoutput) * amp, 0));

}).store;
