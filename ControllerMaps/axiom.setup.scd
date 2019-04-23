i.midi.usb_axiom_25.addControllerGroup(\note,\piano);
i.midi.usb_axiom_25.groups.piano.addController(\piano,nil,0);

i.midi.usb_axiom_25.addControllerGroup(\note,\pads);
i.midi.usb_axiom_25.groups.pads.addController(\pads,nil,9);

i.midi.usb_axiom_25.addControllerGroup(\cc,\knobsA);
i.midi.usb_axiom_25.addControllerGroup(\cc,\knobsB);
i.midi.usb_axiom_25.addControllerGroup(\cc,\ctlPads);

i.midi.usb_axiom_25.groups.knobsA.addController(\knobA1,1);
i.midi.usb_axiom_25.groups.knobsA.addController(\knobA2,2);
i.midi.usb_axiom_25.groups.knobsA.addController(\knobA3,3);
i.midi.usb_axiom_25.groups.knobsA.addController(\knobA4,4);

i.midi.usb_axiom_25.groups.knobsB.addController(\knobB1,5);
i.midi.usb_axiom_25.groups.knobsB.addController(\knobB2,6);
i.midi.usb_axiom_25.groups.knobsB.addController(\knobB3,7);
i.midi.usb_axiom_25.groups.knobsB.addController(\knobB4,8);

i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad1,9);
i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad2,10);
i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad3,11);
i.midi.usb_axiom_25.groups.ctlPads.addController(\ctlPad4,12);





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


"USB Axiom 25 setup".postln;
