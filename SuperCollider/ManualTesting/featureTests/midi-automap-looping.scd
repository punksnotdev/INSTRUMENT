s.boot;


i = INSTRUMENT();

"/home/mukkekunst/Musica/furenku/sets/01-morelia/midi-multidevice.scd".load;

i.autoMIDI = true;

i[\kick] = SynthPlayer(\kickElectro);
i[\kick][0].seq("1 :0.25 1xxx ");

i[\kick] = SynthPlayer(\kickSyn1);
i[\kick][1].seq(":0.125 1xxxx");
i[\kick][2].seq(":0.25 1xxx ");

i[\hh] = SynthPlayer(\hihatElectroKit);
i[\hh][0].seq("1 :0.25 1xxx ");
i[\hh] = SynthPlayer(\hihatClosed);
i[\hh][0].seq("1 :0.125 1xxx ");


i[\hh2] = SynthPlayer(\hihatOpen);
i[\hh2][0].seq(" 1");

i[\hh3] = SynthPlayer(\hihatElectroKit);
i[\hh3][0].seq(" 1");

i[\kick4] = SynthPlayer(\kickSimple);
i[\kick4][0].seq(":0.25 1");



i[\kick5] = SynthPlayer(\kickHollow);
i[\kick5][0].seq(":0.25  1");




i[\kick6] = SynthPlayer(\kickDeep);
i[\kick6][0].seq(":1  3");
i[\kick6].fx=\revlpf;
i[\kick6].fxSet(\wet,0.75)
i[\kick6].fxSet(\cutoff,1900)
i[\kick6].fxSet(\q,0.01)
i[\kick6].fxSet(\rv1,1)
i[\kick6].fxSet(\rv2,1)

i[\kick7] = SynthPlayer(\kickSyn2);
i[\kick7][0].seq(":0.125  1");

i[\kick8] = SynthPlayer(\kickSyn3);
i[\kick8][0].seq(":0.5 0.75");







i[\kick].rm(\trigger,0);
i[\kick].rm(\trigger,2);
i[\kick].rm(\trigger,1);


i[\kick][0].seq("1 :0.25 1xxx ");
i[\kick][1].seq(":0.125 1xxxx");
i[\kick][2].seq(":0.25 1xxx ");

i[\kick].rm(\trigger);
