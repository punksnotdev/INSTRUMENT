SynthDef(\bassSmooth, {
    |gain=1,freq=331.0, decayTime=0.5, amp=2, gate=1, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.3, filterHz=1, q=0.06,legato=0.2|

  var sig, env;

  sig = Pulse.ar(Lag2.kr(freq,legato));
  sig = sig + (LFSaw.ar(Lag2.kr(freq,legato)/2)*3).tanh;

  sig = sig+ RLPF.ar((sig*LFNoise2.kr(3, 1, 1.6)).tanh,filterHz*freq*[2.5,3.3],q.linlin(0,1,0.05,0.5))/3;

  env = EnvGen.kr( Env.asr(att,sus,rel), gate, doneAction: 2 );
  sig = (sig*gain.linlin(0,1,1,18)).tanh * (amp*env);

  Out.ar( out, Pan2.ar( sig/1.25, pan ));

}).store;
