I8Tchord
{

	var <>note;
	var <>type;
	var <>inversion;
	var chord;

	*new{|note_,type_,inversion_=0|
		^super.new.init(note_,type_,inversion_);
	}

	init{|note_,type_,inversion_=0|
		note = note_;
		type = type_;
		inversion = inversion_;
	}

	chord {|type|

		var chordNames, intervals;

		chordNames=[\M, \m, \M7, \m7, \Mmaj7, \mmaj7, \M9, \M9m, \m9, \m9m,\sus2,\sus4];

		intervals = [
		  [0,4,7], //M
		  [0,3,7], //m
		  [10,4,7], //M7
		  [10,3,7], //m7
		  [10,3,7], //Mmaj7
		  [11,4,7], //mmaj7
		  [11,3,7], //M9
		  [0,4,7,14], //M9m
		  [0,4,7,13], //m9
		  [0,3,7,14], //m9m
		  [0,2,7], //sus2
		  [0,4,7], //sus4
		];

		if(chordNames.includes(type))
		{
			[type,chordNames.indexOf(type)].postln;
			intervals[chordNames.indexOf(type)].rotate(inversion).postln;
		  ^note+intervals[chordNames.indexOf(type)].rotate(inversion);
		}
		{
			^0;
		};

	}


}
