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

i.midi.usb_axiom_25.groups.knobsA.addController(\knob1,1);
i.midi.usb_axiom_25.groups.knobsA.addController(\knob2,2);
i.midi.usb_axiom_25.groups.knobsA.addController(\knob3,3);
i.midi.usb_axiom_25.groups.knobsA.addController(\knob4,4);


i.midi.usb_axiom_25.addControllerGroup(\cc,\knobsB);

i.midi.usb_axiom_25.groups.knobsB.addController(\knob1,1);
i.midi.usb_axiom_25.groups.knobsB.addController(\knob2,2);
i.midi.usb_axiom_25.groups.knobsB.addController(\knob3,3);
i.midi.usb_axiom_25.groups.knobsB.addController(\knob4,4);

//
//
m = ();
//
m.piano=i.midi.usb_axiom_25.groups.piano.controllers.piano;
m.pads=i.midi.usb_axiom_25.groups.pads.controllers.pads;

m.knob1=i.midi.usb_axiom_25.groups.knobsA.controllers.knob1;
m.knob2=i.midi.usb_axiom_25.groups.knobsA.controllers.knob2;
m.knob3=i.midi.usb_axiom_25.groups.knobsA.controllers.knob2;
m.knob4=i.midi.usb_axiom_25.groups.knobsA.controllers.knob2;
//
//
z = SynthPlayer(\bass,\mono_bassSmooth, \mono);
y = SynthPlayer(\perc,\dist);

i.map( m.piano, z, \note );
i.map( m.pads, y, \note );

i.map( m.knob1, z, \att );
i.map( m.knob2, z, \rel );
i.map( m.knob3, z, \cutoff );
i.map( m.knob4, z, \q );

z.octave=0;

} }).play;

});
