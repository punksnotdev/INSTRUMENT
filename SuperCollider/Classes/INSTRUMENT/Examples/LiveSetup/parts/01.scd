/*
part 1
*/

i[\kick] = INSTRUMENT(i.synths.kicks.choose);
i[\kick].seq("1");

i[\hihat] = INSTRUMENT(i.synths.hihats.choose);
i[\hihat].seq("1  1 1 ").speed(4);

i.autoMIDI;
i[\drums] = [\kick,\hihat];
// i[\drums].stop

i.autoMIDI;
	i[\notes] = INSTRUMENT(\distRing);
	i[\notes].note("0 2  3");
	i[\notes].amp=1/1.5;
