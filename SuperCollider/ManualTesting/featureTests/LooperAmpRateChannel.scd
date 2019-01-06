s.boot;

i=INSTRUMENT();


i[\loop1]=I8TLooper();
// i[\loop1]=I8TLooper(1);

i[\loop1].rec;
i[\loop1].start;


i[\loop1].amp=0.5;
i[\loop1].amp=1;
i[\loop1].amp=0;
i[\loop1].amp=0.3;
i[\loop1].amp=1;

i[\loop1].rate([1, 2, -1, \r, 3, \r , 1/2]).speed(1);
i[\loop1].rm(\rate,0);

i[\loop1].amp("1 1/4 1 0.5 0.1")
i[\loop1].amp(0,1)
i[\loop1].amp(1)

i[\loop1].rate = 1/8;
i[\loop1].rate = -1;


i[\loop1].rate(1/8);
i[\loop1].rate(0);

i[\loop1].rec;
i[\loop1].start;

i[\loop1].rate(1,0);
i[\loop1].rate(1,1);

i[\loop1].rate(3,0);
i[\loop1].rate(1/4,1);

i[\loop1].rate(4,0);
i[\loop1].rate(1/2,0);
i[\loop1].rate(1/2,1);
i[\loop1].rate(1/4,1);


i[\loop1].fx=\reverb;
i[\loop1].fxSet(\wet,1);
i[\loop1].fxSet(\wet,0);
i[\loop1].fxSet(\wet,1/2);
i[\loop1].fxSet(\rv1,1);
i[\loop1].fxSet(\rv2,1);

i[\loop1].fx=\revlpf
i[\loop1].fxSet(\cutoff,200)
i[\loop1].fxSet(\cutoff,1200)


i[\loop1].fx=\distortion

i[\loop1].fxSet(\gain,33);
i[\loop1].fxSet(\cutoff,400)


i[\loop1].amp(0.5)

i[\loop1].fx=nil


i[\loop1].stop;
i[\loop1].start;
