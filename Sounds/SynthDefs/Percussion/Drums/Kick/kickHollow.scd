SynthDef(\kickHollow, {|amp=0.5, out=0|

    var sig, env;

    sig = SinOsc.ar(60,pi/2) ;
    sig = sig+(Saw.ar(Line.kr(12000,40,1/123),pi/2) / 12);
    sig = sig * EnvGen.kr( Env.perc(1/20,1/3), doneAction: 2);

    sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1/8),Array.geom(7,90,43),0.5)).tanh/8 * EnvGen.kr( Env.perc(1/20,1/4) ))/8;

    sig = (sig*1.3).tanh!2;


    Out.ar( out, Pan2.ar(sig * amp,0)/2 );

}).store;
