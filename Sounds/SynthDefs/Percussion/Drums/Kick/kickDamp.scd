SynthDef(\kickDamp, { |freq = 50, envratio = 3, freqdecay = 0.02, rel = 0.5, amp=0.5, out=0|
    var fenv = EnvGen.kr(Env([envratio, 1], [freqdecay], \exp), 1) * freq,
    aenv = EnvGen.kr(Env.perc(0.005, rel), 1, doneAction: 2);
    Out.ar(out, Pan2.ar(((SinOsc.ar(fenv, 0.5pi, aenv)  * amp* 3).distort*3).tanh,0));
}).store;
