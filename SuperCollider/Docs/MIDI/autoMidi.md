
i.autoMIDI;
i[\kick]=INSTRUMENT(i.synths.kicks.choose);
i.autoMIDI;
i[\k2]=INSTRUMENT(i.synths.kicks.choose);

i.autoMIDI;
i[\k3]=INSTRUMENT(i.synths.kicks.choose);


i[\kick].seq("1 :0.5 1xx  :1 1 :2 1").speed(4);
i[\k2].seq("1 :0.5 1xx  :1 1 :2 1").speed(4);
i[\k3].seq("1 :0.5 1xx  :1 1 :2 1").speed(4);



i.autoMIDI=0;
i[\hh]=INSTRUMENT(i.synths.hihats.choose);
i[\hh].seq("1 :0.5 1xx  :1 1 :2 1").speed(4);
