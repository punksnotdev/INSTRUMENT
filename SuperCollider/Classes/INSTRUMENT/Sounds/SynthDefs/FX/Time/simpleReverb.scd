SynthDef(\reverb, {

	arg
	inBus=0, outBus=0, wet=1,

	room=0.3,
	damp=0.3,
	roomdamp
	;

	var sig;
	var targetRoom, targetDamp;
	var dsp, mix;

	sig = In.ar(inBus);

	targetRoom=room;
	targetDamp=damp;

	if( roomdamp.notNil ) {
		targetRoom=roomdamp;
		targetDamp=roomdamp;
	};

	dsp = (FreeVerb.ar( sig, 1, targetRoom, targetDamp ) * 3).tanh;

	mix = (sig * (1-wet)) + (dsp * wet);

	Out.ar( outBus, Pan2.ar( mix, 0 ) );

}).store;
