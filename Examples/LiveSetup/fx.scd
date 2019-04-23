// i=INSTRUMENT().play;
i.vox=INSTRUMENT(\audioInput);
i.vox.fx=\reverb;

i.vox.synth.set(\inBus,0)


s.meter(4)
i.vox.autostart;
i.vox.fx=nil;

i.vox.synth.release;


(

i.vox.fxSet(\wet,0.9);
i.vox.fxSet(\room,0.9);
i.vox.fxSet(\damp,0.9);

)

i.vox.stop;

i.vox.fx=nil;
i.vox.fx="gateDistort.overdrive";
i.vox.fx="gateDistort.hardcore";
i.vox.fx="gateDistort.extreme";
i.vox.fx="gateDistort.crunch";



i.vox.fx=\shineDestroy;
i.vox.amp=1/2
i.vox.fxSet(\gain,8)
i.vox.fxSet(\delay,1/8)
i.vox.fxSet(\delay,1/4)
i.vox.fxSet(\delay,2/3)
i.vox.fxSet(\cutoff,1230)
i.vox.fxSet(\cutoff,5230)
i.vox.fxSet(\cutoff,530)
i.vox.fxSet(\gain,3)
