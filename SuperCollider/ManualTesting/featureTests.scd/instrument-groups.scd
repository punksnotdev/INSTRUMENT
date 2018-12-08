s.boot;
s.doWhenBooted({
	i=INSTRUMENT();

	i[\kick]=SynthPlayer(\kickElectroKit);
	i[\snare]=SynthPlayer(\snareElectroKit);
	i[\hihat]=SynthPlayer(\hihatElectroKit);
	i[\clap]=SynthPlayer(\clapElectroKit);

	i[\kick][0].seq("1 1  ");
	i[\snare][0].seq("0 1");
	i[\hihat][0].seq("1");
	i[\clap][0].seq("0  1 ");
	i[\kick][1].seq("1 :0.5 1 0.7xx 0.25 0.7 1");
	i[\snare][1].seq("0 1xx :0.125 1xxx  1xxx ");
	i[\hihat][1].seq(":0.25 1 0.75xx ");
	i[\clap][1].seq("0 1xxx");

	i[\drums]=[\kick, \snare, \hihat, \clap];

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
