SynthDef(\reverb, {
	arg
	inBus=0,
	outBus=0,
	wet=0.5,
	room=0.75,
	damp=0.75,
	amp=1;

	var sig;
	var targetRoom, targetDamp, lag=0.3;
	var dsp, mix;

	sig = In.ar(inBus) * 2.5;

	dsp = FreeVerb.ar( sig, 1, room, damp );

	mix = (sig * (1-wet)) + (dsp * wet);

	Out.ar( outBus, Pan2.ar( mix * amp.linlin(0,1,0,1.5), 0 ) );

},
variants: (
	infinite: [ wet: 1, room: 0.99, damp: 0.99 ],
	large: [ wet: 0.85, room: 0.85, damp: 0.85 ],
	medium: [ wet: 0.75, room: 1/2, damp: 1/2 ],
	small: [ wet: 0.75, room: 0.05, damp: 0.05 ],
)
).store;
