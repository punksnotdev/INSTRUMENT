SynthDef(\kickNoise, {|amp=0.5, out=0|

    var sig, env;

    sig = SinOsc.ar(60,pi/2) ;
    sig = sig+(Saw.ar(Line.kr(18000,40,1/3),pi/6) / 4);
    sig = sig * EnvGen.kr( Env.perc(1/20,1/3), doneAction: 2);

    sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1/8),Array.geom(7,90,53),0.5)).tanh * EnvGen.kr( Env.perc(1/20,1/4) ))/5;

    sig = HPF.ar( sig, 60 );

    sig = RLPF.ar( sig, (EnvGen.kr( Env.perc(0.01,0.25),1)*6666)+60,1/3);

    sig = Mix.new((sig*1.3).tanh);


    Out.ar( out, Pan2.ar(sig * amp,0) );

}).store;
