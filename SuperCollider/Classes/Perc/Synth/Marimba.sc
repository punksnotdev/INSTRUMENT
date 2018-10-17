Marimba
{
	*ar
	{
		|t_trig, freq=100, sustain=0.5, rq=0.006|

		var env, signal;
		var rho, theta, b1, b2;

		env = EnvGen.kr(Env.adsr(0.0001, sustain, sustain/2, 0.3), t_trig, doneAction:1);

		b1 = 1.987 * 0.9889999999 * cos(0.09);
		b2 = 0.998057.neg;

		signal = SOS.ar(K2A.ar(t_trig), 0.3, 0.0, 0.0, b1, b2);
		signal = RHPF.ar(signal*0.8, freq, rq) + DelayC.ar(RHPF.ar(signal*0.9, freq*0.99999, rq*0.999), 0.02, 0.01223);

		signal = Decay2.ar(signal, 0.4, 0.3, signal);

		^ Mix.new((signal*env)*(0.3));
	}

}
