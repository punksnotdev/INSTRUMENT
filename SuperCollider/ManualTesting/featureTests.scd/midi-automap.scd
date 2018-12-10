s.boot;
i=INSTRUMENT()




i[\kick]=SynthPlayer(\kickElectro);
i[\kick][0].seq("1");
i[\hh]=SynthPlayer(\hh2);
i[\hh][0].seq("1 :0.125 1xxx ");

i[\snare]=SynthPlayer(\snare2);
i[\snare].speed=4;
i[\snare][0].seq(":2 0 1");


i.autoMIDI = true;
i.autoMIDI = false;

i[\drums]=[\kick,\hh,\snare]



i[\bass]=SynthPlayer(\tranceBazz);
i[\bass][0].seq(\note,"0xx 3 :0.5 0 1 7 8 7 ");
i[\bass].set(\rel,2)



i.map(i.midiControllers.inputs[2],i[\hh2],\amp,[0,1]);
