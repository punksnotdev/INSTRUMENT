SynthDef(\reverb, {

	arg
	inBus=0, outBus=0, wet=0.5,

	room=0.3,
	damp=0.3,
	roomdamp,
	amp=1
	;

	var sig;
	var targetRoom, targetDamp, lag=0.3;
	var dsp, mix;

	sig = In.ar(inBus) * 0.75;

	targetRoom=room;
	targetDamp=damp;

	if( roomdamp.notNil ) {
		targetRoom=Lag2.kr(roomdamp,lag);
		targetDamp=Lag2.kr(roomdamp,lag);
	};

	dsp = FreeVerb.ar( sig, 1, targetRoom, targetDamp );

	mix = (sig * (1-wet)) + (dsp * wet);

	Out.ar( outBus, Pan2.ar( mix * amp.linlin(0,1,0,1.5), 0 ) );

}).store;
