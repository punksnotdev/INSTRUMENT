(

   SynthDef(\snare1, {
       |out = 0, pan = 0, amp = 0.5|
       var pop, popAmp, popFreq;
       var noise, noiseAmp;
       var snd;

       // pop makes a click coming from very high frequencies
       // slowing down a little and stopping in mid-to-low
       popFreq = EnvGen.ar(Env([3261, 410, 160], [0.005, 0.01], curve: \exp));
       popAmp = EnvGen.ar(Env.perc(0.001, 0.11)) * 0.7;
       pop = SinOsc.ar(popFreq) * popAmp;
       // bandpass-filtered white noise
       noiseAmp = EnvGen.ar(Env.perc(0.001, 0.15), doneAction: 1);
       noise = BPF.ar(WhiteNoise.ar, 810, 1.6) * noiseAmp;

       snd = (pop + noise) * 1.3;

       Out.ar(out, Pan2.ar(snd, pan, (amp/3)));
   }).store;

SynthDef(\snare2, {|amp=0.5,out=0|
   var drumosc, filterenv, volenv, drumoutput, snaposc, snapenv, fulloutput;

   drumosc = {((Pulse.ar(100)+WhiteNoise.ar)*3).tanh};
   filterenv = {Line.ar(1, 0, 0.2, doneAction: 1)};
   volenv = {Line.ar(1, 0, 0.6, doneAction: 1)};
   drumoutput = {LPF.ar(drumosc,(filterenv *1000) + 30)};

   snaposc = {BPF.ar(HPF.ar(WhiteNoise.ar(1),500),1500)};
   snapenv = {Line.ar(1, 0, 0.2, doneAction: 1)};

   fulloutput = (drumoutput * volenv) + (snaposc * snapenv);
   //fulloutput = (drumoutput * volenv);

   Out.ar(out,Pan2.ar(fulloutput*(amp/3), 0));

}).store
)
