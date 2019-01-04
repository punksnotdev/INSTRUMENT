s.boot;

i=INSTRUMENT();
i[\piano]=SynthPlayer(\distNote);
i[\kick]=SynthPlayer(\kickHard);
i[\piano].seq(\note,"0 3 2");
i[\kick].seq(\trigger,"1   1");
i[\kick].fx=\reverb;
i[\kick].setFx(\wet,1)
i[\kick].setFx(\rv1,1)
i[\kick].setFx(\rv2,1)
i[\piano].fx=\reverb;
i[\piano].setFx(\wet,1)

i[\kick].fx=nil;



// "/home/mukkekunst/Musica/furenku/sets/01-morelia/midi-multidevice.scd".load;


i.map( i.midi.midi_mix.groups.chan1.controllers.fader, i[\piano], \amp, [0,1]);
i.map( i.midi.midi_mix.groups.chan1.controllers.fader, i[\kick], \amp, [0,1]);
i.unmap( i.midi.midi_mix.groups.chan1.controllers.fader, i[\piano], \amp);
i.unmap( i.midi.midi_mix.groups.chan1.controllers.fader, i[\kick], \amp);
