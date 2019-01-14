s.boot;

p=ProxySpace.push(s);


~z.play;

~z = {|notes=#[48,51,55,60]|
	(SinOsc.ar(notes.midicps,0,SinOsc.kr(1/8,0,0.3,0.4)) * 10 ).tanh/4 ! 2
};


i=INSTRUMENT();
i[\z]=INSTRUMENT(~z);


i[\z].chord([
	C(0,\m),
	C(1,\M,0,[16]),
	C(7,[6,10,15]),
	C(0,\m,0,[15]),
	// C(1,\M),
]).speed(1/4)
