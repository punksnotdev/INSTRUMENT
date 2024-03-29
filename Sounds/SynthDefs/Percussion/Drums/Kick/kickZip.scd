SynthDef(\kickZip, {|amp=0.5, out=0|

    var daNoise,daOsc,env1,env2,env3, envZip;
    envZip=Env.perc(0.0001,1,18000,-20);

    env1=Env.perc(0.001,1,18000,-20);
    env2=Env.perc(0.001,1,1,-8);
    env3=Env.perc(0.001,1,80,-8);

    daNoise=LPF.ar(LFPulse.ar(10000,0,0.1),EnvGen.kr(envZip)+20);

    daOsc=LPF.ar(SinOsc.ar(EnvGen.kr(env3)+20),200);

    Out.ar(out,Pan2.ar(Mix.ar([daNoise,daOsc]) * amp * 2, 0, EnvGen.kr(env2,doneAction: 2)));

}).store;
