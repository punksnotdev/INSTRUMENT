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

			var values = events.collect({|e| e.val });

			pattern = values;

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
[i,"add rest"].postln;
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
		var newGroup = ();
		group.chars.collect({|c| str = str ++ c });
		group = nil;

		newGroup.val = str.asFloat;


		^newGroup;

	}


}
