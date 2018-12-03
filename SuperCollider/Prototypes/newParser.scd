var parseSeqString = {|input|

	var groupStrings = List.new;
	var lastChar;

	var buildingGroupChars = "";

	var index = 0;


	// input.collect({|char|
	// 	char.postln;
	// });
	input.size.do({|index|

		var char = input[ index ];


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
// ("last --- not a space: "++lastChar).postln;
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


var extractParameters = {|group|

	var repeatableOperators = [$p,$f,$x];

	var parameters = IdentityDictionary.new;

	var operators = extractOperators.value( group );

	var operatorIndexes = SortedList.new;

	operators.collect({|operator|
		var foundIndex = group.find( operator );
		if( foundIndex.notNil ) {
			operatorIndexes.add( foundIndex );
		};
	});

	operators.collect({|operator|

		var operatorValue = "";

		var parameter = ();

		var nextOperatorIndex;

		var charsToRead = 0;

		var foundIndex;

		var repetitions = 0;

		var currentIndex = group.find( operator );

		if( repeatableOperators.includes( operator ) ) {

			repetitions = getOperatorRepetitions.value(group,operator);

			repetitions.do({
				operatorValue = operatorValue ++ operator;
			});


		};
		// if no repetitions, read value after operator:
		if(repetitions==0) {

			operatorIndexes.collect({|operatorIndex|

				if( nextOperatorIndex.isNil ) {

					var currentOperatorIndex = group.find( operator );


					if( currentOperatorIndex != operatorIndex ) {
						if( operatorIndex > currentOperatorIndex ) {
							nextOperatorIndex = operatorIndex;
						}
					};


				}

			});

			if( nextOperatorIndex.isNil ) {

				nextOperatorIndex =  currentIndex;

			};


			if( nextOperatorIndex != currentIndex, {
				charsToRead = nextOperatorIndex - group.find(operator) - 1;

			}, {

				// if nextOperatorIndex is nil :

				charsToRead = group.size - nextOperatorIndex - 1;


			});

		};


		charsToRead.do({|index|

			if ( index + currentIndex < group.size ) {

				operatorValue = operatorValue ++ group.at( index + 1 + currentIndex );

			}


		});



		parameters[operator] = operatorValue;

	});

	parameters;

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


// var groups = parseSeqString.value("0xxxx:0.25*3");
var groups = parseSeqString.value("0xxxx:0.25*3     :0.5   :0.125   3ppp 1fffxxx:0.0625   2 3");


var parameters = groups.collect({|group|
	extractParameters.value( group );
});

Tdef(\printall,{

	parameters.size.do({|index|
		"".postln;
		index.postln;
		parameters[index].postln;
		0.005.wait;
	});

}).play;





"----".postln;
"done".postln;

""

/*


- Extract Value/Operator combinations
	- separate into groups
		- separated by single space
		- sequential spaces to rests
			- its total duration is the sum of spaces multiplied by optional duration operator

- Create event for each value, with optional parameters



*/
