b = IdentityDictionary.new;
n = IdentityDictionary.new;


b[0] = (target: 0, ctlNum: 16);
b[1] = (target: 0, ctlNum: 17);
b[2] = (target: 0, ctlNum: 18);
b[3] = (target: 0, ctlNum: 19);

b[4] = (target: 1, ctlNum: 20);
b[5] = (target: 1, ctlNum: 21);
b[6] = (target: 1, ctlNum: 22);
b[7] = (target: 1, ctlNum: 23);

b[8] = (target: 2, ctlNum: 24);
b[9] = (target: 2, ctlNum: 25);
b[10] = (target: 2, ctlNum: 26);
b[11] = (target: 2, ctlNum: 27);

b[12] = (target: 3, ctlNum: 28);
b[13] = (target: 3, ctlNum: 29);
b[14] = (target: 3, ctlNum: 30);
b[15] = (target: 3, ctlNum: 31);



b[16] = (target: 4, ctlNum: 46);
b[17] = (target: 4, ctlNum: 47);
b[18] = (target: 4, ctlNum: 48);
b[19] = (target: 4, ctlNum: 49);

b[20] = (target: 5, ctlNum: 50);
b[21] = (target: 5, ctlNum: 51);
b[22] = (target: 5, ctlNum: 52);
b[23] = (target: 5, ctlNum: 53);

b[24] = (target: 6, ctlNum: 54);
b[25] = (target: 6, ctlNum: 55);
b[26] = (target: 6, ctlNum: 56);
b[27] = (target: 6, ctlNum: 57);

b[28] = (target: 7, ctlNum: 58);
b[29] = (target: 7, ctlNum: 59);
b[30] = (target: 7, ctlNum: 60);
b[31] = (target: 7, ctlNum: 61);

b[32] = (target: 8, ctlNum: 62);




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
