SynthDef(\eq, {

	arg in=99,
		out=99,

		amp=1,
		pan=0,


		low=0.5,
		middle=0.5,
		high=0.5,

		lowFreq=250,
		middleFreq=2500,
		highFreq=8500,

		lowCut=1,
		lowCutFreq=35;


	var sig;
	var dry;
	var wet;

	var dBsLow=low.max(0).min(1);
	var dBsMiddle=middle.max(0).min(1);
	var dBsHigh=high.max(0).min(1);

	dBsLow = dBsLow.linlin(0,1,-9,9);
	dBsMiddle = dBsMiddle.linlin(0,1,-18,18);
	dBsHigh = dBsHigh.linlin(0,1,-18,18);

	dry = In.ar(in) * amp;

	wet = BLowShelf.ar(dry, lowFreq, 1, dBsLow);
	wet = BPeakEQ.ar(wet, middleFreq, 1, dBsMiddle);
	wet = BHiShelf.ar(wet, highFreq, 1, dBsHigh);

	sig = (wet * (1 - lowCut)) + (HPF.ar(wet,lowCutFreq)*lowCut);

	ReplaceOut.ar(out, Pan2.ar(sig,pan))

}).store;
