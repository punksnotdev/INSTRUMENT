I8TPattern
{

	var <>parameters;
	var <>pattern;

	var <hasDurations;
	var <totalDuration;

	var <>played;

	*new{|pattern_,parameters_|
		^super.new.init(pattern_,parameters_);
	}

	init{|pattern_,parameters_|

		// var timePattern;
		// var timePatternTime;

		totalDuration = 0;
		played = false;
		if( pattern_.isString, {


			var events = I8TParser.parse( pattern_ );
			// var events = this.parseEventString(pattern_);
			// var values = List.new;
			var patternEvents = List.new;


			events.collect{|e|
				if( e.amplitude.isNil, { e.amplitude=0.5; });
			};


			hasDurations = false;


			events.collect({|e,i|

				if( e.duration.notNil) {
					hasDurations = true;
				};

			});

			patternEvents = events.collect({|e,i|

				var newPatternEvent = ();

				newPatternEvent.val = e.val;
				// newPatternEvent.val = ( val: e.val );
				if( e.duration.notNil, {
					newPatternEvent.duration = e.duration.asFloat;
				}, {
					newPatternEvent.duration = 1;
				});


				if( e.val != \r, {
					if( e.amplitude.notNil ) {
						newPatternEvent.amplitude = e.amplitude.asFloat;
					};
					if( e.rel.notNil ) {
						newPatternEvent.rel = e.rel.asFloat;
					};
				});


				newPatternEvent

			});


			// pattern = values.asArray;
			pattern = patternEvents.asArray;

			totalDuration = pattern.collect({|event| event.duration }).sum;

		}, {

			if( pattern_.isArray, {

				pattern = pattern_.collect({|patternValue|
					if( patternValue.isKindOf(Event), {
						patternValue;
					}, {
						if( patternValue.isKindOf(I8TChord), {
							(val: patternValue.chord)
						}, {
							( val: patternValue, duration: 1, amplitude: 0.5 );
						});
					});
				});

				// TODO: fix problem with ordinary sequencer and remove this hack to use durationSequencer:
				hasDurations = true;



			}, {

				["!!Pattern not recognized",pattern].postln;

			});

		});


		// timePattern = IdentityDictionary.new;
		// timePatternTime = 0;
		//
		// pattern.collect({|event|
		// 	timePattern.put( timePatternTime, event );
		// 	timePatternTime = timePatternTime + event.duration;
		// });
		//


		// pattern = timePattern;
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
				newGroup.rel = this.getRelease( splitStr[1] );

			});

			if( this.getRepetitions( splitStr[0] ) > 1, {
				newGroup.repetitions = this.getRepetitions( splitStr[0] );
				newGroup.amplitude = this.getAmplitude( splitStr[0] );
				newGroup.rel = this.getRelease( splitStr[0] );
			});

			newGroup.hasDurations = true;
		}, {

			newGroup.val = str.asFloat;

			if( this.getRepetitions( str ) > 1, {
				newGroup.repetitions = this.getRepetitions( str );
			});

			newGroup.amplitude = this.getAmplitude( str );
			newGroup.rel = this.getRelease( str );

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

	getRelease{|string|

		var rel = nil;
		var factor = 0.5;

		if( (string.find("|").notNil || string.find("-").notNil), {


			if( string.find("|").notNil, {

				rel = 1 - (factor * this.getOperatorValue(string, "|"));

			});
			if( string.find("-").notNil, {

				rel = 1 + (factor * this.getOperatorValue(string, "-"));

			});
		});

		^rel.asFloat;


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
