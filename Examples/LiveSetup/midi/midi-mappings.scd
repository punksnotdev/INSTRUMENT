(

i[\bassMono]=INSTRUMENT(\mono_bassPhatPhase);
i[\bassMono].octave=0;

i[\bassMono].mode=\mono;
i[\bassMono].set(\legato,0.3);

i.map(m.mpk_mk2.piano,i[\bassMono],\note);
i.map(m.mpk_mk2.knob5,i[\bassMono],\gain,[1,8]);
i.map(m.mpk_mk2.knob6,i[\bassMono],\factor2,[0.1,3]);



)

i.unmap(m.mpk_mk2.piano,i[\bassMono],\note);

i.unmap(m.mpk_mk2.piano);


(

i[\bassMono]=INSTRUMENT(\bassSmooth);
i[\bassMono].octave=0;
i[\bassMono].mode=\mono;
i.map(m.mpk_mk2.piano,i[\bassMono],\note);
i.map(m.mpk_mk2.knob5,i[\bassMono],\gain,[0,1]);
i.map(m.mpk_mk2.knob6,i[\bassMono],\q,[0,1]);
i.map(m.mpk_mk2.knob7,i[\bassMono],\att,[0,1]);
i.map(m.mpk_mk2.knob8,i[\bassMono],\rel,[0,1]);


)
i.unmap(m.mpk_mk2.piano,i[\bassMono],\note);


(
i[\bassPoly]=INSTRUMENT(\bassWobbleRing);
i[\bassPoly].octave=0;
i[\bassPoly].mode=\poly;
i.map(m.mpk_mk2.piano,i[\bassPoly],\note);
i.map(m.mpk_mk2.knob7,i[\bassPoly],\att,[0,2]);
i.map(m.mpk_mk2.knob8,i[\bassPoly],\rel,[0,2]);

)

i.unmap(m.mpk_mk2.piano,i[\bassPoly],\note);



(
i[\chaosPerc]=INSTRUMENT(\chaosPerc);
i[\chaosPerc].octave=0;
i.map(m.mpk_mk2.pads,i[\chaosPerc],\note);
i.map(m.mpk_mk2.knob1,i[\chaosPerc],\rel);
i.map(m.mpk_mk2.knob2,i[\chaosPerc],\att,[0.01,3]);
i.map(m.mpk_mk2.knob4,i[\chaosPerc],\amp,[0,2.5]);
)
