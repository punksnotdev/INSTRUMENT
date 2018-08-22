(


	var cc = IdentityDictionary.new;
	var note = IdentityDictionary.new;


	cc[16] = (target: 0, ctrl: 0);
	cc[17] = (target: 0, ctrl: 1);
	cc[18] = (target: 0, ctrl: 2);
	cc[19] = (target: 0, ctrl: 3);

	cc[20] = (target: 1, ctrl: 0);
	cc[21] = (target: 1, ctrl: 1);
	cc[22] = (target: 1, ctrl: 2);
	cc[23] = (target: 1, ctrl: 3);

	cc[24] = (target: 2, ctrl: 0);
	cc[25] = (target: 2, ctrl: 1);
	cc[26] = (target: 2, ctrl: 2);
	cc[27] = (target: 2, ctrl: 3);

	cc[28] = (target: 3, ctrl: 0);
	cc[29] = (target: 3, ctrl: 1);
	cc[30] = (target: 3, ctrl: 2);
	cc[31] = (target: 3, ctrl: 3);



	cc[46] = (target: 4, ctrl: 0);
	cc[47] = (target: 4, ctrl: 1);
	cc[48] = (target: 4, ctrl: 2);
	cc[49] = (target: 4, ctrl: 3);

	cc[50] = (target: 5, ctrl: 0);
	cc[51] = (target: 5, ctrl: 1);
	cc[52] = (target: 5, ctrl: 2);
	cc[53] = (target: 5, ctrl: 3);

	cc[54] = (target: 6, ctrl: 0);
	cc[55] = (target: 6, ctrl: 1);
	cc[56] = (target: 6, ctrl: 2);
	cc[57] = (target: 6, ctrl: 3);

	cc[58] = (target: 7, ctrl: 0);
	cc[59] = (target: 7, ctrl: 1);
	cc[60] = (target: 7, ctrl: 2);
	cc[61] = (target: 7, ctrl: 3);

	cc[62] = (target: 8, ctrl: 0);




	note[25] = ( target: 8, note: 0 );
	note[26] = ( target: 8, note: 1 );
	note[27] = ( target: 8, note: 2 );

	note[1] = ( target: 0, note: 0 );
	note[3] = ( target: 0, note: 1 );

	note[4] = ( target: 1, note: 0 );
	note[6] = ( target: 1, note: 1 );

	note[7] = ( target: 2, note: 0 );
	note[9] = ( target: 2, note: 1 );

	note[10] = ( target: 3, note: 0 );
	note[12] = ( target: 3, note: 1 );

	note[13] = ( target: 4, note: 0 );
	note[15] = ( target: 4, note: 1 );

	note[16] = ( target: 5, note: 0 );
	note[18] = ( target: 5, note: 1 );

	note[19] = ( target: 6, note: 0 );
	note[21] = ( target: 6, note: 1 );

	note[22] = ( target: 7, note: 0 );
	note[24] = ( target: 7, note: 1 );



	MIDIIn.connectAll;

	MIDIdef.cc(\cc, {arg ...args;
		[cc[args[1]].target, cc[args[1]].ctrl, args[0] ].postln;
	});//, ccNum, chan, srcID, argTemplate, dispatcher)

	MIDIdef.noteOn(\noteOn, {arg ...args;
		[note[args[1]].target, note[args[1]].note, args[0] ].postln;
	});

	MIDIdef.noteOff(\noteOff, {arg ...args;
		[note[args[1]].target, note[args[1]].note, args[0] ].postln;
	});



)
