var parser = I8TParser();
var parameters = parser.parse("0xxxx:0.25*3    :0.125");

Tdef(\printall,{

	parameters.size.do({|index|
		"".postln;
		index.postln;
		parameters[index].postln;
		0.005.wait;
	});

	"----".postln;
	"done".postln;

}).play;
