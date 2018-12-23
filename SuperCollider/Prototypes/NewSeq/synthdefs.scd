(


		SynthDef(\bassSynth,{
			arg note=60, amp=1;
			var sig = Saw.ar(note.midicps/2)+SinOsc.ar(note.midicps,pi/2);
			var env = EnvGen.kr(Env.perc(0.02,0.3),doneAction: 2);
			var filterEnv = EnvGen.kr(Env.perc(0,0.2));
			sig = RLPF.ar(sig,filterEnv.linlin(0,1,200,3000),0.1);
			sig = (sig*3.3).tanh;
			Out.ar(0,Pan2.ar(sig*env*amp));
		}).add;

		SynthDef(\noteSynth,{
			arg note=60, amp=1;
			var sig = SinOsc.ar(note.midicps,pi/2)/2;
			var env = EnvGen.kr(Env.perc,doneAction: 2);
			Out.ar(0,Pan2.ar(sig*env*amp));
		}).add;

		SynthDef(\kick,{
			arg note=60, amp=1;
			var filterEnv = EnvGen.kr(Env.perc(0,0.02));
			var sig = SinOsc.ar(60,pi/2,0,1.3).tanh+RLPF.ar(WhiteNoise.ar(1/8),filterEnv.linlin(0,1,64,10000),0.01);
			var env = EnvGen.kr(Env.perc(0.01,0.2),doneAction: 2);
			sig = (sig * 3.3).tanh;

			Out.ar(0,Pan2.ar(sig*env*amp));
		}).add;

)
