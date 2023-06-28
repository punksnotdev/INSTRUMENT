SynthDef(\snareBass, {|amp=0.5,out=0|
    var drumosc, filterenv, volenv, drumoutput, snaposc, snapenv, fulloutput;

    drumosc = {((Pulse.ar(100)+WhiteNoise.ar)*3).tanh};
    filterenv = {Line.ar(1, 0, 0.2, doneAction: 2)};
    volenv = {EnvGen.kr(Env.perc(0.01,0.6), doneAction: 2)};
    drumoutput = {LPF.ar(drumosc,(filterenv *1000) + 30)};

    snaposc = {BPF.ar(HPF.ar(WhiteNoise.ar(1),500),1500)};
    snapenv = {Line.ar(1, 0, 0.2, doneAction: 2)};

    fulloutput = (drumoutput * volenv) + (snaposc * snapenv);
    //fulloutput = (drumoutput * volenv);

    Out.ar(out,Pan2.ar(fulloutput*(amp), 0));

}).store;
