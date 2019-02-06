SynthDef(\i8t_drum_kickElectro, {|pan = 0, amp = 1, out=0|

  var body, bodyFreq, bodyAmp;
  var pop, popFreq, popAmp;
  var click, clickAmp;
  var snd;

  // body starts midrange, quickly drops down to low freqs, and trails off
  bodyFreq = EnvGen.ar(Env([261, 120, 51], [0.035, 0.08], curve: \exp));
  bodyAmp = EnvGen.ar(Env.linen(0.005, 0.1, 0.3), doneAction: 1);
  body = SinOsc.ar(bodyFreq) * bodyAmp;
  // pop sweeps over the midrange
  popFreq = XLine.kr(750, 261, 0.02);
  popAmp = EnvGen.ar(Env.linen(0.001, 0.02, 0.001)) * 0.15;
  pop = SinOsc.ar(popFreq) * popAmp;
  // click is spectrally rich, covering the high-freq range
  // you can use Formant, FM, noise, whatever
  clickAmp = EnvGen.ar(Env.perc(0.001, 0.01)) * 0.15;
  click = LPF.ar(Formant.ar(910, 4760, 2110), 3140) * clickAmp;

  snd = body + pop + click;
  snd = snd.tanh;

  Out.ar(out, Pan2.ar(snd * amp, pan, amp) / 2);
  
}).store;
