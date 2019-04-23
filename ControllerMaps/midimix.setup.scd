8.do{|index|

	var chanName;

	chanName=\chan;

	chanName=(chanName++(index+1)).asSymbol;



	m[chanName] = ();
	m[chanName].ctlGroup = i.midi.midi_mix.addControllerGroup(\cc,chanName);

	m[chanName].knobs=();
	m[chanName].knobs.ctlGroup = m[chanName].ctlGroup.addControllerGroup(\cc,\knobs);

	m[chanName].knobs.a = m[chanName].knobs.ctlGroup.addController(\knob1,b[(4*index)+0].ctlNum,[0,1]);
	m[chanName].knobs.b = m[chanName].knobs.ctlGroup.addController(\knob2,b[(4*index)+1].ctlNum,[0,1]);
	m[chanName].knobs.c = m[chanName].knobs.ctlGroup.addController(\knob3,b[(4*index)+2].ctlNum,[0,1]);
	m[chanName].fader = m[chanName].ctlGroup.addController(\fader,b[(4*index)+3].ctlNum,[0,1]);


};



"MIDIMIX setup".postln;
