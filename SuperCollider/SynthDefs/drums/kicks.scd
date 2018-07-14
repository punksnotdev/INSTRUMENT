(
   SynthDef(\kickHollow, {

      var sig, env;

      sig = SinOsc.ar(60,pi/2) ;
      sig = sig+(Saw.ar(Line.kr(12000,40,1/23),pi/2) / 12);
      /*sig = sig + SinOsc.ar([14,24,60,120,444,699,999],0,3).tanh/4;*/
      sig = sig * EnvGen.kr( Env.perc(1/20,1/3), doneAction: 1);

      /*sig = (sig*8).tanh * EnvGen.kr( Env.perc(1/20,1/6));*/
      sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1),Array.geom(7,90,43),0.5)).tanh/8 * EnvGen.kr( Env.perc(1/20,1/4) ))/8;

      sig = sig.tanh!2;

      /*sig = sig * EnvGen.kr( Env.perc(0.01,1/6), doneAction: 1 ) * 1;*/

      Out.ar( 0, Pan2.ar(sig,0) );

   }).store;

   SynthDef(\kickFull, {

      var subosc, subenv, suboutput, clickosc, clickenv, clickoutput;

      subosc = {SinOsc.ar(60)};
      subenv = {Line.ar(1, 0, 0.3, doneAction: 1)};

      clickosc = {LPF.ar(WhiteNoise.ar(1),1500)};
      clickenv = {Line.ar(1, 0, 0.02)};

      suboutput = (subosc * subenv);
      clickoutput = (clickosc * clickenv);

      Out.ar(0, Pan2.ar(suboutput + clickoutput, 0));

   }).store;

   SynthDef(\kickDeep, {

     var daNoise,daOsc,env1,env2,env3;

     env1=Env.perc(0.001,1,80,-20);
     env2=Env.perc(0.001,1,1,-8);
     env3=Env.perc(0.001,1,80,-8);

     daNoise=LPF.ar(WhiteNoise.ar(1),EnvGen.kr(env1)+20);

     daOsc=LPF.ar(SinOsc.ar(EnvGen.kr(env3)+20),200);

     Out.ar(0,Pan2.ar(Mix.ar([daNoise,daOsc]), 0, EnvGen.kr(env2,doneAction: 1)));

   }).store;

   SynthDef(\kickSimple, {
      var sig,cmp;
      sig = LPF.ar( SinOsc.ar( 60, pi/2 ) * 8, 500 );
      sig = sig * EnvGen.kr(Env.perc(1/20,1/2),doneAction: 1);
      cmp = CompanderD.ar(sig, thresh: -20.dbamp, slopeBelow: 1, slopeAbove: 0.3, clampTime: 0.003, relaxTime: 0.08);
      Out.ar(0,Pan2.ar(cmp * (10.dbamp * 0.25),0)) ! 2;

   }).store;



  SynthDef(\kickHard, { |basefreq = 50, envratio = 3, freqdecay = 0.02, ampdecay = 0.5, outbus = 0|
     var   fenv = EnvGen.kr(Env([envratio, 1], [freqdecay], \exp), 1) * basefreq,
        aenv = EnvGen.kr(Env.perc(0.005, ampdecay), 1, doneAction: 1);
     Out.ar(outbus, SinOsc.ar(fenv, 0.5pi, aenv));
  }).store;

  SynthDef(\kickElectro, {
      |out = 0, pan = 0, amp = 0.3|
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

      Out.ar(out, Pan2.ar(snd, pan, amp));
  }).store;



   SynthDef(\kickGabber, {
       var snd;
       snd = SinOsc.ar(Env([1000, 69, 60], [0.015, 0.1], \exp).ar * [-0.1, 0, 0.1].midiratio);
       snd = Mix(snd);
       snd = (snd * 10).tanh;
       snd = (snd * 5.1).atan;
       snd = BPeakEQ.ar(snd, XLine.kr(2000, 100, 0.3), 2.0, 8);
       snd = snd + DelayC.ar(snd, 0.01, SinOsc.ar(4).range(0.0, 0.001));
       snd = RLPF.ar(snd, 8000, 0.7);
       snd = snd * EnvGen.kr(Env([0, 1, 0.7, 0.7, 0], [0.001, 0.01, 0.3, 0.02]),doneAction:1);
       snd = (snd * 0.6).clip(-1, 1);
       Out.ar(\out.kr(0), Pan2.ar(snd, \pan.kr(0), \amp.kr(0.1)));
   }).store;


   SynthDef(\kickSyn1, {
       var snd;
       snd = DC.ar(0);
       snd = snd + (SinOsc.ar(XLine.ar(800, 400, 0.01)) * Env.perc(0.0005, 0.01).ar);
       snd = snd + (BPF.ar(Hasher.ar(Sweep.ar), XLine.ar(800, 100, 0.01), 0.6) * Env.perc(0.001, 0.02).delay(0.001).ar);
       snd = snd + (SinOsc.ar(XLine.ar(172, 50, 0.01)) * EnvGen.kr(Env.perc(0.0001, 0.3, 1, \lin).delay(0.005),doneAction:1));
       snd = snd.tanh;
       Out.ar(\out.kr(0), Pan2.ar(snd, \pan.kr(0), \amp.kr(0.1)));
   }).store;

   SynthDef(\kickSyn2, {
       var snd;
       snd = DC.ar(0);
       snd = snd + (HPF.ar(Hasher.ar(Sweep.ar), 1320) * Env.perc(0.003, 0.03).ar * 0.5);
       snd = snd + (SinOsc.ar(XLine.ar(750, 161, 0.02)) * Env.perc(0.0005, 0.02).ar);
       snd = snd + (SinOsc.ar(XLine.ar(167, 52, 0.04)) * EnvGen.kr(Env.perc(0.0005, 0.3),doneAction:2));
       snd = snd.tanh;
       Out.ar(\out.kr(0), Pan2.ar(snd, \pan.kr(0), \amp.kr(0.1)));
   }).store;

   SynthDef(\kickSyn3, {
       var snd;
       snd = DC.ar(0);
       snd = snd + (SinOsc.ar(XLine.ar(1500, 800, 0.01)) * Env.perc(0.0005, 0.01, curve: \lin).ar);
       snd = snd + (BPF.ar(Impulse.ar(0) * SampleRate.ir / 48000, 6100, 1.0) * 3.dbamp);
       snd = snd + (BPF.ar(Hasher.ar(Sweep.ar), 300, 0.9) * Env.perc(0.001, 0.02).ar);
       snd = snd + (SinOsc.ar(XLine.ar(472, 60, 0.045)) * EnvGen.kr(Env.perc(0.0001, 0.3, curve: \lin).delay(0.005),doneAction:2));
       snd = snd.tanh;
       Out.ar(\out.kr(0), Pan2.ar(snd, \pan.kr(0), \amp.kr(0.1)));
   }).store;



)
