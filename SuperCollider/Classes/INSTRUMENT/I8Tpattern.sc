I8Tpattern
{

	var <>parameters;
	var <>pattern;

	*new{|pattern_,parameters_|
		^super.new.init(pattern_,parameters_);
	}
	init{|pattern_,parameters_|

		if( pattern_.isString, {

			var events = this.parseEventString(pattern_);

			var values = List.new;

			events.collect({|e|
				if(e.repetitions.notNil, {
					e.repetitions.do{
						values.add(e.val);
					}
				}, {
					values.add(e.val);
				});
			});

			pattern = values.asArray;

		}, {

			if( pattern_.isArray, {

				pattern = pattern_;

			}, {

				["!!Pattern not recognized",pattern].postln;

			});

		});

		parameters = parameters_;
	}





    parseEventString {|input|

        var spaces;
        var currentGroup;
        var events;

        events = List.new;

        spaces = input.findAll(" ");

        input.size.do({|i|
            var char = input[i];

	        if( currentGroup == nil, {

                if( char.asString.compare(" ") == 0, {

					events.add( ( val: \r ) );

                },
                {
                    // not a space
                    currentGroup = ();

                    // currentGroup.index = i;
                    currentGroup.chars = List[char];

					if( i == (input.size - 1), {
						events.add( this.closeEventGroup(currentGroup) );
						currentGroup = nil;
					});

                });
            }, {

                // if currentGroup not Nil,
                if( ( i == (input.size - 1)), {

					if( char.asString.compare(" ") != 0,  {
						currentGroup.chars.add( char );
					});

					events.add( this.closeEventGroup(currentGroup) );
					currentGroup = nil;

					if( char.asString.compare(" ") == 0,  {
						events.add( ( val: \r ) );
						[i,"add last rest"].postln;
                    });


                }, {

					if(char.asString.compare(" ") == 0, {

						events.add( this.closeEventGroup(currentGroup) );
						currentGroup = nil;

						if( input[i+1].asString.compare(" ") == 0, {

							var areAllNextCharsSpaces = true;


							(input.size - (i+1)).do{|j|
								if( input[ (input.size-1) - j].asString.compare(" ") != 0, {
									areAllNextCharsSpaces = false;
								})
							};

							if( areAllNextCharsSpaces, {
								[i,"add rest"].postln;
								events.add( ( val: \r ) );
							})

						})

					}, {

						// not a space
						currentGroup.chars.add( char );

					});
                });

            })

        });
        ^events;

    }

	closeEventGroup{|group|

		var str = "";
		var splitStr;
		var newGroup = ();


		group.chars.collect({|c| str = str ++ c });
		if( str.find(":").notNil, {

			splitStr = str.split($:);

			newGroup.val = splitStr[0].asFloat;
			newGroup.duration = splitStr[1].asFloat;

			if( this.getRepetitions( splitStr[0] ) > 1, {
				newGroup.repetitions = this.getRepetitions( splitStr[0] );
			});

		}, {

			newGroup.val = str.asFloat;

			if( this.getRepetitions( str ) > 1, {
				newGroup.repetitions = this.getRepetitions( str );
			});

		});


		newGroup.postln;

		^newGroup;

	}

	getRepetitions{|string|

		var repetitions = 0;

		if( string.find("x").notNil, {

			var indexes = string.findAll("x");


			if( indexes.maxItem == (string.size - 1), {

				if(this.areIndexesSequential(indexes),{
					repetitions = indexes.size;
				});

			}, {
				var repetitionStr = "";

				((string.size-1) - indexes.maxItem).do{|index|
					repetitionStr = repetitionStr ++ string[(string.size-1)-index];
				};

				repetitions = repetitionStr.reverse.asInteger;

			});

		});

		^repetitions;

	}

	areIndexesSequential {|indexes|

		var sequential=true;

		var lastIndex = indexes[0];

		indexes.collect({|i|
			if( (i.asInteger - lastIndex.asInteger) > 1, {
				sequential=false;
			}, {
				lastIndex = i;
			});
		});

		if( sequential == false, {
			"invalid Pattern: x's must be sequential".postln;
		});

		^sequential;

	}


}
