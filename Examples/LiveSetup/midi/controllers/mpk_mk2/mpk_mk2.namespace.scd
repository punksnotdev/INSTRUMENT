m.mpk_mk2=();

i.midi.mpk_mini_mk_ii.addControllerGroup(\note,\piano);
i.midi.mpk_mini_mk_ii.groups.piano.addController(\piano,nil,0);

i.midi.mpk_mini_mk_ii.addControllerGroup(\note,\pads);
i.midi.mpk_mini_mk_ii.groups.pads.addController(\pads,nil,8);

i.midi.mpk_mini_mk_ii.addControllerGroup(\cc,\knobs);

i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k1,1);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k2,2);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k3,3);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k4,4);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k5,5);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k6,6);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k7,7);
i.midi.mpk_mini_mk_ii.groups.knobs.addController(\k8,8);






m.mpk_mk2.piano=i.midi.mpk_mini_mk_ii.groups.piano.controllers.piano;
m.mpk_mk2.pads=i.midi.mpk_mini_mk_ii.groups.pads.controllers.pads;


m.mpk_mk2.knob1=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k1;
m.mpk_mk2.knob2=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k2;
m.mpk_mk2.knob3=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k3;
m.mpk_mk2.knob4=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k4;
m.mpk_mk2.knob5=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k5;
m.mpk_mk2.knob6=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k6;
m.mpk_mk2.knob7=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k7;
m.mpk_mk2.knob8=i.midi.mpk_mini_mk_ii.groups.knobs.controllers.k8;

"MPK MK2 setup".postln;
