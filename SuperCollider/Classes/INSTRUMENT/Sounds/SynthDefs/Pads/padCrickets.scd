SynthDef(\padCrickets, {
    |freq=331.0, decayTime=0.5, amp=0.5, gate=1, out=0, done=2, pan=0, att=0.3, rel=1.3, q=0.06,legato=0.2|

  var sig, env;

  sig = Ringz.ar(Impulse.ar([12,16]),freq*[1/2,2,3],LFNoise0.kr([8,12],[0.2,0.05],[0.21,0.051]));
  sig = sig * LFNoise2.kr(3,4,4.1);
  sig = sig.tanh;

  env = EnvGen.kr( Env.perc(att,rel), gate, doneAction: 2 );
  sig = sig * (amp*env);

  pan = SinOsc.kr(1/8);

  sig = Mix.new(sig);

  Out.ar( out, Pan2.ar( sig, pan ));

}).store;
