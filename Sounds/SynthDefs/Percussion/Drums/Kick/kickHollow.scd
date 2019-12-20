SynthDef(\kickHollow, {|amp=0.5, out=0,t_trig=1|

    var sig, env;

    sig = SinOsc.ar(60,pi/2) ;
    sig = sig+(Saw.ar(Line.kr(18000,40,1/50),pi/2) / 12);
    sig = sig * EnvGen.kr( Env.perc(1/20,1/6), doneAction: 2);

    sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1/8),Array.geom(7,90,53),0.05)).tanh * EnvGen.kr( Env.perc(1/20,1/4) ));

    sig = HPF.ar( sig, 40 );

    sig = RLPF.ar( sig, (EnvGen.kr( Env.perc(0.01,0.05),t_trig)*10000)+60,1/2 );


    sig = Mix.new((sig*4.3).tanh);


    Out.ar( out, Pan2.ar(sig * amp,0) );

}).store;
