b = IdentityDictionary.new;
n = IdentityDictionary.new;


b[0] = (target: 0, control: 16);
b[1] = (target: 0, control: 17);
b[2] = (target: 0, control: 18);
b[3] = (target: 0, control: 19);

b[4] = (target: 1, control: 20);
b[5] = (target: 1, control: 21);
b[6] = (target: 1, control: 22);
b[7] = (target: 1, control: 23);

b[8] = (target: 2, control: 24);
b[9] = (target: 2, control: 25);
b[10] = (target: 2, control: 26);
b[11] = (target: 2, control: 27);

b[12] = (target: 3, control: 28);
b[13] = (target: 3, control: 29);
b[14] = (target: 3, control: 30);
b[15] = (target: 3, control: 31);



b[16] = (target: 4, control: 46);
b[17] = (target: 4, control: 47);
b[18] = (target: 4, control: 48);
b[19] = (target: 4, control: 49);

b[20] = (target: 5, control: 50);
b[21] = (target: 5, control: 51);
b[22] = (target: 5, control: 52);
b[23] = (target: 5, control: 53);

b[24] = (target: 6, control: 54);
b[25] = (target: 6, control: 55);
b[26] = (target: 6, control: 56);
b[27] = (target: 6, control: 57);

b[28] = (target: 7, control: 58);
b[29] = (target: 7, control: 59);
b[30] = (target: 7, control: 60);
b[31] = (target: 7, control: 61);

b[32] = (target: 8, control: 62);




n[25] = ( target: 8, note: 0 );
n[26] = ( target: 8, note: 1 );
n[27] = ( target: 8, note: 2 );

n[1] = ( target: 0, note: 0 );
n[3] = ( target: 0, note: 1 );

n[4] = ( target: 1, note: 0 );
n[6] = ( target: 1, note: 1 );

n[7] = ( target: 2, note: 0 );
n[9] = ( target: 2, note: 1 );

n[10] = ( target: 3, note: 0 );
n[12] = ( target: 3, note: 1 );

n[13] = ( target: 4, note: 0 );
n[15] = ( target: 4, note: 1 );

n[16] = ( target: 5, note: 0 );
n[18] = ( target: 5, note: 1 );

n[19] = ( target: 6, note: 0 );
n[21] = ( target: 6, note: 1 );

n[22] = ( target: 7, note: 0 );
n[24] = ( target: 7, note: 1 );


//
// MIDIIn.connectAll;
//
// MIDIdef.cc(\cc, {arg ...args;
// 	[b[args[1]].target, b[args[1]].control, args[0] ].postln;
// });//, ccNum, chan, srcID, argTemplate, dispatcher)
//
// MIDIdef.noteOn(\noteOn, {arg ...args;
// 	[n[args[1]].target, n[args[1]].note, args[0] ].postln;
// });
//
// MIDIdef.noteOff(\noteOff, {arg ...args;
// 	[n[args[1]].target, n[args[1]].note, args[0] ].postln;
// });

"MIDIMIX map loaded".postln;
