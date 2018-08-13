Kick
{

	classvar synth;

	*new {
		synth = Synth(\kick);
	}

	*play {|note|
		if( note == nil, {
			synth.set(\t_trig,1);
		}, {
			synth.set(\note,note,\t_trig,1);
		})
	}

	*ar {|t_trig=1|

		var sig,cmp;
        sig = LPF.ar( SinOsc.ar( 60, pi/2 ) * 8, 500 );
        sig = sig * EnvGen.kr(Env.perc(1/20,1/2),t_trig,doneAction: 1);
        cmp = CompanderD.ar(sig, thresh: -20.dbamp, slopeBelow: 1, slopeAbove: 0.3, clampTime: 0.003, relaxTime: 0.08) ! 2;
        ^Out.ar(0,Pan2.ar(cmp * (10.dbamp * 0.25),0));

	}
	// *play
	*caos {|att=0.01, rel=0.5, modFreq=1, modbw=0.1, freq1=60, freq2=66, lowcutfreq=50,  t_trig=0, amp1=0.75, amp2=0.75|
			var kick,env;
			kick=RHPF.ar(LFTri.ar(Pulse.ar(modFreq,modbw,freq1,freq2),0,amp1/2)+
				SinOsc.ar(Mix(60,82,280),0,amp2/2)+
				LFTri.ar(Pulse.ar(modFreq,modbw,freq1,freq2),0,amp2/3),lowcutfreq,0.75);
			kick=CompanderD.ar(kick,0.5,0.59,0.8,0.01,0.52);
			env=EnvGen.ar(Env.perc(att,rel),t_trig,doneAction:0);
			^Pan2.ar(kick*env,[-1,0.98]);
		}

	*caos2{|att= 0.01, rel= 0.5, modFreq= 1, modbw= 0.1, bw= 0.1, freq1= 60, freq2= 62, lowcutfreq= 50,  t_trig= 1, amp= 1|
		var kick,env;
   	     		kick=RHPF.ar(
						 Pulse.ar(
						     	Pulse.ar(modFreq,modbw,freq1,freq2),
						 bw,amp/4),
        		    lowcutfreq,0.5);
        		kick=CompanderD.ar(kick,0.6,0.59,0.8);
        		env=EnvGen.ar(Env.perc(att,rel),t_trig,doneAction:2);
    		^Pan2.ar(kick*env,[-1,0.98]);
		}

}
