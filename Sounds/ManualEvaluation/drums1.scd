SynthDef(\kickDeep, {|amp=0.5, out=0|

    var daNoise,daOsc,env1,env2,env3;

    env1=Env.perc(0.001,1,80,-20);
    env2=Env.perc(0.001,1,1,-8);
    env3=Env.perc(0.001,1,80,-8);

    daNoise=LPF.ar(WhiteNoise.ar(1),EnvGen.kr(env1)+20);

    daOsc=LPF.ar(SinOsc.ar(EnvGen.kr(env3)+20),200);

    Out.ar(out,Pan2.ar(Mix.ar([daNoise,daOsc]) * amp, 0, EnvGen.kr(env2,doneAction: 2))) / 2;

}).store;




SynthDef(\kickFull, {|amp=0.5, out=0|

    var subosc, subenv, suboutput, clickosc, clickenv, clickoutput;

    subosc = {SinOsc.ar(60)};
    subenv = {Line.ar(1, 0, 0.3, doneAction: 2)};

    clickosc = {LPF.ar(WhiteNoise.ar(1),1500)};
    clickenv = {Line.ar(1, 0, 0.02)};

    suboutput = (subosc * subenv);
    clickoutput = (clickosc * clickenv);

    Out.ar(out, Pan2.ar((suboutput + clickoutput) * amp, 0) / 2);

}).store;



SynthDef(\kickGabber, {|amp=0.5, out=0|
   var snd;
   snd = SinOsc.ar(Env([1000, 69, 60], [0.015, 0.1], \exp).ar * [-0.1, 0, 0.1].midiratio);
   snd = Mix(snd);
   snd = (snd * 10).tanh;
   snd = (snd * 5.1).atan;
   snd = BPeakEQ.ar(snd, XLine.kr(2000, 100, 0.3), 2.0, 8);
   snd = snd + DelayC.ar(snd, 0.01, SinOsc.ar(4).range(0.0, 0.001));
   snd = RLPF.ar(snd, 8000, 0.7);
   snd = snd * EnvGen.kr(Env([0, 1, 0.7, 0.7, 0], [0.001, 0.01, 0.3, 0.02]),doneAction: 1);
   snd = (snd * 0.6).clip(-1, 1);
   Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp) / 2);
}).store;


SynthDef(\kickHard, { |basefreq = 50, envratio = 3, freqdecay = 0.02, ampdecay = 0.5, amp=0.5, out=0|
     var fenv = EnvGen.kr(Env([envratio, 1], [freqdecay], \exp), 1) * basefreq,
     aenv = EnvGen.kr(Env.perc(0.005, ampdecay), 1, doneAction: 2);
     Out.ar(out, (SinOsc.ar(fenv, 0.5pi, aenv)  * amp*1.4).distort);
}).store;



SynthDef(\kickHollow, {|amp=0.5, out=0|

    var sig, env;

    sig = SinOsc.ar(60,pi/2) ;
    sig = sig+(Saw.ar(Line.kr(12000,40,1/123),pi/2) / 12);
    sig = sig * EnvGen.kr( Env.perc(1/20,1/3), doneAction: 2);

    sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1/8),Array.geom(7,90,43),0.5)).tanh/8 * EnvGen.kr( Env.perc(1/20,1/4) ))/8;

    sig = (sig*1.3).tanh!2;


    Out.ar( out, Pan2.ar(sig * amp,0)/2 );

}).store;


SynthDef(\kickSyn1, {|amp=0.5, out=0|
    var snd;
    snd = DC.ar(0);
    snd = snd + (SinOsc.ar(XLine.ar(800, 400, 0.01)) * Env.perc(0.0005, 0.01).ar);
    snd = snd + (BPF.ar(Hasher.ar(Sweep.ar), XLine.ar(800, 100, 0.01), 0.6) * Env.perc(0.001, 0.02).delay(0.001).ar);
    snd = snd + (SinOsc.ar(XLine.ar(172, 50, 0.01)) * EnvGen.kr(Env.perc(0.0001, 0.3, 1, \lin).delay(0.005),doneAction: 2));
    snd = snd.tanh;
    Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp));
}).store;


SynthDef(\kickSyn2, {|amp=0.5, out=0|
	var snd;
	snd = DC.ar(0);
	snd = snd + (HPF.ar(Hasher.ar(Sweep.ar), 1320) * Env.perc(0.003, 0.03).ar);
	snd = snd + (SinOsc.ar(XLine.ar(750, 161, 0.02)) * Env.perc(0.0005, 0.02).ar);
	snd = snd + (SinOsc.ar(XLine.ar(167, 52, 0.04)) * EnvGen.kr(Env.perc(0.0005, 0.3),doneAction: 2));
	snd = snd.tanh;
	Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp/1.5));
}).store;


SynthDef(\kickSyn3, {|amp=0.5, out=0|
    var snd;
    snd = DC.ar(0);
    snd = snd + (SinOsc.ar(XLine.ar(1500, 800, 0.01)) * Env.perc(0.0005, 0.01, curve: \lin).ar);
    snd = snd + (BPF.ar(Impulse.ar(0) * SampleRate.ir / 28000, 6100, 1.0) * 3.dbamp);
    snd = snd + (BPF.ar(Hasher.ar(Sweep.ar), 300, 0.9) * Env.perc(0.001, 0.02).ar);
    snd = snd + (SinOsc.ar(XLine.ar(472, 60, 0.045)) * EnvGen.kr(Env.perc(0.0001, 0.3, curve: \lin).delay(0.005),doneAction: 2));
    snd = snd.tanh;
    Out.ar(out, Pan2.ar(snd * amp, \pan.kr(0), amp) / 2);
}).store;
