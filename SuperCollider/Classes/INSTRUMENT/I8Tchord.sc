I8Tchord
{

	var <>note;
	var <>type;
	var <>inversion;
	var <>add_intervals;
	var chord;

	*new{|note_,type_,inversion_=0,add_intervals_|
		^super.new.init(note_,type_,inversion_,add_intervals_);
	}

	init{|note_,type_,inversion_=0,add_intervals_|
		note = note_;
		type = type_;
		inversion = inversion_;
		add_intervals=add_intervals_;
	}

	chord {|type|

		var chordNames, intervals;
		var inversionOctave1;
		var inversionOctave2;
		var inversionOctave3;

		inversionOctave1=0;
		inversionOctave2=0;
		inversionOctave3=0;

		switch( inversion,
			1, {
				inversionOctave1 = 12;
			},
			2, {
				inversionOctave1 = 12;
				inversionOctave2 = 12;
			},
			3, {
				inversionOctave1 = 12;
				inversionOctave2 = 12;
				inversionOctave3 = 12;
			},
		);

		chordNames=[\M, \m, \M7, \m7, \Mmaj7, \mmaj7, \M9, \M9m, \m9, \m9m,\sus2,\sus4];

		intervals = [
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3
		  ], //M
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3
		  ], //m
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3
		  ], //M7
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3
		  ], //m7
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3
		  ], //Mmaj7
		  [
		  1+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3
		  ], //mmaj7
		  [
		  1+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3
		  ], //M9
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3
		  ,14], //M9m
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3
		  ,13], //m9
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3
		  ,14], //m9m
		  [
		  0+inversionOctave1,
		  2+inversionOctave2,
		  7+inversionOctave3
		  ], //sus2
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3
		  ], //sus4
		];

		if( add_intervals.isArray == false, {
			add_intervals = Array.new;
		});

		if(chordNames.includes(type))
		{
			// [type,chordNames.indexOf(type)].postln;
			// intervals[chordNames.indexOf(type)].rotate(inversion).postln;
			(note+(intervals[chordNames.indexOf(type)].rotate(inversion)++add_intervals)).postln;
		  ^(note+(intervals[chordNames.indexOf(type)].rotate(inversion)++add_intervals));
		}
		{
			^0;
		};

	}


}
