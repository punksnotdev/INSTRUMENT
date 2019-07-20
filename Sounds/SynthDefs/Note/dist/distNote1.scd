SynthDef(\distNote1, {
|freq=440.0, decayTime=0.5, amp=0.2, gain=1, gain1=3, gain1LFORate=3, out=0, done=2, pan=0, att=0.001, sus=0.05, rel=0.6, q=0.01, q2=1,detune=0|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(gain1LFORate,gain1,3)).tanh,(freq+LFNoise2.kr(gain1LFORate,[3,5]*detune))*4*gain1,q);

	sig = RLPF.ar(sig + ( LFSaw.ar(freq) * (3.33 * (gain*SinOsc.kr( gain1LFORate,0,0.5,0.5 ))) ).tanh, freq, q2 ).tanh / 2;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;
