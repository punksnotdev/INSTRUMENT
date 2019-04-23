
// Setup: INSTRUMENT

i=INSTRUMENT();

i.tempo = e.tempo;



// Setup: ProxySpace

ProxySpace.clearAll;
p = ProxySpace.push(s);
p.makeTempoClock(TempoClock.default.tempo,'instrument',TempoClock.default);


p.quant = 4;

~out.quant=4;


~z.fadeTime = 5;

~z = {|amp=0.5| Silent.ar() ! 2  };

~amp = {1/3};
~mix = ~z;

~out = {
	Pan2.ar(Limiter.ar(CompanderD.ar(Mix.new(HPF.ar(
		~mix.ar
	,30)), 0.9, 1, 0.9, 0.045,0.3 )*~amp.kr),0);
};
~mix.fadeTime=8;


~out.play;




// Setup: Loopers

i.loadSynths(Platform.userExtensionDir++'/INSTRUMENT/INSTRUMENT/Looper/looper/*');


"Loopers Synths: Loaded".postln;

(thisProcess.nowExecutingPath.dirname ++ "/create-loopers.scd").load;


// Setup: Synths

i.synths = i.loadSynths(Platform.userExtensionDir++'/INSTRUMENT/INSTRUMENT/Sounds/SynthDefs/*');

"Synths: Loaded".postln;



// Setup: MIDI


(thisProcess.nowExecutingPath.dirname ++ "/midi/midi-setup.scd").load;
(thisProcess.nowExecutingPath.dirname ++ "/midi/midi-out-setup.scd").load;


// Start:

i.play;
