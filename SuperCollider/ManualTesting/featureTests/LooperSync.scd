s.boot;
l=I8TLooper();

~shouldStartRecording=false;

~shouldStopRecording=false;

~shouldStartRecording=true;
~shouldStopRecording=true;

Tdef(\syncLoop, {
// l.play;
	inf.do{|i|
		// if(i%16==0) {
			Synth(\dist);

			if( ~shouldStartRecording == true ) {
				l.rec;
				~isRecording = true;
				~shouldStartRecording = false;
			};
			if( ~shouldStopRecording == true ) {
				l.play;
				~isRecording = false;
				~shouldStopRecording = false;

			};
		// };
		// if(i%4==2) {
		// 	l.play;
		// };
		(1).wait;
	}


}).play;

l.stop


i=INSTRUMENT()
i.play;
i[\kick].play;
i[\hihat].play;
i[\kick].stop;
i[\hihat].stop;
i[\kick]=SynthPlayer(\kickElectro)
i[\kick].seq(":0.5 0.5")
i[\hihat]=SynthPlayer(\hihatElectroKit)
i[\hihat].seq(":0.125 0.3 0.3 ")
