SynthDef(\kickSimple, {|amp=0.5, out=0|
    var sig,cmp;
    sig = LPF.ar( SinOsc.ar( 60, pi/2 ) * 8, 500 );
    sig = sig * EnvGen.kr(Env.perc(1/20,1/4),doneAction: 2);
    cmp = CompanderD.ar(sig, thresh: -20.dbamp, slopeBelow: 1, slopeAbove: 0.3, clampTime: 0.003, relaxTime: 0.08);
    Out.ar(out,Pan2.ar((cmp * (10.dbamp)) * amp,0) ) !  2;

}).store;
