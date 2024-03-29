SynthDef(\kickGabber, {|amp=0.5, out=0,gain=10,rel=0.3|
   var snd;
   snd = SinOsc.ar(Env([1000, 69, 60], [0.015, 0.1], \exp).ar * [-0.1, 0, 0.1].midiratio);
   snd = Mix(snd);
   snd = (snd * gain).tanh;
   snd = (snd * 5.1).atan;
   snd = BPeakEQ.ar(snd, XLine.kr(2000, 100, 0.3), 2.0, 8);
   snd = snd + DelayC.ar(snd, 0.01, SinOsc.ar(4).range(0.0, 0.001));
   snd = RLPF.ar(snd, 8000, 0.7);
   snd = snd * EnvGen.kr(Env([0, 1, 0.7, 0.7, 0], [0.001, 0.01, rel, 0.02]),doneAction: 2);
   snd = (snd * 0.6).clip(-1, 1);
   Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp) );
}).store;
