SynthDef(\reverb, {

	arg
	inBus=0, outBus=0, wet=1,

	room=0.3,
	damp=0.3;

	var sig = In.ar(inBus);

	var dsp = FreeVerb.ar( sig, 1, room, damp );

	var mix = (sig * (1-wet)) + (dsp * wet);

	Out.ar( outBus, Pan2.ar( mix, 0 ) );


}).store;
