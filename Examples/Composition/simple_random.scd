i.pad=INSTRUMENT(\distPad1)
i.pad.note("0  2   3  0  2   3  5  0 2   3  5 7 ").speed(1/4)
i.pad.amp=4
i.pad.set(\rel,8)

i.pad2=INSTRUMENT(\distPad2)
i.pad2.note("0  2   3  0  2   3  5  0 2   3  5 7 ").speed(1/2).random
i.pad2.octave=6

i.pad3=INSTRUMENT(\distPad3)
i.pad3.note("0  2   3  0  2   3  5  0 2   3  5 7 ").speed(2).random
i.pad3.octave=8
i.pad3.amp=1/6


i.drums.stop


i.pad3.amp=1



(
	i.pad3.fx=\reverb;
	i.pad3.fxSet(\wet,1);
	i.pad3.fxSet(\damp,1);
	i.pad3.fxSet(\room,1);
)
i.pad3.stop;
