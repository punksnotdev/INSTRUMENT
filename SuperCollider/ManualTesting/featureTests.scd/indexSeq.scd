s.boot;

i=INSTRUMENT();

i[\kick]=SynthPlayer(\kickElectroKit);
i[\kick][0].seq(":0.5 1");



i[\snare]=SynthPlayer(\snare2);
i[\snare][0].seq(\trigger, "1",[\repeat,1])


p=ProxySpace.push(s);
~z.play;

~z = { arg note=60,t_trig,dist=3.3; (SinOsc.ar(note.midicps) * Decay2.kr(t_trig)*dist).tanh/4};

i[\z]=Proxy(~z)
i[\z][0].seq(\note,":0.25 0 3 2")
i[\z][1].seq(\note,":0.125 10 12 13 15 12")
i[\z][0].seq(\dist,":2 1 13 332")
i[\z][0].rm(\dist)
i[\z][0].proxy


i[\kick][0].seq(\trigger, "1 :0.5 1xxx ",[\repeat,3]);
i[\kick][1].seq(\trigger, ":0.125 1xxxx :0.5 1xxx",[\repeat,1]);

i[\hihat]=SynthPlayer(\hihatElectroKit);
i[\hihat][0].seq(\trigger, "1:0.5 :0.25 1xxx");

//
// i[\snare][1].seq(\trigger, "1xx 0.5xxx  ",[\speed,4])
