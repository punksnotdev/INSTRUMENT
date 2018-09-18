ControllerManager {



	var controllers;
	var instruments;
	var controllerNames;
	var <>targets;

	var controlTargetMap;

	var < midi;

	var instrument;

	*new {|instrument_|
		^super.new.init(instrument_);
	}

	init {|instrument_|

		instrument = instrument_;

		controllers = IdentityDictionary.new;
		controllerNames = List.new;
		instruments = List.new;
		targets = List.new;

		controlTargetMap = IdentityDictionary.new;




	}


	set {|source, param1, param2 |

		var min, max;
		var outRange, minOutVal;
		var normalizedValue;
		var outValue;


		var controller = controlTargetMap[source.key.asSymbol];

		if(controller.notNil, {

			var target = controller.target;
			var key = controller.key;
			var parameter = controller.parameter;
			var range = controller.range;
			var protocol = controller.protocol;

			var type = controller.controller.type;


			switch( type,
				\cc, {

					var value = param1 / 127;
					if( range.notNil, {

						outRange = (range[1] - range[0]).abs;

						minOutVal = range[0];

						outValue = minOutVal + outRange * value;

					}, {

						outValue = value;

					});

					target.set(
						parameter,
						outValue
					);

				},

				\note, {
					target.set(\note,(val:param1, amplitude: param2/127));
				}

			);


		});

	}

	map {|controller,target,parameter,range|

		var mappingAlreadySetKey = nil;

		controlTargetMap.collect({| item, key|
			if( ( item.target == target && item.parameter == parameter ), {
				[item.target,item.parameter].postln;
				mappingAlreadySetKey = key;
			});
		});

		if( mappingAlreadySetKey.notNil, {
			"mapping already set: replace".postln;
			controlTargetMap.removeAt(mappingAlreadySetKey);
		});

		controlTargetMap[ controller.key ] = (
			controller: controller,
			target: target,
			parameter: parameter,
			range: range,
			key: controller.key,
			protocol: controller.protocol,
		);

		^controlTargetMap[ controller.key ];

	}


	mapDef {|controller,target|

		var newSource;

		switch( controller.protocol,
			"midi", {
				newSource = MIDIController(
					this,
					controller.type,
					controller.controllerId,
					controller.channel,
				);
				newSource.name = controller.name;
			}
		);

		controllers[controller.name] = (
			target: target,
			key: controller.parameter,
			range: controller.range,
			type: controller.type,
			protocol: controller.protocol,
		);

		controllerNames.add( controller.name );

	}

	addTarget {|target|

		targets.add( target );

	}


	addInstrument {|instrument|

		var ctlName;
		var index = instruments.size;
		instruments.add( instrument );

		if(controllerNames[ index ].notNil, {

			ctlName = controllerNames[ index ];

			controllers[ctlName].target.target = instrument;

		});

	}


	// mapController {|id_,param_,range_|
	//
	// 	controllerManager_
	// 	type_
	// 	controllerId_
	// 	channel_
	// 	sourceId_
	//
 	// 	MIDIController(controllerManager_, type_, controllerId_, channel_, sourceId_);
	//
	//
	//
	// }

	midi_ {|on=false|

		if( on, {

			var srcNames = List.new;

			midi = MIDIManager(this);

			Tdef(\initMidi, { 1.do{
			MIDIClient.init();

			3.wait;

			MIDIClient.sources.collect({|src,i|
				srcNames.add( src.device.asSymbol );
			});

			if( instrument.gui.notNil, {

				var callback = {|id|
					midi.postln;
					midi.addDevice( midi, MIDIClient.sources[id] );
				};

				instrument.gui.setMIDIDevices(
					srcNames.asArray, callback
				);

			});

		 	}}).play;

		}, {});

		^midi
	}


}
