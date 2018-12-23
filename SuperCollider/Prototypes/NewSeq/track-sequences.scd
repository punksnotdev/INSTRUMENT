(

	~beatsPerMeasure = 4;

	~tracks = IdentityDictionary.new;

	~tracks[\noteSynth] = IdentityDictionary.new;

	// values in ms
	~tracks[\noteSynth] = (
		speed: 1,
		beatsPerMeasure: ~beatsPerMeasure,
		synthdef:\noteSynth,
		currentBeat: nil,
		sequence: IdentityDictionary.new,
		loop: false
	);

	~tracks[\kick] = IdentityDictionary.new;

	// values in ms
	~tracks[\kick] = (
		speed: 2,
		beatsPerMeasure: ~beatsPerMeasure,
		synthdef:\kick,
		currentBeat: nil,
		sequence: IdentityDictionary.new,
		loop: true
	);

	// values in ms
	~tracks[\bass] = (
		speed: 4,
		beatsPerMeasure: ~beatsPerMeasure,
		synthdef:\bassSynth,
		currentBeat: nil,
		sequence: IdentityDictionary.new,
		loop: true
	);

	SynthDef(\bassSynth,{
		arg note=60, amp=1;
		var sig = Saw.ar(note.midicps/2)+SinOsc.ar(note.midicps,pi/2);
		var env = EnvGen.kr(Env.perc(0.02,0.3),doneAction: 2);
		var filterEnv = EnvGen.kr(Env.perc(0,0.2));
		sig = RLPF.ar(sig,filterEnv.linlin(0,1,200,3000),0.1);
		sig = (sig*3.3).tanh;
		Out.ar(0,sig*env*amp);
	}).add;

	SynthDef(\noteSynth,{
		arg note=60, amp=1;
		var sig = SinOsc.ar(note.midicps,pi/2);
		var env = EnvGen.kr(Env.perc,doneAction: 2);
		Out.ar(0,sig*env*amp);
	}).add;

	SynthDef(\kick,{
		arg note=60, amp=1;
		var filterEnv = EnvGen.kr(Env.perc(0,0.03));
		var sig = SinOsc.ar(60,pi/2)+RLPF.ar(WhiteNoise.ar(1/8),filterEnv.linlin(0,1,64,10000),0.01);
		var env = EnvGen.kr(Env.perc,doneAction: 2);
		sig = (sig * 3.3).tanh;

		Out.ar(0,sig*env*amp);
	}).add;


	~tracks[\bass].sequence[0] = ( note: 60-24, amp: 1 );
	~tracks[\bass].sequence[2] = ( note: 63-24, amp: 0.5 );
	~tracks[\bass].sequence[4] = ( note: 62-24, amp: 0.25 );
	~tracks[\bass].sequence[6] = ( note: 67-24, amp: 0.35 );
	~tracks[\bass].sequence[7.5] = ( note: 68-24, amp: 0.75 );

	~tracks[\noteSynth].sequence[0] = ( note: 60, amp: 1 );
	~tracks[\noteSynth].sequence[2] = ( note: 63, amp: 0.5 );
	~tracks[\noteSynth].sequence[4] = ( note: 62, amp: 0.25 );
	~tracks[\noteSynth].sequence[6] = ( note: 67, amp: 0.35 );
	~tracks[\noteSynth].sequence[7.5] = ( note: 68, amp: 0.75 );


	~tracks[\kick].sequence[0] = ( note: 60, amp: 1 );
	~tracks[\kick].sequence[1] = ( note: 63, amp: 0.5 );
	~tracks[\kick].sequence[3] = ( note: 62, amp: 0.25 );
	~tracks[\kick].sequence[6] = ( note: 67, amp: 0.35 );
	~tracks[\kick].sequence[7] = ( note: 68, amp: 0.75 );

	g = { arg sequence;

		var keyList = List.new;

		sequence.sortedKeysValuesDo{|k,v|

			keyList.add(k);

		};

		keyList;
	};

	~tracks[\noteSynth].sequenceKeys = g.value( ~tracks[\noteSynth].sequence );
	~tracks[\bass].sequenceKeys = g.value( ~tracks[\bass].sequence );
	~tracks[\kick].sequenceKeys = g.value( ~tracks[\kick].sequence );


	// ~tracks;

)
