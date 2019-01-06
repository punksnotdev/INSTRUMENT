s.boot;

i=INSTRUMENT();

i[\loop1]=I8TLooper(0);

i[\loop1].rec;

i[\loop1].play;
i[\loop1].amp=0.5;
i[\loop1].amp=0;
i[\loop1].amp=1;

i[\loop1].rate=2;
i[\loop1].rate=1/2;
i[\loop1].rate=1;

i[\loop1].stop;
