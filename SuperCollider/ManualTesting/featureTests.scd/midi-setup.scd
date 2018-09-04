s.boot;
(
	i=INSTRUMENT();

	i.midi=true;

)
(

MIDIIn.connect(0, MIDIClient.sources[3] );
m = i.controllerManager.midi;
m.addDevice( MIDIClient.sources[3] );


i.midi.midi_mix.addControllerGroup(\channel,\channel1);
i.midi.midi_mix.addControllerGroup(\channel,\channel2);

i.midi.midi_mix.groups.channel1.addControllerGroup(\cc,\fader);
i.midi.midi_mix.groups.channel1.addControllerGroup(\cc,\knobs);

i.midi.midi_mix.groups.channel2.addControllerGroup(\cc,\fader);
i.midi.midi_mix.groups.channel2.addControllerGroup(\cc,\knobs);



// controller mapping:

i.midi.midi_mix.groups.channel1.groups.knobs.addController(\knob0,16,[0,100]);
i.midi.midi_mix.groups.channel1.groups.knobs.addController(\knob1,17,[0,100]);
i.midi.midi_mix.groups.channel1.groups.knobs.addController(\knob2,18,[0,100]);

i.midi.midi_mix.groups.channel1.groups.fader.addController(\fader0,19,[0,100]);

i.midi.midi_mix.groups.channel2.groups.knobs.addController(\knob0,20,[0,100]);
i.midi.midi_mix.groups.channel2.groups.knobs.addController(\knob1,21,[0,100]);
i.midi.midi_mix.groups.channel2.groups.knobs.addController(\knob2,22,[0,100]);

i.midi.midi_mix.groups.channel2.groups.fader.addController(\fader0,23,[0,100]);


z = SynthPlayer(\bass,\tranceBazz);
z.seq(\note,0,"0 3 2 7");
y = SynthPlayer(\bass2,\tranceBazz);
y.seq(\note,0,"10 13 12 19",[\speed,2]);

i.map(
	i.midi.midi_mix.groups.channel1.groups.fader.controllers.fader0,
	z,
	\amp,
	[0,1]
);
i.map(
	i.midi.midi_mix.groups.channel1.groups.knobs.controllers.knob0,
	z,
	\att,
	[0,1]
);
i.map(
	i.midi.midi_mix.groups.channel1.groups.knobs.controllers.knob1,
	z,
	\rel,
	[0,3]
);

i.map(
	i.midi.midi_mix.groups.channel1.groups.knobs.controllers.knob2,
	z,
	\rq,
	[0.05,0.1]
);



i.map(
	i.midi.midi_mix.groups.channel2.groups.fader.controllers.fader0,
	y,
	\amp,
	[0,1]
);
i.map(
	i.midi.midi_mix.groups.channel2.groups.knobs.controllers.knob0,
	y,
	\att,
	[0,1]
);
i.map(
	i.midi.midi_mix.groups.channel2.groups.knobs.controllers.knob1,
	y,
	\rel,
	[0,3]
);

i.map(
	i.midi.midi_mix.groups.channel2.groups.knobs.controllers.knob2,
	y,
	\rq,
	[0.05,0.1]
);

)

z.octave=3
