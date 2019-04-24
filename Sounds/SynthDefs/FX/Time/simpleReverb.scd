SynthDef(\reverb, {
	arg
	inBus=0,
	outBus=0,
	wet=0.5,
	room=0.3,
	damp=0.3
	amp=1;

	var sig;
	var targetRoom, targetDamp, lag=0.3;
	var dsp, mix;

	sig = In.ar(inBus) * 1.25;

	dsp = FreeVerb.ar( sig, 1, room, damp );

	mix = (sig * (1-wet)) + (dsp * wet);

	ReplaceOut.ar( outBus, Pan2.ar( mix * amp.linlin(0,1,0,1.5), 0 ) );

}).store;
