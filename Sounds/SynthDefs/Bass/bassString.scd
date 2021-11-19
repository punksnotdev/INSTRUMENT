SynthDef(\bassString, {arg out=0, freq=440, pan=0, amp=0.3, gain=10,rel=1.5;
	var pluck, period, string;
	var f1,f2,f3,f4,f5;
	pluck = PinkNoise.ar(Decay.kr(Impulse.kr(0.005), 0.05));
	period = freq.reciprocal * 2;
	string = CombL.ar(pluck, period, period, rel*6);
	string = LeakDC.ar(LPF.ar(string, 12000)) * amp;

	string = (string*(10+gain)).tanh;

	f1=BBandPass.ar( string, 80, 1/4, 500);
	f2=BBandPass.ar( string, 250, 1/3, 104);
	f3=BBandPass.ar( string, 750, 1/3, 37);
	f4=BBandPass.ar( string, 2500, 1/5, 13);
	f5=BBandPass.ar( string, 4500, 1, 0.1);

	string = LPF.ar(f1+f2+f3+f4+f5,6000) + (RLPF.ar(string.tanh,6000,1/2)/8) + (string/3);

	string = HPF.ar( string, 60 );
	string = (string*gain).tanh;

	string = CompanderD.ar(string,3/4,1,1/2,0.01,0.3);


	string = string * EnvGen.kr( Env.perc(0.05,rel), doneAction:2);
	Out.ar(out, Pan2.ar(string*amp, pan))
}).store;
