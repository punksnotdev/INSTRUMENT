Hats
{
	*play
	{
		^ (
			EnvGen.kr(Env.perc(0.01,0.3),doneAction: 2)
			*
			Mix(Resonz.ar(Decay2.ar(WhiteNoise.ar(5.3),0.01,1/6),144*(1..3)*Array.geom(6,1,2.4),0.1).tanh)
		)
	}
}
