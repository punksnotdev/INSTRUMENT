().play
i=INSTRUMENT();
i.play;


i[\kick]=INSTRUMENT(\kickElectro);
i[\kick].seq("1");

i[\hihat]=INSTRUMENT(\hihatElectro);
i[\hihat].seq("1");
i[\hihat].clock=2


i[\kh2]=[\hihat];
i[\kh2].clock=4

i[\kh]=[\kick,\hihat];
i[\kh].amp=1/2
i[\kh].clock=1
i[\kh].fx=\delay1
i[\kh].fx=nil
i[\kh].fx=\reverb

i[\kh].play
