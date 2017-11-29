ProbClock
{
	*kr
	{|rate=1,probability=1,phase=0|
		CoinGate.kr( probability, Impulse.kr( rate, phase ) )
	}
}
