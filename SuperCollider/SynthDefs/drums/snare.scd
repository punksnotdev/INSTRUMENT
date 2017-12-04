(

   SynthDef(\snareElectro, {
       |out = 0, pan = 0, amp = 0.3|
       var pop, popAmp, popFreq;
       var noise, noiseAmp;
       var snd;

       // pop makes a click coming from very high frequencies
       // slowing down a little and stopping in mid-to-low
       popFreq = EnvGen.ar(Env([3261, 410, 160], [0.005, 0.01], curve: \exp));
       popAmp = EnvGen.ar(Env.perc(0.001, 0.11)) * 0.7;
       pop = SinOsc.ar(popFreq) * popAmp;
       // bandpass-filtered white noise
       noiseAmp = EnvGen.ar(Env.perc(0.001, 0.15), doneAction: 2);
       noise = BPF.ar(WhiteNoise.ar, 810, 1.6) * noiseAmp;

       snd = (pop + noise) * 1.3;

       Out.ar(out, Pan2.ar(snd, pan, amp));
   }).add;

SynthDef(\snareNoise, {
   var drumosc, filterenv, volenv, drumoutput, snaposc, snapenv, fulloutput;

   drumosc = {((Pulse.ar(100)+WhiteNoise.ar)*3).tanh};
   filterenv = {Line.ar(1, 0, 0.2, doneAction: 0)};
   volenv = {Line.ar(1, 0, 0.6, doneAction: 2)};
   drumoutput = {LPF.ar(drumosc,(filterenv *1000) + 30)};

   snaposc = {BPF.ar(HPF.ar(WhiteNoise.ar(1),500),1500)};
   snapenv = {Line.ar(1, 0, 0.2, doneAction: 0)};

   fulloutput = (drumoutput * volenv) + (snaposc * snapenv);
   //fulloutput = (drumoutput * volenv);

   Out.ar(0,Pan2.ar(fulloutput, 0));

}).add
)