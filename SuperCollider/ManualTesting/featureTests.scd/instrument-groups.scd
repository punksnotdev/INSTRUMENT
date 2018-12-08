s.boot;
s.doWhenBooted({
	i=INSTRUMENT();

	i[\kick]=SynthPlayer(\kickElectroKit);
	i[\snare]=SynthPlayer(\snareElectroKit);
	i[\hihat1]=SynthPlayer(\hihatElectroKit);
	i[\hihat2]=SynthPlayer(\hhClosed);
	i[\clap]=SynthPlayer(\clapElectroKit);

	i[\kick][0].seq("1 1  ");
	i[\snare][0].seq("0 1");
	i[\hihat1][0].seq(":0.25 0 1xx");
	i[\hihat2][0].seq(":0.5 1 :0.5 0.7xx ");
	i[\clap][0].seq("0  1 ");
	i[\kick][1].seq("1 :0.5 1 0.7xx 0.25 0.7 1");
	i[\snare][1].seq("0 1xx :0.125 1xxx  1xxx ");
	i[\hihat1][1].seq(":0.25 1 0.75xx ");
	i[\hihat2][1].seq(":0.125  1  0.75xx ");
	i[\clap][1].seq("0 1xxx");

	i[\hihats]=[\hihat1,\hihat2];
	i[\hihats].speed=1/2;
	i[\hihats].amp=4;

	i[\hihats].fx=\reverb;


	i[\drums]=[\kick, \snare, \hihats, \clap];

	i[\hihats].amp=2;
	i[\drums].amp = 0.5;
	i[\hihats].amp=1;
	i[\drums].amp = 1;
	i[\drums].speed = 0.5;
	i[\drums].speed = 8;
	i[\drums].speed = 1;
	i[\drums].speed = 1;


	i[\drums].speed = 4;

/*
	i[\drums].speed(1/4);
	i[\drums].speed(4)
	i[\drums].amp = 1
	i[\drums].go(0)
	i[\drums].fx = \reverb
	i[\drums].setFx(\wet,0.1)
	i[\drums].setFx(\wet,0.9)
	i[\drums].setFx(\wet,0.5)
	i[\drums].fx()


*/


});
