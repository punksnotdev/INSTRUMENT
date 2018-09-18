s.boot;
s.doWhenBooted({
Tdef(\start, { 1.do {
	i=INSTRUMENT();

	i.midi=true;

3.wait;


MIDIIn.connect(0, MIDIClient.sources[3] );
m = i.controllerManager.midi;
m.addDevice( MIDIClient.sources[3] );

i.midi.usb_axiom_25.addControllerGroup(\note,\piano);
i.midi.usb_axiom_25.groups.piano.addController(\piano,nil,0);

i.midi.usb_axiom_25.addControllerGroup(\note,\pads);
i.midi.usb_axiom_25.groups.pads.addController(\pads,nil,9);

i.midi.usb_axiom_25.addControllerGroup(\cc,\knobsA);

i.midi.usb_axiom_25.groups.knobsA.addController(\knobA1,1);
i.midi.usb_axiom_25.groups.knobsA.addController(\knobA2,2);
i.midi.usb_axiom_25.groups.knobsA.addController(\knobA3,3);
i.midi.usb_axiom_25.groups.knobsA.addController(\knobA4,4);


i.midi.usb_axiom_25.addControllerGroup(\cc,\knobsB);

i.midi.usb_axiom_25.groups.knobsB.addController(\knobB1,5);
i.midi.usb_axiom_25.groups.knobsB.addController(\knobB2,6);
i.midi.usb_axiom_25.groups.knobsB.addController(\knobB3,7);
i.midi.usb_axiom_25.groups.knobsB.addController(\knobB4,8);

m = ();
m.piano=i.midi.usb_axiom_25.groups.piano.controllers.piano;
m.pads=i.midi.usb_axiom_25.groups.pads.controllers.pads;


m.knobA1=i.midi.usb_axiom_25.groups.knobsA.controllers.knobA1;
m.knobA2=i.midi.usb_axiom_25.groups.knobsA.controllers.knobA2;
m.knobA3=i.midi.usb_axiom_25.groups.knobsA.controllers.knobA3;
m.knobA4=i.midi.usb_axiom_25.groups.knobsA.controllers.knobA4;


m.knobB1=i.midi.usb_axiom_25.groups.knobsB.controllers.knobB1;
m.knobB2=i.midi.usb_axiom_25.groups.knobsB.controllers.knobB2;
m.knobB3=i.midi.usb_axiom_25.groups.knobsB.controllers.knobB3;
m.knobB4=i.midi.usb_axiom_25.groups.knobsB.controllers.knobB4;


z = SynthPlayer(\bass,\mono_bassSmooth, \mono);
y = SynthPlayer(\perc,\dist);

i.map( m.piano, z, \note );
i.map( m.pads, y, \note );

i.map( m.knobA1, z, \att,[0.01,2] );
i.map( m.knobA2, z, \rel, [0.1,3] );
i.map( m.knobA3, z, \q, [0.01,0.1] );
i.map( m.knobA4, z, \legato, [0,2] );

i.map( m.knobB1, y, \att,[0.01,2] );
i.map( m.knobB2, y, \rel, [0.1,3] );
i.map( m.knobB3, y, \q, [0.005,0.1] );
i.map( m.knobB4, y, \gain, [1,4] );

z.octave=0;

} }).play;

});
