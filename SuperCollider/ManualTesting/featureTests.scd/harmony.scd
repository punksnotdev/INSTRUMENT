(
	s.boot;
	s.doWhenBooted({

		i=INSTRUMENT();
		p=ProxySpace.push(s);
		p.makeTempoClock(166/120);

		~z.play;

		~z={|notes=#[48,51,55]|
			Mix.new(
				(
					Decay2.kr(Impulse.kr(~tempo.kr/[2,3,4],[0,3,5]/8),2,4)*SinOsc.ar(notes.midicps)
					* 4
				).tanh / 2
			) ! 2
		};

		i[\harmony] = Proxy(~z);

		i[\harmony][0].seq(\chord,[
			C(0,\m,2),
			C(3,\sus4,2),
			\r,
			C(7,\M),
			\r,
			\r,
			C(10,\sus4,1),
			\r,
		]);

		i[\harmony][1].seq(\chord,[
			C(3,\sus4,1),
			C(2,\sus4,1),
			C(0,\m,2),
			\r,
			C(5,\M),
			\r,
		],[\repeat,8]);

		i[\harmony][2].seq(\chord,[
			C(3,\sus4,2),
			C(2,\sus4,1)
		],[\speed,1/2]);

	});
)
