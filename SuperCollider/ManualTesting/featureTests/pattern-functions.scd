(
s.boot;
s.doWhenBooted
({

s.meter;
s.volume=(-12);
().play;

i = INSTRUMENT();

i[\bass]=SynthPlayer(\tranceBazz);

i[\bass][0].note("0").speed(4).do(8);

i[\bass][1].note("2 3").speed(4).do(4);
i[\bass][2].note("5 3 5 2").speed(4).do(2);
i[\bass][3].note("7 3 5 2 3 0  ").speed(4).do(1);
i[\bass].note("0 2 3 5 7").reverse.maybe(0).speed(4).do(1);

i[\melody]=SynthPlayer(\distNote1);
i[\melody].octave=6;
i[\melody].amp=0.3;
i[\melody][0].note("0 2 3").mirror.pyramid.mirror.speed(12).do(2);
i[\melody][1].note("2 3 5").mirror.speed(4).do(2);
i[\melody][2].note("2 3 5 7 9 12 15").random.speed(8).do(8);

i[\kick]=SynthPlayer(\kickSyn1);

i[\kick][0].seq(":2 1");

i[\hh]=SynthPlayer(\hihatElectroKit);

i[\hh].seq("1xx 0.5xx 0.25").speed(4).maybe;
i[\hh].seq("1xx 0.5xx 0.25").speed(4).maybe(0.3);

i[\hh][0].seq(":0.5 1 0.5 :0.25 0.25xxx ");
i[\hh][1].seq("1 0.5 0.25").speed(2).reverse;
i[\hh][1].seq("1 0.5 0.25").speed(4).do(8).maybe;
i[\hh][2].seq(":0.25 1 0.5 :0.25 0.25xxx ");
i[\hh][3].seq("1 0.5 :0.25 0.25xxx ").speed(2);


i[\hh2]=SynthPlayer(\hhClosed);

i[\hh2][0].seq("1xxx ").speed(8).maybe(0.2);
i[\hh2][1].seq("1xxx ").speed(8).maybe(0.4);
i[\hh2][2].seq("1xxx ").speed(8).maybe(0.6);
i[\hh2][3].seq("1xxx ").speed(8).maybe(0.8);


});
)
