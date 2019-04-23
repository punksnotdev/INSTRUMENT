(

    SynthDef(\bassWobbleRing, {
        |freq=331.0, decayTime=0.5, amp=0.25, gate=1, out=0, done=2, pan=0, att=0.3, rel=1.3, q=0.06,legato=0.02|

      var sig, env;

      sig = RLPF.ar(3*Blip.ar(freq,SinOsc.kr(3,pi/2,99,101)),Lag2.kr(freq*[1,2],legato),[0.001,0.1]);
      sig = sig + (LFSaw.ar(Lag2.kr(freq,legato)*[2,3])*[13,3]).tanh / 2;

      sig = sig+ (RLPF.ar((WhiteNoise.ar*LFNoise2.kr(3, 1, 1.6)).tanh,freq*[2,3],q)*2.3).tanh;

      env = EnvGen.kr( Env.perc(att,rel), gate, doneAction: 2 );
      sig = Mix.new((sig/12).tanh) * (amp*env);

      Out.ar( out, Pan2.ar( sig/2, pan ));

    }).store;

)
