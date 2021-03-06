SynthDef(\hihatSimple, {
    |out = 0, pan = 0, amp = 0.5|
    var click, clickAmp;
    var noise, noiseAmp;
    var snd;

    // noise -> resonance -> expodec envelope
    noiseAmp = EnvGen.ar(Env.perc(0.001, 0.3, curve: -8), doneAction: 2);
    noise = Mix(BPF.ar(ClipNoise.ar, [4010, 4151], [0.15, 0.56], [1.0, 0.6])) * 0.7 * noiseAmp;

    snd = noise;

    Out.ar(out, Pan2.ar(snd, pan, amp) );
}).store;



SynthDef(\hihatClosed, {|amp=0.5,out=0, rel=0.125|

    var hatosc, hatenv, hatnoise, hatoutput;

    hatnoise = {LPF.ar(WhiteNoise.ar(1),6000)};

    hatosc = {HPF.ar(hatnoise,2000)};
    hatenv = EnvGen.kr(Env.perc(0.01,rel),doneAction:2);

    hatoutput = (hatosc * hatenv)/2;

    Out.ar(out,
    Pan2.ar(hatoutput*amp, 0)

    )

}).store;




SynthDef(\hihatOpen, {|amp=0.5,out=0|

    var hatosc, hatenv, hatnoise, hatoutput;

    hatnoise = {LPF.ar(WhiteNoise.ar(1),6000)};

    hatosc = {HPF.ar(hatnoise,2000)};
    hatenv = {Line.ar(1, 0, 0.3,doneAction: 2)};

    hatoutput = (hatosc * hatenv);

    Out.ar(out,
    Pan2.ar(hatoutput*amp, 0)

    )

}).store;




SynthDef(\hihatShort, {
    |out = 0, pan = 0, amp = 0.5|
    var click, clickAmp;
    var noise, noiseAmp;
    var snd;

    // noise -> resonance -> expodec envelope
    noiseAmp = EnvGen.ar(Env.perc(0.001, 0.3, curve: -8), doneAction: 2);
    noise = Mix(BPF.ar(ClipNoise.ar, [4010, 4151], [0.15, 0.56], [1.0, 0.6])) * 0.7 * noiseAmp;

    snd = noise;

    Out.ar(out, Pan2.ar(snd, pan, amp) );
}).store;




SynthDef(\snareBass, {|amp=0.5,out=0|
    var drumosc, filterenv, volenv, drumoutput, snaposc, snapenv, fulloutput;

    drumosc = {((Pulse.ar(100)+WhiteNoise.ar)*3).tanh};
    filterenv = {Line.ar(1, 0, 0.2, doneAction: 2)};
    volenv = {EnvGen.kr(Env.perc(0.01,0.6), doneAction: 2)};
    drumoutput = {LPF.ar(drumosc,(filterenv *1000) + 30)};

    snaposc = {BPF.ar(HPF.ar(WhiteNoise.ar(1),500),1500)};
    snapenv = {Line.ar(1, 0, 0.2, doneAction: 2)};

    fulloutput = (drumoutput * volenv) + (snaposc * snapenv);
    //fulloutput = (drumoutput * volenv);

    Out.ar(out,Pan2.ar(fulloutput*(amp/3), 0));

}).store;



SynthDef(\snareDry, {
    |out = 0, pan = 0, amp = 1|
    var pop, popAmp, popFreq;
    var noise, noiseAmp;
    var snd;

    // pop makes a click coming from very high frequencies
    // slowing down a little and stopping in mid-to-low
    popFreq = EnvGen.ar(Env([3261, 410, 160], [0.005, 0.01], curve: \exp));
    popAmp = EnvGen.ar(Env.perc(0.001, 0.11)) * 0.7;
    pop = SinOsc.ar(popFreq) * popAmp;
    // bandpass-filtered white noise
    noiseAmp = EnvGen.ar(Env.perc(0.001, 0.15), doneAction: 2);
    noise = BPF.ar(WhiteNoise.ar, 810, 1.6) * noiseAmp;

    snd = (pop + noise) * 1.3;

    Out.ar(out, Pan2.ar(snd, pan, (amp/3)));
}).store;


SynthDef(\snareHollow, {arg out = 0, amp = 0.1, sinfreq = 180, att = 0.01, rel = 0.2, ffreq = 2000, pan = 0;
	var env, snd1, snd2, sum;
	env = Env.perc(att, rel, amp).kr(doneAction: 2);
	snd1 = HPF.ar(
		in: WhiteNoise.ar,
		freq: ffreq,
		mul: env
	);
	snd2 = SinOsc.ar(freq: sinfreq, mul: env);
	sum = snd1 + snd2;
	Out.ar(out, Pan2.ar(sum, pan));
}).store;


SynthDef(\snareNeuro, {
	var snd;
	// a percussive click to give it some attack
	snd = LPF.ar(HPF.ar(WhiteNoise.ar, 300), 8000) * Env.linen(0.001, 0.01, 0.001).ar;
	// sine sweep body. very important!
	snd = snd + (SinOsc.ar(Env([400, 196, 160], [0.04, 0.2], \exp).ar) * Env.perc(0.04, 0.2).ar * 6.dbamp).tanh;
	// sound of snare coils rattling
	snd = snd + (HPF.ar(BPeakEQ.ar(WhiteNoise.ar, 4000, 0.5, 3), 300) * Env.perc(0.05, 0.2).delay(0.01).ar(2) * -3.dbamp);
	// another sound sweep to improve the attack, optional
	snd = snd + (SinOsc.ar(XLine.kr(3000, 1500, 0.01)) * Env.perc(0.001, 0.02).ar);
	// distortion helps glue everything together and acts as a compressor
	snd = (snd * 1.4).tanh/3;
	snd = Pan2.ar(snd, \pan.kr(0), \amp.kr(0.1));
	Out.ar(\out.kr(0), snd);
}).store;
