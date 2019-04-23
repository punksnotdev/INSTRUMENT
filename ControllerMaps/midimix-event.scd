o = ();

o.inputMap = IdentityDictionary.new;
o.outputMap = IdentityDictionary.new;


o.name = "MIDI Mix";

o.outputMap[0] = (target: 0, ctlNum: 16);
o.outputMap[1] = (target: 0, ctlNum: 17);
o.outputMap[2] = (target: 0, ctlNum: 18);
o.outputMap[3] = (target: 0, ctlNum: 19);

o.outputMap[4] = (target: 1, ctlNum: 20);
o.outputMap[5] = (target: 1, ctlNum: 21);
o.outputMap[6] = (target: 1, ctlNum: 22);
o.outputMap[7] = (target: 1, ctlNum: 23);

o.outputMap[8] = (target: 2, ctlNum: 24);
o.outputMap[9] = (target: 2, ctlNum: 25);
o.outputMap[10] = (target: 2, ctlNum: 26);
o.outputMap[11] = (target: 2, ctlNum: 27);

o.outputMap[12] = (target: 3, ctlNum: 28);
o.outputMap[13] = (target: 3, ctlNum: 29);
o.outputMap[14] = (target: 3, ctlNum: 30);
o.outputMap[15] = (target: 3, ctlNum: 31);



o.outputMap[16] = (target: 4, ctlNum: 46);
o.outputMap[17] = (target: 4, ctlNum: 47);
o.outputMap[18] = (target: 4, ctlNum: 48);
o.outputMap[19] = (target: 4, ctlNum: 49);

o.outputMap[20] = (target: 5, ctlNum: 50);
o.outputMap[21] = (target: 5, ctlNum: 51);
o.outputMap[22] = (target: 5, ctlNum: 52);
o.outputMap[23] = (target: 5, ctlNum: 53);

o.outputMap[24] = (target: 6, ctlNum: 54);
o.outputMap[25] = (target: 6, ctlNum: 55);
o.outputMap[26] = (target: 6, ctlNum: 56);
o.outputMap[27] = (target: 6, ctlNum: 57);

o.outputMap[28] = (target: 7, ctlNum: 58);
o.outputMap[29] = (target: 7, ctlNum: 59);
o.outputMap[30] = (target: 7, ctlNum: 60);
o.outputMap[31] = (target: 7, ctlNum: 61);

o.outputMap[32] = (target: 8, ctlNum: 62);




o.inputMap[25] = ( target: 8, note: 0 );
o.inputMap[26] = ( target: 8, note: 1 );
o.inputMap[27] = ( target: 8, note: 2 );

o.inputMap[1] = ( target: 0, note: 0 );
o.inputMap[3] = ( target: 0, note: 1 );

o.inputMap[4] = ( target: 1, note: 0 );
o.inputMap[6] = ( target: 1, note: 1 );

o.inputMap[7] = ( target: 2, note: 0 );
o.inputMap[9] = ( target: 2, note: 1 );

o.inputMap[10] = ( target: 3, note: 0 );
o.inputMap[12] = ( target: 3, note: 1 );

o.inputMap[13] = ( target: 4, note: 0 );
o.inputMap[15] = ( target: 4, note: 1 );

o.inputMap[16] = ( target: 5, note: 0 );
o.inputMap[18] = ( target: 5, note: 1 );

o.inputMap[19] = ( target: 6, note: 0 );
o.inputMap[21] = ( target: 6, note: 1 );

o.inputMap[22] = ( target: 7, note: 0 );
o.inputMap[24] = ( target: 7, note: 1 );


//
// MIDIIn.connectAll;
//
// MIDIdef.cc(\cc, {arg ...args;
// 	[o.outputMap[args[1]].target, o.outputMap[args[1]].control, args[0] ].postln;
// });//, ccNum, chan, srcID, argTemplate, dispatcher)
//
// MIDIdef.noteOn(\noteOn, {arg ...args;
o.// 	[inputMap[args[1]].target, inputMap[args[1]].note, args[0] ].postln;
// });
//
// MIDIdef.noteOff(\noteOff, {arg ...args;
o.// 	[inputMap[args[1]].target, inputMap[args[1]].note, args[0] ].postln;
// });

"MIDIMIX map loaded".postln;
