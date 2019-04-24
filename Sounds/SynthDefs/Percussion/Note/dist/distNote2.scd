SynthDef(\distNote2, {
|freq=440.0, decayTime=0.5, amp=0.2, gain=1, out=0, done=2, pan=0, att=0.001, sus=0.05, rel=0.8, q=0.01|

var sig, env;

sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*[4,6],q)/2;

sig = RLPF.ar(sig + ( LFSaw.ar(freq) * 1.33 * gain ).tanh, freq, 0.2 ).tanh / 4;

env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

sig = sig * env;

Out.ar( out, sig * amp );

}).store
