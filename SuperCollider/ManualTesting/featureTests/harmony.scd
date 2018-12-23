(
	s.boot;
	s.doWhenBooted({

		i=INSTRUMENT();
		p=ProxySpace.push(s);
		p.makeTempoClock(166/120);

		~z.play;

		~z={|notes=#[48,51,55,48]|
			Mix.new(CombN.ar(
				(
					Decay2.kr(Impulse.kr(~tempo.kr/[2,3,4],[0,3,5]/8),2,4)
					*
					(SinOsc.ar(notes.midicps*2))
					* 4
				).tanh / 8
			, 1, 1/([3,8]+SinOsc.kr(~tempo.kr*6,0,0.00015)),4 )
			) ! 2
		};

		i[\harmony] = Proxy(~z);
		i[\harmony].octave=5;


			i[\harmony][0].seq(\chord,[
				C(3,\sus4,2,[12]),
				\r,
				C(7,\M,0,[12]),
				\r,
				\r,
				C(10,\sus4,1,[12]),
				\r,
			]);

		i[\harmony][1].seq(\chord,[
			C(3,\sus4,1,[12]),
			C(2,\sus4,1,[12]),
			C(0,\Mmaj7,2,[12]),
			\r,
			C(5,\M,0,[12]),
			\r,
		],[\repeat,8,\speed,2]);

		i[\harmony][2].seq(\chord,[
			C(3,\sus4,2,[12]),
			C(2,\sus4,1,[12])
		],[\speed,1/2]);

		i[\harmony][3].seq(\chord,[
			C(3,\mmaj7),
			C(3,\Mmaj7),
		],[\speed,1/2]);

	});
)
