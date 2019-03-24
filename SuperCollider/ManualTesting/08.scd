(
	p = ProxySpace.push(s.boot);

	~z = {|t_trig=1,notes=#[50,60,62,65,69,72],amp=1|Mix.new(Pan2.ar( SinOsc.ar(Lag.kr(notes.midicps,1/4),0,LFNoise2.kr(2,1.30*Lag2.kr(amp,1/8),0.61*Lag2.kr(amp,1/8))).tanh*Decay2.kr(t_trig) / 2)) };
	~z.play;
	~z.set(\t_trig,1);

	INSTRUMENT.new.play;

	z = Proxy.new(\c1,~z);
	z.play;
	z.seq(P(\chord,[C(0,\m),C(7,\Mmaj7)]));

)

(
~z = \setsrc -> Pbind(
    \dur, Prand([1, 0.5, 2], inf),
    \source, Prand ([
        { SinOsc.ar(SinOsc.ar({5.rand}.dup + 4) * 50 + 400 + 50.rand) * 0.1 },
        { SinOsc.ar(LFSaw.ar({5.rand}.dup + 4) * 50 + 400 + 50.rand) * 0.1},
        { LFSaw.ar(SinOsc.ar({5.rand}.dup + 4) * 50 + 400 + 50.rand) * 0.1 },
        { LFSaw.ar(LFSaw.ar({5.rand}.dup + 4) * 50 + 400 + 50.rand) * 0.1 }
    ], inf)
)
);
