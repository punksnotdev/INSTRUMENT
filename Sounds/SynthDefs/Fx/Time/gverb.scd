SynthDef(\gverb, {

	arg inBus=0, outBus=0, gate = 1, room = 100, revtime = 1, damp = 0.6, inputbw = 0.5, spread = 15, drylevel = 1, earlyreflevel = 0.7, taillevel = 0.5, maxroomsize = 300, amp = 0.5;

	var source = In.ar(inBus,8);
	var reverb;
	var env = Linen.kr(gate, 0.1, 1, 0.1, 2);


	reverb = GVerb.ar(source, room, revtime, damp, inputbw, spread, drylevel, earlyreflevel, taillevel, maxroomsize);

	reverb = reverb * amp ;

	ReplaceOut.ar(outBus,Pan2.ar(env*reverb,0));
//By Zé Craum

}).store;
