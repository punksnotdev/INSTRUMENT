(


	~tracks = IdentityDictionary.new;

	~tracks[\noteSynth] = IdentityDictionary.new;

	// values in ms
	~tracks[\noteSynth] = (
		speed: 4,
		beatsPerBar: ~beatsPerBar,
		synthdef:\noteSynth,
		currentBeat: nil,
		sequence: IdentityDictionary.new,
		loop: false
	);

	~tracks[\kick] = IdentityDictionary.new;

	// values in ms
	~tracks[\kick] = (
		speed: 4,
		beatsPerBar:~beatsPerBar,
		synthdef:\kick,
		currentBeat: nil,
		sequence: IdentityDictionary.new,
		loop: true
	);

	// values in ms
	~tracks[\bass] = (
		speed: 2,
		// beatsPerBar: ~beatsPerBar,
		beatsPerBar: ~beatsPerBar,
		synthdef:\bassSynth,
		currentBeat: nil,
		sequence: IdentityDictionary.new,
		loop: true
	);

	~tracks[\bass].sequence[0] = ( note: 36, amp: 1 );
	~tracks[\bass].sequence[2] = ( note: 38, amp: 1 );
	~tracks[\bass].sequence[3] = ( note: 39, amp: 1 );
	~tracks[\bass].sequence[6] = ( note: 41, amp: 1 );
	~tracks[\bass].sequence[7] = ( note: 43, amp: 1 );
	~tracks[\bass].sequence[9] = ( note: 39, amp: 1 );
	~tracks[\bass].sequence[10] = ( note: 39, amp: 1 );


	~tracks[\noteSynth].sequence[0] = ( note: 60, amp: 0.8 );
	~tracks[\noteSynth].sequence[2] = ( note: 63, amp: 0.5 );
	~tracks[\noteSynth].sequence[4] = ( note: 62, amp: 0.25 );
	~tracks[\noteSynth].sequence[6] = ( note: 67, amp: 0.35 );
	~tracks[\noteSynth].sequence[7.5] = ( note: 68, amp: 0.75 );
	~tracks[\noteSynth].sequence[21.5] = ( note: 68, amp: 0.75 );


	~tracks[\kick].sequence[0] = ( note: 60, amp: 1 );
	~tracks[\kick].sequence[1] = ( note: 63, amp: 0.5 );
	~tracks[\kick].sequence[3] = ( note: 62, amp: 0.25 );
	~tracks[\kick].sequence[6] = ( note: 67, amp: 0.35 );
	~tracks[\kick].sequence[7] = ( note: 68, amp: 0.75 );
	// ~tracks[\kick].sequence[11] = ( note: 68, amp: 0.75 );

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
