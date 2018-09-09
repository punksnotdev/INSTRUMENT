s.boot;
(
	i=INSTRUMENT();

	i.midi=true;

)
(

MIDIIn.connect(0, MIDIClient.sources[4] );
m = i.controllerManager.midi;
m.addDevice( MIDIClient.sources[4] );



i.midi.midi_mix.addControllerGroup(\channel,\channel1);
i.midi.midi_mix.addControllerGroup(\channel,\channel2);

i.midi.midi_mix.groups.channel1.addControllerGroup(\cc,\fader);
i.midi.midi_mix.groups.channel1.addControllerGroup(\cc,\knobs);

i.midi.midi_mix.groups.channel2.addControllerGroup(\cc,\fader);
i.midi.midi_mix.groups.channel2.addControllerGroup(\cc,\knobs);


i.midi.midi_mix.groups.channel1.groups.fader.addController(\fader0,19,[0,100]);

i.midi.midi_mix.groups.channel1.groups.knobs.addController(\knob0,16,[0,100]);
i.midi.midi_mix.groups.channel1.groups.knobs.addController(\knob1,17,[0,100]);
i.midi.midi_mix.groups.channel1.groups.knobs.addController(\knob2,18,[0,100]);

i.midi.midi_mix.groups.channel2.groups.fader.addController(\fader0,23,[0,100]);

i.midi.midi_mix.groups.channel2.groups.knobs.addController(\knob0,16,[0,100]);
i.midi.midi_mix.groups.channel2.groups.knobs.addController(\knob1,17,[0,100]);
i.midi.midi_mix.groups.channel2.groups.knobs.addController(\knob2,18,[0,100]);


m = ();

m.channel1=();
m.channel1.knobs=List.new;
m.channel1.faders=List.new;

m.channel2=();
m.channel2.knobs=List.new;
m.channel2.faders=List.new;
// controller mapping:

m.channel1.faders.add( i.midi.midi_mix.groups.channel1.groups.fader.controllers.fader0 );
m.channel1.knobs.add( i.midi.midi_mix.groups.channel1.groups.knobs.controllers.knob0 );
m.channel1.knobs.add( i.midi.midi_mix.groups.channel1.groups.knobs.controllers.knob1 );
m.channel1.knobs.add( i.midi.midi_mix.groups.channel1.groups.knobs.controllers.knob2 );

m.channel2.faders.add( i.midi.midi_mix.groups.channel2.groups.fader.controllers.fader0 );
m.channel2.knobs.add( i.midi.midi_mix.groups.channel2.groups.knobs.controllers.knob0 );
m.channel2.knobs.add( i.midi.midi_mix.groups.channel2.groups.knobs.controllers.knob1 );
m.channel2.knobs.add( i.midi.midi_mix.groups.channel2.groups.knobs.controllers.knob2 );








z = SynthPlayer(\bass,\tranceBazz);
z.seq(\note,0,"0 3 2 7");
y = SynthPlayer(\bass2,\tranceBazz);
y.seq(\note,0,"10 13 12 19",[\speed,2]);

a=[z,y];
p = [ [\att,[0,1]], [\rel,[
0.3,3]], [\rq,[0.05,0.1]] ];


i.map( m.channel1.faders[0], a[0], \amp, [0,1] );
i.map( m.channel2.faders[0], a[1], \amp, [0,1] );

m.channel1.knobs.collect({|ctl,j|
	i.map(ctl,a[0],p[j][0],p[j][1])
});

m.channel2.knobs.collect({|ctl,j|
	i.map(ctl,a[1],p[j][0],p[j][1])
});


z.octave=4;

)
