SynthDef(\gong, {
	arg ratio1=0.9, ratio2=0.55, ratio3=1.03, index2=2, index3=4, amp=0.7, dur=7, pan=0;
	var sig, op1, op2, op3, env1, env2, env3;
	env3 = EnvGen.kr(Env.new([0, 1, 1, 0], [0.4, 0.3, dur]), doneAction:2);
	env2 = EnvGen.kr(Env.new([0, 1, 1, 0], [0, 0.3, dur]), doneAction:2);
	env1 = EnvGen.kr(Env.new([0, 1, 1, 0], [0.003, 0.3, dur - 0.5]), doneAction:2);
	op3 = SinOsc.ar(([67, 70, 74].midicps * ratio3), mul:[67, 70, 74].midicps * ratio3 * index3) * env3;
	op2 = SinOsc.ar((([67, 70, 74].midicps * ratio2) + op3), mul:[67, 70, 74].midicps * ratio2 * index2) * env2;
	op1 = SinOsc.ar(([67, 70, 74].midicps * ratio1) + op2);
	sig = op1 * env1 * amp;
	sig = Splay.ar(sig);
	sig = Balance2.ar(sig[0], sig[1], pan);
	Out.ar(0, sig);
},
variants: (
	a: [\ratio1, 0.503, \ratio2, 0.50, \ratio3, 1.35, \index2, 1.53, \index3, 4.33],
	b: [\ratio1, 0.59, \ratio2, 0.17, \ratio3, 0.75, \index2, 1.54, \index3, 3.29],
	c: [\ratio1, 0.49, \ratio2, 0.17, \ratio3, 0.34, \index2, 1.33, \index3, 4.31],
	d: [\ratio1, 1.11, \ratio2, 0.2, \ratio3, 0.80, \index2, 1.97, \index3, 5.06],
)).store;
