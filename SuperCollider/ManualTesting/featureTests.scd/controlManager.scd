(

s.boot;

s.doWhenBooted({

Tdef(\boot, {

	1.do {

		MIDIIn.connectAll;

		4.wait;

		i = INSTRUMENT();

		c = (
			controllers: [
				(
					protocol: "midi",
					sourceId: 0,
					name: "test",
					type: \cc,
					parameter: \rel,
					range: [0,3]
				)
			]
		);


		i.mapController(c);



		i.instrument[\k] = SynthPlayer(\k,\tranceBazz);

		i.instrument[\k].seq(\note,0,"0 3 7",[\speed,4]);


	}
	}).play;

});

)


i.instrument[\k].set(\sus,2);


i.instrument[\k2] = SynthPlayer(\k2,\tranceBazz);
i.instrument[\k2].seq(\note,0,"0 3 7",[\speed,4]);
