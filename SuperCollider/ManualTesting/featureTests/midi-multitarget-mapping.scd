s.boot;
s.meter
i=INSTRUMENT();
i[\piano]=INSTRUMENT(\distNote1);
i[\kick]=INSTRUMENT(\kickHard);
i[\piano].seq(\note,"0 3 2");
i[\kick].seq(\trigger,"1   1");
i[\kick].fx=\reverb;
i[\kick].fxSet(\wet,1)
i[\kick].fxSet(\rv1,1)
i[\kick].fxSet(\rv2,1)
i[\piano].fx=\reverb;
i[\piano].fxSet(\wet,1)
().play
i[\kick].fx=nil;



// "/home/mukkekunst/Musica/furenku/sets/01-morelia/midi-multidevice.scd".load;


i.map( i.midi.midi_mix.groups.chan1.controllers.fader, i[\piano], \amp, [0,1]);
i.map( i.midi.midi_mix.groups.chan1.controllers.fader, i[\kick], \amp, [0,1]);
i.unmap( i.midi.midi_mix.groups.chan1.controllers.fader, i[\piano], \amp);
i.unmap( i.midi.midi_mix.groups.chan1.controllers.fader, i[\kick], \amp);
