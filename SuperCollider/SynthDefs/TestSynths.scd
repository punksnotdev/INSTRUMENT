SynthDef(\test,{|note=60,t_trig=0,decay=0.3,distortion=1.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( note.midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh;

	sig = RLPF.ar( sig, cutoff, 0.1);

	Out.ar(0, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\bassTest,{|note=60,t_trig=0,decay=0.3,distortion=2.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( (note-[24,12]).midicps ) * 0.1;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh/4;

	sig = RLPF.ar( sig, cutoff, 0.1 );

	Out.ar(0, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\bassTest2,{|note=60,t_trig=0,decay=0.3,distortion=2.3,cutoff=1500|
	var sig, env;
	sig = SinOsc.ar( (note-[24,12]).midicps ) * 0.3;
	env = EnvGen.kr(Env.perc(1/100,decay),t_trig);
	sig = sig * env;
	sig = (sig * distortion).tanh;

	sig = RLPF.ar( sig, cutoff, 0.3 );

	Out.ar(0, Pan2.ar( sig, 0 ));
}).store;


SynthDef(\reverb, { arg rv1 = 0.7, rv2=0.8;
    ReplaceOut.ar(
        1,
        FreeVerb.ar(
            In.ar(0, 1),
            rv1,
            rv2
        )
    )
}).store;

SynthDef(\distortion, { arg dist = 3;
    ReplaceOut.ar(
        1,
        (In.ar(0, 1)*dist).tanh/2
    )
}).store;


SynthDef(\delay1, { arg delay = 0.25;
    ReplaceOut.ar(
        1,
        DelayN.ar(
            In.ar(0, 1),
            delay,
            delay
        )
    )
}).store;


SynthDef(\delay2, { arg maxdelay = 5;
    ReplaceOut.ar(
        1,
        CombC.ar(
			In.ar(0, 1), maxdelay, LFNoise2.kr(0.3).range(1, maxdelay), 1
		);
    )
}).store;
