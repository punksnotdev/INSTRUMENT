s.boot;

s.doWhenBooted({

	TempoClock.default.tempo = 195/60;

	i = INSTRUMENT();

	i[\kick]=SynthPlayer(\kickHard);

	i[\kick][0].seq(\trigger,"1");
	i[\kick][1].seq(\trigger,"1xx:0.125");
	i[\kick][0].seq(\trigger,"1xxx:0.125 ");


	Tdef( \redeclare, {

		inf.do {

			i[\kick]=SynthPlayer(\kickHollow);
			4.wait;
			i[\kick]=SynthPlayer(\kickFull);
			4.wait;
			i[\kick]=SynthPlayer(\kickDeep);
			4.wait;
			i[\kick]=SynthPlayer(\kickSimple);
			4.wait;
			i[\kick]=SynthPlayer(\kickHard);
			4.wait;
			i[\kick]=SynthPlayer(\kickElectro);
			4.wait;
			i[\kick]=SynthPlayer(\kickGabber);
			4.wait;
			i[\kick]=SynthPlayer(\kickSyn1);
			4.wait;
			i[\kick]=SynthPlayer(\kickSyn2);
			4.wait;
			i[\kick]=SynthPlayer(\kickSyn3);
			4.wait;

		}


	}).play;


})
