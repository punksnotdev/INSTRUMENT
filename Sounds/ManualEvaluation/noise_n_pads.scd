
SynthDef(\fluteGlitch, {

	arg
	out=0,
	freq=150,
	gate=1;

	var sig, env, brown;

	brown = BrownNoise.kr;

	sig =  BPF.ar(WhiteNoise.ar(0.5) + SinOsc.ar(freq) * brown, freq, 10);

	sig=sig.tanh;

	env = EnvGen.kr(Env.adsr(0.5, 0.05, 1, 3),gate,doneAction:2);

	Out.ar(out, (Pan2.ar(sig * env) / 2));

}).store;



SynthDef(\noiseLead_XY, {

	arg
	out=0,
	freq=220,
	pan=0,
	amp=1;

	var sig;

	sig =  Resonz.ar( GrayNoise.ar(0.5) + SinOsc.ar(freq, 0, 0.5) * MouseX.kr(1, 1000, 50), freq*[1,5],MouseY.kr(0.001,0.1,0.4,0.5))*100 ;

	sig = Mix.new(sig.distort);

	Out.ar(out, Pan2.ar( sig * amp, pan) / 2);

}).store;




SynthDef(\padCrickets, {
    |freq=331.0, decayTime=0.5, amp=0.5, gate=1, out=0, done=2, pan=0, att=0.3, rel=1.3, q=0.06,legato=0.2|

  var sig, env;

  sig = Ringz.ar(Impulse.ar([12,16]),freq*[1/2,2,3],LFNoise0.kr([8,12],[0.2,0.05],[0.21,0.051]));
  sig = sig * LFNoise2.kr(3,4,4.1);
  sig = sig.tanh;

  env = EnvGen.kr( Env.perc(att,rel), gate, doneAction: 2 );
  sig = sig * (amp*env);

  pan = SinOsc.kr(1/8);

  sig = Mix.new(sig);

  Out.ar( out, Pan2.ar( sig, pan ));

}).store;




SynthDef(\padShineDestroy, {

	arg freq=440,
	t_trig=1,
	res=10,
	att=1,
	decay=3,
	rel=6;


	var sig, env, filter_env;

	sig = LFPulse.ar(freq+[1,-1],[0,3]/8,LFSaw.kr(1).linlin(-1,1,0,1));
	sig = sig + SinOsc.ar((freq)/2,0,0.6);
	sig = sig + Saw.ar((freq)/2);

	sig = (Mix.new(sig)*3.3).tanh;

	filter_env = EnvGen.kr(Env.perc(att,decay/4),t_trig);

	sig = Mix.new(RLPF.ar(sig,[freq*[3,4],filter_env.linlin(0,1,100,5000)],res.reciprocal*filter_env.linlin(0,1,0.1,1)));


	env = EnvGen.kr(Env.adsr(att,decay,1,rel),t_trig,doneAction:2);


	sig = sig * SinOsc.kr(4).linlin(-1,1,0.3,[1,1.3]);
	sig = sig.tanh;

	sig = sig * env;

	sig = sig+Mix.new(CombC.ar(sig,3,[1/3,1/2,1/4],2));


	sig = (sig * 1.2).tanh*8;

	Out.ar(0,Pan2.ar(sig));


}).store;



SynthDef(\distPad1, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*4,0.05)/2;

	sig = RLPF.ar(sig + ( LFSaw.ar(freq) * 33.33 ).tanh, freq*2, 0.35 ).tanh / 4;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;




SynthDef(\distPad2, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*8,0.05)/2;

	sig = RLPF.ar(( (sig + LFSaw.ar(freq)) * 333.33 ).tanh, freq, 0.05 );

	sig = (sig*1.3).tanh / 3;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;



SynthDef(\distPad3, {
|freq=440.0, decayTime=0.5, amp=0.2, temp=100, out=0, done=2, pan=0, att=1.5, sus=0.1, rel=3.5|

	var sig, env;

	sig = RLPF.ar(WhiteNoise.ar(LFNoise2.kr(3,3,100)).tanh,freq*4,0.05)/2;

	sig = RLPF.ar(
		(sig + LFSaw.ar(freq/4)),
		freq, 0.05
	);

	sig = (sig * 1.33).tanh / 4;
	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, sig * amp );

}).store;
