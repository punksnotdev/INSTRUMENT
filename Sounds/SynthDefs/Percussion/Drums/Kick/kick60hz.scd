SynthDef(\kick60hz, {|amp=0.5, out=0,t_trig=1, rel=0.5|

    var sig, env;

    sig = SinOsc.ar(60,pi/2)*1.5 ;
    sig = sig+(Saw.ar(XLine.kr(18000,40,1/1000),pi/3) *2).tanh/10;


    sig = LPF.ar(HPF.ar( sig, 30 ),70) + HPF.ar( sig, 30 ) + HPF.ar( sig, 60 );

    sig = RLPF.ar( sig, (EnvGen.kr( Env.perc(0.001,0.0015),t_trig)*10666)+60,1/100 );

    sig = Mix.new((sig*1.3).tanh);


    sig = sig+((RLPF.ar((sig)+WhiteNoise.ar(1/8),Array.geom(7,90,23),0.05)).tanh * EnvGen.kr( Env.perc(0,1/200) ));


    sig = sig * EnvGen.kr( Env.perc(0.001,rel), doneAction: 2)/3;

    Out.ar( out, Pan2.ar(sig * amp,0) );

}).store;
