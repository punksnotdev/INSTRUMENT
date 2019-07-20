SynthDef(\strings, { arg out, freq=440, amp=0.8, gate=1, pan, freqLag=0.2;
					var env, in, delay, f1, f2;
					f1 = freq.lag(freqLag);
					f2 = freq.lag(freqLag * 0.5);
					delay = 0.25 / f2;
					env = Env.perc(0.2, 1, 0.3);
					in = WhiteNoise.ar(180);
					in = CombL.ar(in, delay, delay, 1);
					in = Resonz.ar(in, f1, 0.001).abs;
					in = in * EnvGen.kr(env, doneAction:2);
					Out.ar(out, Pan2.ar(in, pan, amp));

//From SC Examples Folder
// some small pieces
// Julian Rohrhuber, 2007
}).store;
