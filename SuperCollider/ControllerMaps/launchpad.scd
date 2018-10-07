o = ();

o.name="Launchpad";
o.outputType=\note;

o.outputMap = IdentityDictionary.new;
o.inputMap = IdentityDictionary.new;


o.outputMap[0] = (target: 0, ctlNum: 0);
o.outputMap[1] = (target: 0, ctlNum: 1);
o.outputMap[2] = (target: 0, ctlNum: 2);
o.outputMap[3] = (target: 0, ctlNum: 3);
o.outputMap[4] = (target: 0, ctlNum: 4);
o.outputMap[5] = (target: 0, ctlNum: 5);
o.outputMap[6] = (target: 0, ctlNum: 6);
o.outputMap[7] = (target: 0, ctlNum: 7);
o.outputMap[8] = (target: 0, ctlNum: 8);

o.outputMap[9] = (target: 1, ctlNum: 16);
o.outputMap[10] = (target: 1, ctlNum: 17);
o.outputMap[11] = (target: 1, ctlNum: 18);
o.outputMap[12] = (target: 1, ctlNum: 19);
o.outputMap[13] = (target: 1, ctlNum: 20);
o.outputMap[14] = (target: 1, ctlNum: 21);
o.outputMap[15] = (target: 1, ctlNum: 22);
o.outputMap[16] = (target: 1, ctlNum: 23);
o.outputMap[17] = (target: 1, ctlNum: 24);

o.outputMap[18] = (target: 2, ctlNum: 32);
o.outputMap[19] = (target: 2, ctlNum: 33);
o.outputMap[20] = (target: 2, ctlNum: 34);
o.outputMap[21] = (target: 2, ctlNum: 35);
o.outputMap[22] = (target: 2, ctlNum: 36);
o.outputMap[23] = (target: 2, ctlNum: 37);
o.outputMap[24] = (target: 2, ctlNum: 38);
o.outputMap[25] = (target: 2, ctlNum: 39);
o.outputMap[26] = (target: 2, ctlNum: 40);

o.outputMap[27] = (target: 3, ctlNum: 48);
o.outputMap[28] = (target: 3, ctlNum: 49);
o.outputMap[29] = (target: 3, ctlNum: 50);
o.outputMap[30] = (target: 3, ctlNum: 51);
o.outputMap[31] = (target: 3, ctlNum: 52);
o.outputMap[32] = (target: 3, ctlNum: 53);
o.outputMap[33] = (target: 3, ctlNum: 54);
o.outputMap[34] = (target: 3, ctlNum: 55);
o.outputMap[35] = (target: 3, ctlNum: 56);



o.outputMap[36] = (target: 4, ctlNum: 64);
o.outputMap[37] = (target: 4, ctlNum: 65);
o.outputMap[38] = (target: 4, ctlNum: 66);
o.outputMap[39] = (target: 4, ctlNum: 67);
o.outputMap[40] = (target: 4, ctlNum: 68);
o.outputMap[41] = (target: 4, ctlNum: 69);
o.outputMap[42] = (target: 4, ctlNum: 70);
o.outputMap[43] = (target: 4, ctlNum: 71);
o.outputMap[44] = (target: 4, ctlNum: 72);

o.outputMap[45] = (target: 5, ctlNum: 80);
o.outputMap[46] = (target: 5, ctlNum: 81);
o.outputMap[47] = (target: 5, ctlNum: 82);
o.outputMap[48] = (target: 5, ctlNum: 83);
o.outputMap[49] = (target: 5, ctlNum: 84);
o.outputMap[50] = (target: 5, ctlNum: 85);
o.outputMap[51] = (target: 5, ctlNum: 86);
o.outputMap[52] = (target: 5, ctlNum: 87);
o.outputMap[53] = (target: 5, ctlNum: 88);

o.outputMap[54] = (target: 6, ctlNum: 96);
o.outputMap[55] = (target: 6, ctlNum: 97);
o.outputMap[56] = (target: 6, ctlNum: 98);
o.outputMap[57] = (target: 6, ctlNum: 99);
o.outputMap[58] = (target: 6, ctlNum: 100);
o.outputMap[59] = (target: 6, ctlNum: 101);
o.outputMap[60] = (target: 6, ctlNum: 102);
o.outputMap[61] = (target: 6, ctlNum: 103);
o.outputMap[62] = (target: 6, ctlNum: 104);

o.outputMap[63] = (target: 7, ctlNum: 112);
o.outputMap[64] = (target: 7, ctlNum: 113);
o.outputMap[65] = (target: 7, ctlNum: 114);
o.outputMap[66] = (target: 7, ctlNum: 115);
o.outputMap[67] = (target: 7, ctlNum: 116);
o.outputMap[68] = (target: 7, ctlNum: 117);
o.outputMap[69] = (target: 7, ctlNum: 118);
o.outputMap[70] = (target: 7, ctlNum: 119);
o.outputMap[71] = (target: 7, ctlNum: 120);



o.outputMap.keysValuesDo{|k,v|

	o.inputMap[v.ctlNum] = (inputNum: k);

};
