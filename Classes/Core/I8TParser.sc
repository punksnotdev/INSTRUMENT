I8TParser {
/*

- [ ] implementar reset
- [ ] implementar grupos

*/


	*new {

		^super.new.init();

	}

	init {
	}

	*parse {|input|

		var groupStrings = List.new;
		var lastChar;

		var buildingGroupChars = "";

		var index = 0;

		var parameterGroups;

		var subsequences;

		// if contains opening and closing brackets
		if( this.validateMatching(input), {
			subsequences = this.getSubsequences(input);
		}, {
			subsequences =  [input];
		});

		subsequences.do({|subsequence|

			subsequence.size.do({|index|

				var char = input[ index ];


				// if is not last character
				if( index < (input.size - 1) ) {


					// if current char is not a space
					// append to current building group

					if( char != Char.space ) {

						if( lastChar == Char.space ) {

							// if( char != $:, {
								// if last char wast space and current isn't,
								// then last group is closed and we start a a new one
								if( buildingGroupChars.size > 0 ) {

									groupStrings.add( buildingGroupChars );
									buildingGroupChars = "";

								};

							// }, {
							//
							// 	if( buildingGroupChars.size > 0 ) {
							//
							// 		groupStrings.add( buildingGroupChars );
							// 		buildingGroupChars = "";
							//
							// 	};
							//
							// });

						};

						buildingGroupChars = buildingGroupChars ++ char;



					};

					// if current char is space

					if( char == Char.space ) {

						// if last char is also space

						if( lastChar.notNil ) {

							if( lastChar == Char.space ) {
								// start new group with a space
								buildingGroupChars = buildingGroupChars ++ Char.space;//char;

								// if( input.findBackwards( Char.space ).notNil ) {

									// if this is last space in string
									// if( input.findBackwards( Char.space ) <= index ) {
									// 	buildingGroupChars = buildingGroupChars ++ Char.space;
									// }
								// }

							};

							// if current char is not a space
							// create group from to current building chars

							if( lastChar != Char.space ) {

								groupStrings.add( buildingGroupChars );
								buildingGroupChars = "";

							}

						};

						if( lastChar.isNil, {

							buildingGroupChars = buildingGroupChars ++ char;

						});

					};

				};


				// if is last character

				if( index >= (input.size - 1) ) {



					if( char != Char.space ) {

						if( lastChar == Char.space ) {

							if( buildingGroupChars.compare("")!=0) {

								groupStrings.add( buildingGroupChars );

							};

							buildingGroupChars = "";

						};

					};

					if( char == Char.space ) {


						if( lastChar != Char.space ) {

							groupStrings.add( buildingGroupChars );

							buildingGroupChars = "";

						};

						if( lastChar == Char.space ) {
							buildingGroupChars = buildingGroupChars ++ Char.space;
						}

					};


					buildingGroupChars = buildingGroupChars ++ char;

					groupStrings.add( buildingGroupChars );

					buildingGroupChars = "";

				};



				lastChar = char;


			});

		});


		parameterGroups = groupStrings.collect({|groupString|
			this.extractParameters(groupString)
		});


		^this.getEventsList(parameterGroups);

	}


	*extractOperators {|input|


		var operators = [Char.space,$p,$f,$x,$:,$*];

		var foundOperators = List.new;

		operators.collect({|operator,index|

			var operatorIndex = input.find(operator);

			if( operatorIndex.notNil ) {

				foundOperators.add( operator );

			};

		});

		^foundOperators;

	}



	*extractParameters {|group|


		var repeatableOperators = [Char.space,$p,$f,$x];

		var parameters = IdentityDictionary.new;

		var operators = this.extractOperators( group );

		var operatorIndexes = SortedList.new;

		var groupValue;



		if( operators.size > 0 ) {

			operators.collect({|operator|
				var foundIndex = group.find( operator );
				if( foundIndex.notNil ) {
					operatorIndexes.add( foundIndex );
				};
			});

			if( operatorIndexes.size > 0) {


				if( operatorIndexes[0] > 0, {
					groupValue = group.split( group.at(operatorIndexes[0].asInteger) )[0];
				}, {
					groupValue = nil;
				});
			};

			operators.collect({|operator|

				var operatorValue = "";

				var parameter = ();

				var nextOperatorIndex;

				var charsToRead = 0;

				var foundIndex;

				var repetitions = 0;

				var currentIndex = group.find( operator );

				var operatorParameter;

				if( repeatableOperators.includes( operator ) ) {

					repetitions = this.getOperatorRepetitions(group,operator);

				};


				// if 1 repetition, read value after operator:
				if( repetitions<=1 ) {

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

						nextOperatorIndex = currentIndex;

					};


					if( nextOperatorIndex != currentIndex, {

						charsToRead = nextOperatorIndex - group.find(operator) - 1;

					}, {

						// if nextOperatorIndex is nil :

						charsToRead = group.size - nextOperatorIndex - 1;

					});

					if( charsToRead > 0, {
						charsToRead.do({|index|

							if ( index + currentIndex < group.size ) {

								operatorParameter = operatorParameter ++ group.at( index + 1 + currentIndex );

							}


						});

					}, {

						operatorParameter = 1;

					});


				};


				if( (operatorParameter.notNil) && (repetitions <= 1), {

					if(operatorParameter.asString.contains("/")){
						var lh, rh;
						lh = operatorParameter.split($/)[0];
						rh = operatorParameter.split($/)[1];
						operatorParameter = (lh.asFloat / rh.asFloat);
					};
					operatorValue = operatorParameter.asString;
				},
				{
					if( repetitions > 1 ) {
						operatorValue = repetitions.asString;
					};

				});


				parameters[operator] = operatorValue;

				if( groupValue.notNil, {

					parameters['val'] = groupValue;

				});



			});

		};

		if( operators.size <= 0 ) {

			parameters['val']=group;

		}


		^parameters;

	}



	*getOperatorRepetitions {|string,char|

		var indexes = string.findAll( char );

		var operatorValue = 0;

		if(this.areIndexesSequential(indexes)){
			operatorValue = indexes.size;
		};

		^operatorValue
	}

	*areIndexesSequential {|indexes|

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


		^areSequential;

	}



	*getEventsList{|parameterGroups|

		var events = List.new;
		var eventsPost = List.new;

		var nextEventDuration;
		var nextEventRepetitions;
		var nextEventAmp;

		var ampRange = [4,4];
		var pianoValue;
		var forteValue;

		parameterGroups.collect({|parameterGroup|

			var event = ();



			parameterGroup.keysValuesDo({|k,v|

				var hasValue = false;


				switch( k,

					Char.space, {

						event.val= \r;
						event.repetitions = v;

					},

					'val', {
						event.val= v;

					},
					$:, {

						event.duration = v;

					},
					$*, {

						event.amplitude = v;

					},

					$x, {

						event.repetitions = v;

					},
					"x", {
						event.repetitions = v;

					},

					$p, {
						event.piano = v;
					},
					$f, {
						event.forte = v;
					},
					// {
					//
					// 	event.val = v;
					//
					// }

				);



			});

			if( event.duration.notNil, {
				if( nextEventDuration.notNil ) {
					if( event.val.isNil, {
						nextEventDuration = event.duration;
					}, {
						event.duration = event.duration.asFloat * nextEventDuration.asFloat;
					});
				}
			},{
				if( nextEventDuration.notNil, {
					event.duration = nextEventDuration;
				}, {
					event.duration = 1;
				});

			});

			if( event.val.isNil ) {

				if( event.duration.notNil, {
					nextEventDuration = event.duration;
				});
				// if( event.repetitions.notNil, {
				// 	nextEventRepetitions = event.repetitions;
				// });
				// if( event.amp.notNil, {
				// 	nextEventAmp = event.amp;
				// });

			};



			// if( event.repetitions.notNil, {
			// 	event.repetitions = event.repetitions * nextEventRepetitions;
			// },{
			// 	event.repetitions = nextEventRepetitions;
			// });
			// if( event.amp.notNil, {
			// 	event.amp = event.amp * nextEventAmp;
			// },{
			// 	event.amp = nextEventAmp;
			// });

			if( event.val.notNil ) {
				events.add(event)
			};

		});

		pianoValue = events.collect(_.piano).reject(_.isNil).maxItem;
		forteValue = events.collect(_.forte).reject(_.isNil).maxItem;

		if( ampRange.notNil ) {

			if( pianoValue.notNil ) {
				ampRange[0]=pianoValue.asString;
			};
			if( forteValue.notNil ) {
				ampRange[1]=forteValue.asString;
			};

		};


		if( ((ampRange[0]<4) && (ampRange[1]<4))) {
			ampRange = nil;
		};

		events.collect({|event,i|

			var repetitions;
			var amplitude, piano, forte;

			if( event.repetitions.notNil, {

				repetitions = event.repetitions.asInteger;

				if( repetitions>0 ) {

					repetitions.asInteger.do({
						var newEvent = event;

						newEvent.repetitions = nil;
						eventsPost.add( newEvent );
					});

				}
			}, {
				eventsPost.add( event );
			});

			if( ampRange.notNil ) {


				amplitude = event.amplitude;
				piano = event.piano;
				forte = event.forte;


				amplitude = 0.5;

				if( piano.notNil ) {
					amplitude = (0.5-((piano.asString.asInteger / ampRange[0].asString.asInteger) / 2.5));
					amplitude = (0.5-((piano.asString.asInteger / ampRange[0].asString.asInteger) / 2.5));
				};
				if( forte.notNil ) {
					amplitude = (0.5+((forte.asString.asInteger / ampRange[1].asString.asInteger) / 2.5 ));
				};

				if( amplitude.isNumber ) {
					event.amplitude = amplitude.abs;
				};

			};


		});

		^eventsPost;

	}


	// TODO: implement nested subsequence checking

	validateMatching{|input,openingSymbol=$(,closingSymbol=$)|


			var opening=input.findAll(openingSymbol);
			var closing=input.findAll(closingSymbol);

			var correct = true;

			if(opening.size===closing.size, {

				opening.size.do({|index|

					if( opening[index] > closing[index] ) {
						correct = false;
						"must open ( before closing )".warn;
					};
					if( index < (opening.size - 1) ) {
						if( opening[index+1] < closing[index] ) {
							correct = false;
							"must close ) before opening (".warn;
						};
					};
				});


			}, {
				"number of ( and ) not matching".warn;
			});

			^correct;

	}

	getSubsequences {|input|

		var subsequences = List.new;

		var opening = input.findAll($();
		var closing = input.findAll($));

		if( this.validateMatching(input) ) {
			opening.size.do({|index|
				var subsequence = input.copyRange(opening[index]+1,closing[index]-1);
				// var total = (closing[index] - opening[index])-1;
				var checkEnd = input.copyToEnd(closing[index]+1);
				var operators = checkEnd.copyFromStart(checkEnd.find(Char.space));

				subsequences.add((
					pattern: subsequence,
					operators: operators
				));
			});
		};

		^subsequences;

	}


	getSubsequenceEvents {|input|

		if( input.find( $( ).notNil ) {
			var splitString = input.split($();
			var rightHand = splitString[1];

			var content;


			if( rightHand.find( $) ).notNil ) {

				var splitRightHand = rightHand.split($));
				var operatorString = splitRightHand[1];
				var operators = I8TParser.extractParameters(operatorString);
				var events;

				content = splitRightHand[0];

				events = I8TParser.parse(content);

				if( operators.isKindOf(IdentityDictionary) ) {

					if( operators[$:].notNil ) {

						var duration = operators[$:].asFloat;
						var durations = events.collect({|event|
							event.duration.asFloat * duration.asFloat;
						});
						events.do({|event,index| event.duration = durations[index]})
					};

					if( operators[$x].notNil ) {
						var repetitions = operators[$x].asInteger;
						if((repetitions-1)>0) {
							(repetitions-1).do({
								events = events ++ events;
							});
						}
					};

					// TODO: implement other operators
				};

				^events;

			}
		}

	}

}
