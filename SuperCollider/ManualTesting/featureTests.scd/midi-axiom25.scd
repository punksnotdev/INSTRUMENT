(
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
	i.midi.usb_axiom_25.addControllerGroup(\cc,\ctlPads);

	i.midi.usb_axiom_25.groups.knobsA.addController(\knobA1,1);
	i.midi.usb_axiom_25.groups.knobsA.addController(\knobA2,2);
	i.midi.usb_axiom_25.groups.knobsA.addController(\knobA3,3);
	i.midi.usb_axiom_25.groups.knobsA.addController(\knobA4,4);


	i.midi.usb_axiom_25.addControllerGroup(\cc,\knobsB);

	i.midi.usb_axiom_25.groups.knobsB.addController(\knobB1,5);
	i.midi.usb_axiom_25.groups.knobsB.addController(\knobB2,6);
	i.midi.usb_axiom_25.groups.knobsB.addController(\knobB3,7);
	i.midi.usb_axiom_25.groups.knobsB.addController(\knobB4,8);

	i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad1,9);
	i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad2,10);
	i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad3,11);
	i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad4,12);

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

	m.ctlPad1=i.midi.usb_axiom_25.groups.ctlPads.controllers.ctlPad1;
	m.ctlPad2=i.midi.usb_axiom_25.groups.ctlPads.controllers.ctlPad2;
	m.ctlPad3=i.midi.usb_axiom_25.groups.ctlPads.controllers.ctlPad3;
	m.ctlPad4=i.midi.usb_axiom_25.groups.ctlPads.controllers.ctlPad4;


	z = SynthPlayer(\bass,\mono_bassSmooth, \mono);
	y = SynthPlayer(\perc,\dist);
	// y.seq(\note,0,"0  3 2 8");


	i.map( m.piano, z, \note );
	i.map( m.pads, y, \note );

	i.map( m.knobA1, z, \att,[0.01,2] );
	i.map( m.knobA2, z, \rel, [0.1,3] );
	i.map( m.knobA3, z, \q, [0.01,0.1] );
	i.map( m.knobA4, z, \legato, [0,0.4] );

	i.map( m.knobB1, y, \att,[0.01,2] );
	i.map( m.knobB2, y, \rel, [0.1,3] );
	i.map( m.knobB3, y, \q, [0.005,0.1] );
	i.map( m.knobB4, y, \gain, [1,4] );

	i.map( m.ctlPad1, y, \detune,[0,10] );
	i.map( m.ctlPad2, y, \q2, [1,0.1] );
	i.map( m.ctlPad3, y, \gain1, [6,2] );
	i.map( m.ctlPad4, y, \gain1LFORate, [3,13] );
	// i.map( m.ctlPad3, y, \q, [0.005,0.1] );
	// i.map( m.ctlPad4, y, \gain, [1,4] );

	z.octave=0;
	y.octave=5;

	} }).play;

	});

)
