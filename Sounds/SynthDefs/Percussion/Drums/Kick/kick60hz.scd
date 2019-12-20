SynthDef(\kick60hz, {|amp=0.5, out=0,t_trig=1, rel=0.5|

    var sig, env;

    sig = SinOsc.ar(60,pi/2)*1.5 ;
    sig = sig+(Saw.ar(Line.kr(18000,40,1/50),pi/3) * 1/8).tanh;


    sig = HPF.ar( sig, 40 );

    sig = RLPF.ar( sig, (EnvGen.kr( Env.perc(0.01,0.015),t_trig)*10666)+60,1/100 );

    sig = Mix.new((sig*1.3).tanh);


    sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1/8),Array.geom(7,90,23),0.5)).tanh * EnvGen.kr( Env.perc(1/30,1/4) ));


    sig = sig * EnvGen.kr( Env.perc(1/20,rel), doneAction: 2)/3;

    Out.ar( out, Pan2.ar(sig * amp,0) );

}).store;
