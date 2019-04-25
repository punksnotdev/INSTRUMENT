SynthDef(\kickHard, { |basefreq = 50, envratio = 3, freqdecay = 0.02, ampdecay = 0.5, amp=0.5, out=0|
    var fenv = EnvGen.kr(Env([envratio, 1], [freqdecay], \exp), 1) * basefreq,
    aenv = EnvGen.kr(Env.perc(0.005, ampdecay), 1, doneAction: 2);
    Out.ar(out, (SinOsc.ar(fenv, 0.5pi, aenv)  * amp*1.4).distort);
}).store;
