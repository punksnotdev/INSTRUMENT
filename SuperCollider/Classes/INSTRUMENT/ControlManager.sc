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


	set {|source, value|
		var min, max;
		var outRange, minOutVal;
		var normalizedValue;
		var outValue;

		var target = controllers[source.name].target;
		var key = controllers[source.name].key;
		var range = controllers[source.name].range;
		var protocol = controllers[source.name].protocol;

		switch( protocol,
			"midi", {

				min = 0; max = 127;

				normalizedValue = (value / 127).asFloat;

				outRange = (range[1] - range[0]).abs;

				minOutVal = range[0];

				outValue = minOutVal + outRange*normalizedValue;

			}
		);

		["set:",target,key,outValue].postln;

		target.set(
			key,
			outValue
		);

	}

	map {|controller,target|

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



		// newSource.ç
		// newSource.ç
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

			midi = MIDIManager();

			Tdef(\initMidi, { 1.do{
			MIDIClient.init();

			3.wait;

			MIDIClient.sources.collect({|src,i|
				srcNames.add( src.device.asSymbol );
			});

			if( instrument.gui.notNil, {

				var callback = {|id|
					midi.postln;
					midi.addDevice( MIDIClient.sources[id] );
				};

				instrument.gui.setMIDIDevices(
					srcNames.asArray, callback
				);

			});

		 	}}).play;

		}, {});

		^midi
	}


	initializeMIDI {

		MIDIIn.connect(0,MIDIClient.sources[4]);


	}

}
