var parseSeqString = {|input|

	var groupStrings = List.new;
	var lastChar;

	var buildingGroupChars = "";

	var index = 0;

	input.isKindOf(String).postln;


	// input.collect({|char|
	// 	char.postln;
	// });
	input.size.do({|index|

		var char = input[ index ];

		char.postln;



		// if is last character

		if( index >= (input.size - 1) ) {

			if( char != Char.space ) {
				buildingGroupChars = buildingGroupChars ++ char;
				groupStrings.add( buildingGroupChars );
				buildingGroupChars = "";
			}

		};

		// if is not last character
		if( index < (input.size-1) ) {
			// if current char is space

			if( char == Char.space ) {

				// if last char is also space

				if( lastChar.notNil ) {

					if( lastChar == Char.space ) {
						// start new group with a space
						buildingGroupChars = buildingGroupChars ++ $r;//char;

					};

					// if current char is not a space
					// create group from to current building chars

					if( lastChar != Char.space ) {
("last --- not a space: "++lastChar).postln;
						groupStrings.add( buildingGroupChars );
						buildingGroupChars = "";

					}

				};

				if( lastChar.isNil, {

					buildingGroupChars = buildingGroupChars ++ char;

				});

			};


			// if current char is not a space
			// append to current building group

			if( char != Char.space ) {

				if( lastChar == Char.space ) {

					if( char != $: ) {

						if( buildingGroupChars.size > 0 ) {

							groupStrings.add( buildingGroupChars );
							buildingGroupChars = "";

						};

					};

				};

				buildingGroupChars = buildingGroupChars ++ char;

			};




		};


		lastChar = char;

	});

	groupStrings

};


var extractOperators = {|input|


	var operators = [$p,$f,$x,$:,$*];

	var foundOperators = List.new;

	operators.collect({|operator,index|

		var operatorIndex = input.find(operator);

		if( operatorIndex.notNil ) {

			foundOperators.add( operator );

		};

	});

	foundOperators;

};



var getOperatorRepetitions = {|string,char|

	var indexes = string.findAll( char );

	var operatorValue = 0;

	if(areIndexesSequential.value(indexes)){
		operatorValue = indexes.size;
	};

	operatorValue
};

var areIndexesSequential = {|indexes|

	var areSequential=true;

	var lastIndex = indexes[0];

	indexes.collect({|i|
		if( (i.asInteger - lastIndex.asInteger) > 1, {
			areSequential=false;
		}, {
			lastIndex = i;
		});
	});

	if( areSequential == false, {
		"invalid Pattern: x's must be sequential".postln;
	});

	areSequential;

};


var groups = parseSeqString.value("0xxxx:0.25*3     :0.25   :0.125   3ppp 1fff   2 3");


groups.postln;

groups.collect({|group|

	var repeatableOperators = [$p,$f,$x];

	var parameters = IdentityDictionary.new;

	var operators = extractOperators.value( group );


	operators.collect({|operator|

		var operatorValue = "";

		var parameter = ();

		if( repeatableOperators.includes( operator ) ) {

			var repetitions = getOperatorRepetitions.value(group,operator);

			repetitions.do({
				operatorValue = operatorValue ++ operator;
			});


		};

		parameters[operator] = operatorValue;

	});

	parameters.postln;

});




/*


- Extract Value/Operator combinations
	- separate into groups
		- separated by single space
		- sequential spaces to rests
			- its total duration is the sum of spaces multiplied by optional duration operator

- Create event for each value, with optional parameters



*/
