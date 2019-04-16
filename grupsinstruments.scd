s.boot;
s.doWhenBooted({
	i = INSTRUMENT();

	i.play;

	i.piano = INSTRUMENT(\distNote1);
	i.piano.note("0 2 3");

});




g=Group.new;

b=Bus.audio(s,2);

m=Synth.tail(g,\audioBus);
m.set(\outBus,89);
m.set(\inBus,b);

r=Synth.tail(g,\reverb);
r.set(\inBus,89)


i.piano.outBus=b;
i.piano.group=g;
