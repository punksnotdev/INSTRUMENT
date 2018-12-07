I8Tpattern
{

	var <>parameters;
	var <>pattern;

	var <hasDurations;

	*new{|pattern_,parameters_|
		^super.new.init(pattern_,parameters_);
	}
	init{|pattern_,parameters_|

		pattern_.postln;

		if( pattern_.isString, {


			var events = I8TParser.parse( pattern_ );
			// var events = this.parseEventString(pattern_);

			// var values = List.new;
			var patternEvents = List.new;


			// var amplitudes = events.collect{|e|
			// 	if( e.amplitude.isNil, { 0.5; }, { e.amplitude; });
			// };


			// events.collect({arg event; ["event:", event].postln; });

			hasDurations = false;


			events.collect({|e,i|

				var newPatternEvent = ();

				if( e.hasDurations == true, {
					hasDurations = true;
				});

				newPatternEvent.val = e.val;
				newPatternEvent.duration = e.duration;

				if( e.val != \r, {
					newPatternEvent.amplitude = e.amplitude;
				});


				// if(e.repetitions.notNil, {
				// 	e.repetitions.do{
				// 		// values.add(e.val);
				// 		patternEvents.add(newPatternEvent);
				// 	}
				// }, {
					// values.add(e.val);
					patternEvents.add(newPatternEvent);
				// });

			});


			// pattern = values.asArray;
			pattern = patternEvents.asArray;
			pattern.postln;

		}, {

			if( pattern_.isArray, {

				pattern = pattern_.collect({|patternValue|
					if( patternValue.isKindOf(Event), {
						patternValue;
					}, {
						( val: patternValue );
					});
				});

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


			if( splitStr[1].find("/").notNil, {

				var fraction1, fraction2;
				fraction1 = splitStr[1].split($/)[0];
				fraction2 = splitStr[1].split($/)[1];

				newGroup.duration = fraction1.asFloat / fraction2.asFloat;

			}, {

				newGroup.duration = splitStr[1].asFloat;
				newGroup.amplitude = this.getAmplitude( splitStr[1] );

			});

			if( this.getRepetitions( splitStr[0] ) > 1, {
				newGroup.repetitions = this.getRepetitions( splitStr[0] );
				newGroup.amplitude = this.getAmplitude( splitStr[0] );
			});

			newGroup.hasDurations = true;
		}, {

			newGroup.val = str.asFloat;

			if( this.getRepetitions( str ) > 1, {
				newGroup.repetitions = this.getRepetitions( str );
			});

			newGroup.amplitude = this.getAmplitude( str );

		});

		^newGroup;

	}

	getAmplitude{|string|

		var amplitude = 0.5;
		var factor = 0.1;

		if( string.find("*").notNil, {
			amplitude = this.getOperatorParameter(string, "*")
			},
		{

			if( string.find("p").notNil, {

				amplitude = amplitude - (factor * this.getOperatorValue(string, "p"));

			});
			if( string.find("f").notNil, {

				amplitude = amplitude + (factor * this.getOperatorValue(string, "f"));

			});
		});

		^amplitude.asFloat;


	}

	getRepetitions{|string|

		var repetitions = 0;

		if( string.find("x").notNil, {
			repetitions = this.getOperatorValue(string,"x");
		});

		^repetitions.asInteger;

	}

	getOperatorValue {|string,char|
		var operatorValue = 0;

		if(
			(
				string.findBackwards(char) == (string.size - 1)
				||
				this.isBeforeOtherOperator(string,string.findBackwards(char))
			)
		, {
			operatorValue = this.getOperatorRepetitions(string, char);
		}, {
			operatorValue = this.getOperatorParameter(string,char);
		});

		^operatorValue;

	}
	getOperatorRepetitions {|string,char|

		var indexes = string.findAll( char );

		var operatorValue = 0;

		if(this.areIndexesSequential(indexes),{
			operatorValue = indexes.size;
		});

		^operatorValue
	}
	getOperatorParameter {|string,char|

		var indexes = string.findAll( char );

		var operatorValue = 0;

		var operatorValueStr = "";

		((string.size-1) - indexes.maxItem).do{|index|
			operatorValueStr = operatorValueStr ++ string[(string.size-1)-index];
		};

		operatorValue = operatorValueStr.reverse.asInteger;

		^operatorValue
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

	isBeforeOtherOperator {|string,index|

		var isBefore = false;

		var operators = ["p","f","x",":","*"];

		operators.collect({|o|
			if( string[ index + 1 ].asString.compare(o) == 0, {
				isBefore = true;
			});
		});

		^isBefore;
	}

}
