s.boot;
(
	i=INSTRUMENT();

	i.midi=true;

)
(

MIDIIn.connect(0, MIDIClient.sources[3] );
m = i.controllerManager.midi;
m.addDevice( MIDIClient.sources[3] );

i.midi.usb_axiom_25.addControllerGroup(\note,\piano);

i.midi.usb_axiom_25.groups.piano.addController(\piano);
//
//
m = ();
//
m.piano=i.midi.usb_axiom_25.groups.piano.controllers.piano;
//
//
z = SynthPlayer(\bass,\tranceBazz);
// z.seq(\note,0,"0 3 2 7");
// y = SynthPlayer(\bass2,\tranceBazz);
// y.seq(\note,0,"10 13 12 19",[\speed,2]);
//
// a=[z,y];
// p = [ [\att,[0,1]], [\rel,[
// 0.3,3]], [\rq,[0.05,0.1]] ];
//
//
i.map( m.piano, z, \note );
// i.map( m.channel2.faders[0], a[1], \amp, [0,1] );
//
// m.channel1.knobs.collect({|ctl,j|
// 	i.map(ctl,a[0],p[j][0],p[j][1])
// });
//
// m.channel2.knobs.collect({|ctl,j|
// 	i.map(ctl,a[1],p[j][0],p[j][1])
// });
//
//
z.octave=0;

)
