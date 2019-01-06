s.boot;

i=INSTRUMENT();

i[\loop1]=I8TLooper();
i[\loop1]=I8TLooper(1);

i[\loop1].rec;
i[\loop1].start;
i[\loop1].amp=0.5;
i[\loop1].amp=0;
i[\loop1].amp=3;

i[\loop1].rate([1, 8, -1, \r, \r , 1/4]).speed(1/8)

i[\loop1].amp("1 1/4 1 0.5 0.1")
i[\loop1].amp(0)

i[\loop1].rate = 1/2;
i[\loop1].rate = -1;


i[\loop1].rate(1/8);
i[\loop1].rate(0);
i[\loop1].rate(1,0);
i[\loop1].rate(1,1);
i[\loop1].rate(3,2);

i[\loop1].rate(1/8,0);
i[\loop1].rate(1/2,1);
i[\loop1].rate(1/4,4);

i[\loop1].stop;
