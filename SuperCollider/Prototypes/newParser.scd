// var parseSeqString = {|input|
f = {|input|

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



f.value("1xxxx:0.25     :0.25   :0.125   32 1   2 3");




/*


- Extract Value/Operator combinations
	- separate into groups
		- separated by single space
		- sequential spaces to rests
			- its total duration is the sum of spaces multiplied by optional duration operator

- Create event for each value, with optional parameters



*/
