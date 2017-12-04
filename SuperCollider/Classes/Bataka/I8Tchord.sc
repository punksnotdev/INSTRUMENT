I8Tchord
{

	var <>note;
	var <>type;
	var chord;

	*new{|note_,type_|
		^super.new.init(note_,type_);
	}

	init{|note_,type_|
		note = note_;
		type = type_;
	}

	chord {|chord=\m|

		var chordNames, intervals;

		chordNames=[\M, \m, \M7, \m7, \Mmaj7, \mmaj7, \M9, \M9m, \m9, \m9m];

		intervals = [
		  [0,7,12,16,19,24],
		  [0,7,12,15,19,24],
		  [0,7,10,16,19,24],
		  [0,7,10,15,19,24],
		  [0,7,10,15,19,24],
		  [0,7,11,16,19,24],
		  [0,7,11,15,19,24],
		  [0,7,14,16,19,24],
		  [0,7,13,16,19,24],
		  [0,7,14,15,19,24],
		  [0,7,13,15,19,24]
		];

		if(chordNames.includes(chord))
		{
		  ^note+intervals[chordNames.indexOf(chord)];
		}
		{
			^0;
		};

	}


}
