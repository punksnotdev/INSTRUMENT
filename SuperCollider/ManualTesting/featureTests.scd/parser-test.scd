// e = I8TParser.parse("0xxxx:0.25*3    :0.125 4xx 3xxx 2xxxx 1");
p = I8TParser.parse("12xxxx:0.25*3    :0.125 14xx 13xxx 12xxxx 11");

p = I8TParser.parse("1 3    2  ");
p = I8TParser.parse("1  2   3  4xx:0.125*3.22226   :0.125");

p
e = I8TParser.getEventsList(p)

e

[ GRP, 1 ]
[ GRP,  2 ]
[ group, 1, List[  ] ]
[ group,  2, List[   ] ]
8.isInteger

Tdef(\printall,{

	e.size.do({|index|
		"".postln;
		index.postln;
		e[index].postln;
		0.005.wait;
	});

	"----".postln;
	"done".postln;

}).play;
