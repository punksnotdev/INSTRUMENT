(



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

)
