ControllerManager {



	var controllers;
	var instruments;
	var controllerNames;
	var <>targets;

	var <controlTargetMap;

	var < midi;

	var instrument;

	*new {|instrument_|
		^super.new.init(instrument_);
	}

	init {|instrument_|

		instrument = instrument_;

		controllers = IdentityDictionary.new;
		controllerNames = IdentityDictionary.new;
		instruments = IdentityDictionary.new;
		targets = List.new;

		controlTargetMap = IdentityDictionary.new;




	}


	set {|source, param1, param2 |

		var min, max;
		var outRange, minOutVal;
		var normalizedValue;
		var outValue;


		var controllerList = controlTargetMap[source.key.asSymbol];
		var inputValue;
		var inputMap;
		var listener;


	 	inputValue = param1;


		listener = source.listener;
		if( listener.isKindOf(MIDIDevice) ) {
			var spec = listener.spec;
			[param1, param2, listener, spec].postln;
			if( spec.isKindOf(I8TControllerSpec) ) {
				["output:",source.key,spec.getOutputByCtlNum(param1)].postln;
			};
		};

		if( source.midiTarget.notNil,{

			var spec = source.midiTarget.spec;

	        if( spec.isKindOf(I8TControllerSpec), {
	            inputValue = spec.getInputByCtlNum(param1);
	        });

		});


		if( ( controllerList.isKindOf(Collection) && (controllerList.size > 0) ), {

			controllerList.collect({|controller|


				var target = controller.target;
				var key = controller.key;
				var parameter = controller.parameter;
				var range = controller.range;
				var protocol = controller.protocol;
				var type = controller.controller.type;

				switch( type,
					\cc, {

						var value = inputValue / 127;

						if( range.notNil, {

							outRange = (range[1] - range[0]).abs;

							minOutVal = range[0].min(range[1]);

							if( range[0] > range[1] , {

								outValue = minOutVal + (1 - (outRange * value) );

							}, {

								outValue = minOutVal + outRange * value;
							});

						}, {

							outValue = value;

						});

						target.set(
							parameter,
							outValue
						);

					},

					\note, {

						target.set(\note,(val: inputValue, amplitude: param2/127));

					}

				);


			});

		}, {

			["ControllerManager:", "no controller set", source, param1, param2].postln;

		});
	}


	map {|controller,target,parameter,range|

		var mapping = (
			controller: controller,
			target: target,
			parameter: parameter,
			range: range,
			key: controller.key,
			protocol: controller.protocol,
		);


		// var newKey = target.name ++ '-' ++ target.parameter;

		if( controlTargetMap[controller.key].isKindOf(List) == false, {

			controlTargetMap[controller.key] = List.new;

		}, {

			controlTargetMap[controller.key].collect({| item, index |
				// check if target + parameter mapping exists
				if( ( item.target == target && item.parameter == parameter ), {
					controlTargetMap[controller.key].removeAt( index );
				});

			});

		});


		controlTargetMap[controller.key].add( mapping );

		// [
		// 	"ControllerManager:",
		// 	"added mapping:",
		// 	"source:",mapping.key,
		// 	"target:",mapping.target,
		// 	"index:", controlTargetMap[controller.key].size - 1
		// ].postln;


		^mapping

	}


	unmap {|controller,target,parameter|


		if( controlTargetMap[controller.key].isKindOf(List) == true, {

			controlTargetMap[controller.key].collect({| item, index |

				// check if target + parameter mapping exists
				if( ( item.target == target && item.parameter == parameter ), {

					controlTargetMap[controller.key].removeAt( index );

					// [
					// 	"ControllerManager:",
					// 	"removed mapping:",
					// 	"source:",controller.key,
					// 	"target:",item.target,
					// 	"index:", index
					// ].postln;

				});

				if( target.isNil, {

					controlTargetMap[controller.key].removeAt( index );

				});


			});


		});

		^true


	}



	addTarget {|target|

		targets.add( target );

	}


	addInstrument {|instrument, key|

		var ctlName;


		// if( instruments[key].notNil, {
		// 	this.removeInstrument( instruments[key] );
		// });
		if( instrument.isKindOf(I8TNode), {

			instruments[key] = instrument;

			if(controllerNames[ key ].notNil, {

				ctlName = controllerNames[ key ];

				controllers[ctlName].target.target = instrument;

			});

		}, {
			// TODO: "I8TControllerManager: To Do: Implement Groups"
			"I8TControllerManager: To Do: Implement Groups"
		});

	}


	removeInstrument {|instrument|

		var ctlName;
		var index = instrument.name;

		if( index > - 1 ) {

			instruments.removeAt( index );

			if( controllerNames[ index ].notNil, {

				ctlName = controllerNames[ index ];

				controllers[ctlName].target.target = nil;

			});

		}


	}



	midi_ {|on=false|

		if( on, {

			var srcNames = List.new;

			midi = MIDIManager(this);

			Tdef(\initMidi, { 1.do{

			// MIDIClient.init(verbose:false);

			// 3.wait;

			MIDIClient.sources.collect({|src,i|
				srcNames.add( src.device.asSymbol );
			});


			midi.setupMIDIOut();

			// if( instrument.gui.notNil, {
			//
			// 	var callback = {|id|
			// 		midi.addDevice( midi, MIDIClient.sources[id] );
			// 	};
			//
			// 	instrument.gui.setMIDIDevices(
			// 		srcNames.asArray, callback
			// 	);
			//
			// });

		 	}}).play;

		}, {});

		^midi
	}


}
