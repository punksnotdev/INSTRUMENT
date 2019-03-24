(
p=ProxySpace.push(s);
	~c.play;
~a = { Impulse.kr([2,3],[0,2,9,12]/16)};
~b = { CoinGate.kr(3/[4,5],\in.kr(0!4))};
~c = { ((BPF.ar(WhiteNoise.ar(1/2),(50+[0,1,4,5]).midicps,0.001) * Decay2.kr(\in.kr(0!4)))* 144).tanh ! 2 };

~a<>>~b<>>~c

)
