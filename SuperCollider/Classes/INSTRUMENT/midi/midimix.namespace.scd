var inputs = List.new;

m.midi_mix=();

8.do{|index|

	var chanName;

	chanName=\chan;

	chanName=(chanName++(index+1)).asSymbol;



	m.midi_mix[chanName] = ();
	m.midi_mix[chanName].ctlGroup = i.midi.midi_mix.addControllerGroup(\cc,chanName);

	m.midi_mix[chanName].knobs=();
	m.midi_mix[chanName].knobs.ctlGroup = m.midi_mix[chanName].ctlGroup.addControllerGroup(\cc,\knobs);

	m.midi_mix[chanName].knobs.a = m.midi_mix[chanName].knobs.ctlGroup.addController(\knob1,b[(4*index)+0].ctlNum);
	m.midi_mix[chanName].knobs.b = m.midi_mix[chanName].knobs.ctlGroup.addController(\knob2,b[(4*index)+1].ctlNum);
	m.midi_mix[chanName].knobs.c = m.midi_mix[chanName].knobs.ctlGroup.addController(\knob3,b[(4*index)+2].ctlNum);
	m.midi_mix[chanName].fader = m.midi_mix[chanName].ctlGroup.addController(\fader,b[(4*index)+3].ctlNum);
	// m.midi_mix[chanName].knobs.a = m.midi_mix[chanName].knobs.ctlGroup.addController(\knob1,b[(4*index)+0].ctlNum,[0,1]);
	// m.midi_mix[chanName].knobs.b = m.midi_mix[chanName].knobs.ctlGroup.addController(\knob2,b[(4*index)+1].ctlNum,[0,1]);
	// m.midi_mix[chanName].knobs.c = m.midi_mix[chanName].knobs.ctlGroup.addController(\knob3,b[(4*index)+2].ctlNum,[0,1]);
	// m.midi_mix[chanName].fader = m.midi_mix[chanName].ctlGroup.addController(\fader,b[(4*index)+3].ctlNum,[0,1]);

	inputs.add(m.midi_mix[chanName].fader);

};


i.midiControllers.inputs = inputs;


"MIDIMIX setup".postln;
