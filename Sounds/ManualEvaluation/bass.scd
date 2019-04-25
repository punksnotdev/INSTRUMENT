/*

bassTrance1
bassTrance2
bassTrance3
bassDist
mono_bassPhatPhase
bassSimple
bassSinTri
bassSmooth
bassWobbleRing

*/

SynthDef(\bassTrance1, {
|freq=1.0, decayTime=0.5, amp=0.5, gain=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2, rq=0.06, dist=1|

	var sig, env;

	sig = SinOsc.ar(freq)/4;
	sig = sig + Saw.ar(freq/4)/2;
	sig = (sig * 23.33*gain).tanh;
	sig = RLPF.ar(sig,freq*[1,4],rq).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	sig = (sig * dist).tanh;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;


SynthDef(\bassTrance2, {
|freq=1.0, decayTime=0.5, amp=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2|

	var sig, env;

	sig = SinOsc.ar(freq/2)/4;
	sig = sig + Saw.ar(freq/4)/2;
	sig = RLPF.ar(sig,freq*[1,4],0.06);
	sig = (sig * 23.33).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;


SynthDef(\bassTrance3, {
|freq=1.0, decayTime=0.5, amp=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2|

	var sig, env;

	sig = LFPulse.ar(freq/2,LFNoise2.kr(8,0.4,0.5));
	sig = sig + Saw.ar(freq/[4,1/2])/[2,8];
	sig = RLPF.ar(sig,freq*[1,4],0.06);
	sig = (sig * 3.33).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;


SynthDef(\bassDist, {
|freq=1.0, decayTime=0.5, amp=0.5, gain=0.5, temp=100, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.2, rq=0.06, dist=1|

	var sig, env;

	sig = SinOsc.ar(freq)/4;
	sig = sig + Saw.ar(freq/4)/2;
	sig = (sig * 23.33*gain).tanh;
	sig = RLPF.ar(sig,freq*[1,4],rq).tanh;
	// sig = (sig * 23.33).tanh;

	env = EnvGen.kr( Env.perc(att,rel), doneAction: 2 );

	sig = sig * env;

	sig = (sig * dist).tanh;

	Out.ar( out, Pan2.ar(sig * amp / 4, pan) );

}).store;



SynthDef(\mono_bassPhatPhase, {
	|gain=2.1, factor2=1, amp=0.5, gate=1, out=0, freq=331.0, done=2, pan=0, att=0.01, sus=0.3, rel=0.3, q=0.06, legato=0.4, clock=1|

	var lfo;
	var spread;
	var env;

    var lagFreq = Lag2.kr(freq,legato);


	var sig;
    var factor;

    factor = Latch.kr( LFNoise0.kr(clock), Dust.kr(clock)).linexp(-1,1,0.2,12)*factor2;

    lfo = SinOsc.kr(clock*(5*factor),0,3,4);
    spread = SinOsc.kr(clock*4,0,[13,30]).linlin(-1,1,0,factor);


    sig = LFTri.ar(lagFreq+([1,2]))/3
	+
	Saw.ar(lagFreq+([3,4])/2)/4
	+
	LFPulse.ar(
		[
			lagFreq,
			// lagFreq*[1/2,1,3,8],
			Saw.ar(clock+([3,5]*lfo))
			.linlin(0,1,lagFreq,lagFreq+(spread*[3,5]))
		],
		[0,3,5,7]/8,
		lfo/[1,6,14,20]
	) ;

	sig = Mix.new(
		sig/4 ! 1
	) ! 1;


	sig = sig * gain;

	sig = sig.tanh;

    sig = RLPF.ar(sig,lagFreq*[1,8,16],[0.3,0.15*lfo,1]);

	env = EnvGen.kr(Env.asr(att,sus,rel),gate,doneAction:2);

  	sig = sig * (amp*env);


	Out.ar( out, Pan2.ar( sig, pan ));

}).store;




SynthDef(\bassSimple, {|freq=220,amp=0.5, out=0|

	var sig;
	var env;

	sig = Saw.ar(freq);

	sig = sig * EnvGen.kr( Env.perc, doneAction: 2 );

	Out.ar(out, Pan2.ar(sig * amp, 0, amp) / 2);

}).store;



SynthDef(\bassSinTri,
{
	arg
	out=0,
	freq=90;

    var sig, env;

    sig = SinOsc.ar(freq,pi/2,1,0);

	sig = sig + LFTri.ar(freq,4,1,0);

    env = EnvGen.kr(Env.perc(0.005, 1, 2, 0.1),doneAction:2);

  	Out.ar(out, (Pan2.ar( sig * env) / 2));

}).store;




SynthDef(\bassSmooth, {
    |gain=1,freq=331.0, decayTime=0.5, amp=2, gate=1, out=0, done=2, pan=0, att=0.01, sus=0.1, rel=0.3, cutoff=1, q=0.06,legato=0.2|

  var sig, env;

  sig = Pulse.ar(Lag2.kr(freq,legato));
  sig = sig + (LFSaw.ar(Lag2.kr(freq,legato)/2)*3).tanh;

  sig = sig+ RLPF.ar((sig*LFNoise2.kr(3, 1, 1.6)).tanh,cutoff*freq*[2.5,3.3],q.linlin(0,1,0.05,0.5))/3;

  env = EnvGen.kr( Env.asr(att,sus,rel), gate, doneAction: 2 );
  sig = (sig*gain.linlin(0,1,1,18)).tanh * (amp*env);

  Out.ar( out, Pan2.ar( sig/1.25, pan ));

}).store;



SynthDef(\bassWobbleRing, {
	|freq=331.0, decayTime=0.5, amp=0.25, gate=1, out=0, done=2, pan=0, att=0.3, rel=1.3, q=0.06,legato=0.02|

  var sig, env;

  sig = RLPF.ar(3*Blip.ar(freq,SinOsc.kr(3,pi/2,99,101)),Lag2.kr(freq*[1,2],legato),[0.001,0.1]);
  sig = sig + (LFSaw.ar(Lag2.kr(freq,legato)*[2,3])*[13,3]).tanh / 2;

  sig = sig+ (RLPF.ar((WhiteNoise.ar*LFNoise2.kr(3, 1, 1.6)).tanh,freq*[2,3],q)*2.3).tanh;

  env = EnvGen.kr( Env.perc(att,rel), gate, doneAction: 2 );
  sig = Mix.new((sig/12).tanh) * (amp*env);

  Out.ar( out, Pan2.ar( sig/2, pan ));

}).store;
