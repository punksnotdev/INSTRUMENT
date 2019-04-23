I8TChord
{

	var <>note;
	var <>type;
	var <>inversion;
	var <>add_intervals;
	var chord;
	var chordNames;

	*new{|note_,type_,inversion_=0,add_intervals_|
		^super.new.init(note_,type_,inversion_,add_intervals_);
	}

	init{|note_,type_,inversion_=0,add_intervals_|
		if( note_.isKindOf( Number), {

			chordNames=[\M, \m, \M7, \m7, \dim, \aug, \Mmaj7, \mmaj7, \M9, \M9m, \m9, \m9m,\sus2,\sus4];

			if( chordNames.includes( type_),
			{

				note = note_;
				type = type_;
				inversion = inversion_;
				add_intervals=add_intervals_;
			}, {
				if( type_.isKindOf(Array), {

					note = note_;
					type = type_;
					inversion = inversion_;
					add_intervals=add_intervals_;

				}, {

					"Chord type not valid".postln;

				});

			});
		}, {
			"Chord note must be a Number".postln;
		});

	}

	chord {

		var intervals;
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



		intervals = [
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3,
		  12
		  ], //M
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3,
		  12
		  ], //m
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  10+inversionOctave3,
		  12
		  ], //M7
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  10+inversionOctave3,
		  12
		  ], //m7
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  6+inversionOctave3,
		  12
		  ], //dim
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  8+inversionOctave3,
		  12
		  ], //aug
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3,
		  11+inversionOctave3,
		  12
		  ], //Mmaj7
		  [
		  0+inversionOctave1,
		  3+inversionOctave1,
		  7+inversionOctave2,
		  11+inversionOctave3,
		  12
		  ], //mmaj7
		  [
		  1+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3,
		  12
		  ], //M9
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3,
		  14], //M9m
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3,
		  13], //m9
		  [
		  0+inversionOctave1,
		  3+inversionOctave2,
		  7+inversionOctave3,
		  14], //m9m
		  [
		  0+inversionOctave1,
		  2+inversionOctave2,
		  7+inversionOctave3,
		  12
		  ], //sus2
		  [
		  0+inversionOctave1,
		  4+inversionOctave2,
		  7+inversionOctave3,
		  12
		  ], //sus4
		];

		if( add_intervals.isArray == false, {
			add_intervals = Array.new;
		});

		if( type.isKindOf(Array), {
			(([note]++(note+type)).rotate(inversion)++add_intervals).postln;
			^(([note]++(note+type)).rotate(inversion)++add_intervals);

		}, {
			if(chordNames.includes(type))
			{
				var chordIntervals = intervals[chordNames.indexOf(type)];
				var newChord = List.new;
				(4-add_intervals.size).do({|i|
					newChord.add( chordIntervals[i] );
				});
				(note+(newChord.rotate(inversion)++add_intervals)).postln;
				^(note+(newChord.rotate(inversion)++add_intervals));
			}
		});


		{
			^0;
		};

	}


}
