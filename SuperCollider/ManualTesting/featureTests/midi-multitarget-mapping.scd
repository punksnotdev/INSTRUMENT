s.boot;

i=INSTRUMENT();
i[\piano]=SynthPlayer(\distNote);
i[\piano].seq(\note,"0 3 2");




// "/home/mukkekunst/Musica/furenku/sets/01-morelia/midi-multidevice.scd".load;


i.map( i.midi.midi_mix.controllers.fader, i[\piano], \amp, [0,1]);
