s.boot;
p=ProxySpace.push(s);
i=INSTRUMENT();


~z.play;
~z={|freq=440, amp=1, t_trig=1| Decay2.kr(t_trig) * (SinOsc.ar (freq)/2) * amp};


i[\z]=Proxy(~z)

i[\z].seq(\note, "0 3 7 10 14");
i[\z].seq(\note, "0 3 7  10   14");
i[\z].seq(\note, "0 3 7xxxx:0.5  10   14");
i[\z].seq(\note, "0 3 :0.5 7 7 7 7 :1  10   14");
i[\z].seq(\note, "0 3 :0.5 7 3 7 5 :1  10   14 23");
i[\z].seq(\note, "0xxxx:1.5 3xx:0.5 :0.5 7 3xxxxx 7 5 :1  10   14 23 34", [ \speed, 4, \repeat, 2]);


i[\z][0].seq(\note, "0 2 3 5 7 8 10", [\repeat, 1]);
i[\z][1].seq(\note, "12 10 8 7 5 3 2", [\repeat, 1]);


i[\kick]=SynthPlayer(\kickSyn1);
i[\kick].seq("1");

i[\hh]=SynthPlayer(\hihatClosed);
i[\hh].seq(" 1");
i[\hh].speed=2;

i[\kick][0].seq("1 0.5 1 0.5 1xxxx", [\speed, 2]);
i[\kick][1].seq("1xx 0.5 1xxxx 0.5 1", [\speed, 2]);
i[\kick][2].seq("1xxxx   0.5 1 0.5xxx 1", [\speed, 2]);
i[\hh][0].seq("0.5 1xx 0.7", [\speed, 3]);
i[\hh][1].seq("0.5 1xx 0.7 1xxx 0.5", [\speed, 3]);
i[\hh][2].seq("0.5xx 1   0.5 1   0.5 1", [\speed, 3]);



// es lo mismo que

i[\kick].speed=2;
i[\hh].speed=3;

i[\kick][0].seq("1 0.5 1 0.5 1xxxx");
i[\kick][1].seq("1xx 0.5 1xxxx 0.5 1");
i[\kick][2].seq("1xxxx   0.5 1 0.5xxx 1");
i[\hh][0].seq("0.5 1xx 0.7");
i[\hh][1].seq("0.5 1xx 0.7 1xxx 0.5");
i[\hh][2].seq("0.5xx 1   0.5 1   0.5 1");






// variar velocidades

i[\kick][0].seq("1 0.5");
i[\kick][1].seq(":0.25 1xxx ");
i[\kick][2].seq(":0.125 1xxxx    ");

i[\hh][0].seq(":1 1xx ");
i[\hh][1].seq(":0.5 1xx ");
i[\hh][2].seq(":0.25 1xx ");



// jugar con play_parameters

i[\kick][0].seq("1 0.5", [\repeat, 4, \speed, 1]);
i[\kick][0].seq("1 0.5", [\repeat, 4, \speed, 1]);
i[\kick][0].seq("1 0.5", [\repeat, 4, \speed, 1]);

i[\hh][0].seq("1xx ", [\repeat, 4, \speed, 1]);
i[\hh][0].seq("1xx ", [\repeat, 4, \speed, 1]);
i[\hh][0].seq("1xx ", [\repeat, 4, \speed, 1]);


// vas

i[\kick][0].seq("1 0.5 1xx", [\repeat, 4, \speed, 8]);
i[\kick][1].seq("1 0.5 1xx", [\repeat, 8, \speed, 2]);
i[\kick][2].seq("1 0.5 1xx", [\repeat, 16, \speed, 8]);

i[\hh][0].seq("1xx ", [\repeat, 16, \speed, 8]);
i[\hh][1].seq("1xx ", [\repeat, 4, \speed, 4]);
i[\hh][2].seq("1xx ", [\repeat, 8, \speed, 16]);


i[\kick].synthdef=\kickElectro;
i[\kick].synthdef=\kickHard;
i[\kick].synthdef=\kickSyn2;
i[\kick].synthdef=\kickHollow;
i[\kick].synthdef=\kickGabber;

i[\kick].seq(\synthdef, [
	\kickElectro,
	\kickHard,
	\kickSyn2,
	\kickHollow,
	\kickGabber
]);

i[\hh].seq(\synthdef, [
	\hihatClosed,
	\hihatShort,
	\hihatOpen,
], [\speed, 1/4] );


// agrupar instrumentos

i[\drums]=[\kick,\hh];

i[\drums].speed=1;
i[\drums].speed=1/2;
i[\drums].speed=1/4;
i[\drums].speed=1/8;
i[\drums].speed=1/16;
i[\drums].speed=1;
i[\drums].speed=2;
i[\drums].speed=4;
i[\drums].speed=8;
i[\drums].speed=1;


i[\drums].amp=1/4;
i[\drums].amp=1/2;
i[\drums].amp=1;

i[\drums].go(0);
i[\drums].stop;
i[\drums].play;







i[\kick].seq(\synthdef, [
	\kickSyn1,
	\r,\r,\r,
	\kickSyn2,
	\r,\r,\r,
	\kickSyn3
]);


i[\hh].seq(\synthdef, [
	\hihatShort,
	\r,\r,\r,
	\hihatOpen
]);





i[\drums].fx=\reverb;

i[\drums].fxSet(\wet,0);
i[\drums].fxSet(\wet,0.5);
i[\drums].fxSet(\wet,1);

i[\drums].fxSet(\rv1,1);
i[\drums].fxSet(\rv2,1);

i[\drums].fxSet(\rv1,0.1);
i[\drums].fxSet(\rv2,0.1);
