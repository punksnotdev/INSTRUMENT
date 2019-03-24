s.boot;

(
	i=INSTRUMENT();

	i.synths = i.loadPath(Platform.userExtensionDir++'/INSTRUMENT/INSTRUMENT/Sounds/SynthDefs/*');

	// i.listSynths;

)

i.stop;

i.synths.keysValuesDo({|k,v|
	[k,v].postln;
});



(
i.synths.drums.kits.electro.hihat == i.synths.drums.electro.hihat
&&
i.synths.drums.electro.hihat == i.synths.drums.kits.hihat
&&
i.synths.drums.kits.hihat == i.synths.drums.hihat
&&
i.synths.drums.hihat == i.synths.electro.hihat
&&
i.synths.electro|.hihat == i.synths.hihat
)

i.synths.electro.kick


// i=INSTRUMENT();
i.play;

i.synths.bass.noise.pads

i[\kick]=INSTRUMENT(i.synths.electro.kick);

i[\kick][0].seq("1");
i[\kick][1].seq("1").speed(2);
i[\kick][2].seq("1").speed(4);
i[\kick][3].seq("1").speed(16).repeat(16);
i[\kick][4].seq("1").speed(32).do(8);
i[\kick][5].seq("1").speed(1/4).x(1);


i[\hihat]=INSTRUMENT(i.synths.electro.hihat);

i[\hihat][0].seq("1  0.5xx :0.25 1xxx ");
i[\hihat][1].seq("1  0.5xx :0.25 1xxx ").speed(2);
i[\hihat][2].seq("1  0.5xx :0.25 1xxx ").speed(4);
i[\hihat][3].seq("1  0.5xx :0.25 1xxx ").speed(16).repeat(16);
i[\hihat][4].seq("1  0.5xx :0.25 1xxx ").speed(32).do(8);
i[\hihat][5].seq("1  0.5xx :0.25 1xxx ").speed(1/2).x(1);
i[\hihat].fx=\reverb
i[\hihat].fxSet(\wet,1)
i[\hihat].fxSet(\rv1,1)
i[\hihat].fxSet(\rv2,1)
i[\hihat].amp=2
i[\hihat].clock=2


i[\drums]=[\kick,\hihat];

i[\drums].clock=2
i[\drums].go(9)
i[\drums].go(7)
i[\drums].go(4)

i[\drums].fx=\reverb
i[\drums].fxSet(\wet,1)
i[\drums].fxSet(\rv1,1)
i[\drums].fxSet(\rv2,1)



//
//
// i.synths.drums.keysValuesDo({
// 	arg k,v;
// 	[k].postln;
//
// });
//
// i.synths.kits.electro==i.synths.electro
//
// electro.kick
