(

s.boot;

s.doWhenBooted({

"/home/mukkekunst/Musica/SuperCollider/INSTRUMENT/SuperCollider/ControllerMaps/midimix.scd".load;

Tdef(\boot, {

	1.do {

		MIDIIn.connectAll;

		3.wait;

		i = INSTRUMENT();
		i.midi_(true);
		i.setupGUI();

		c = (
			controllers: [
				(
					protocol: "midi",
					sourceId: 0,
					name: "test",
					type: \cc,
					parameter: \rel,
					range: [0,3],
					controllerId: b[(0*4)+3].control
				),
				(
					protocol: "midi",
					sourceId: 0,
					name: "test2",
					type: \cc,
					parameter: \rel,
					range: [0,3],
					controllerId: b[(1*4)+3].control
				),
				(
					protocol: "midi",
					sourceId: 0,
					name: "test3",
					type: \cc,
					parameter: \rel,
					range: [0,3],
					controllerId: b[(2*4)+3].control
				)
			]
		);


		i.mapController(c);



		i.instrument[\k] = SynthPlayer(\k,\dist);

		i.instrument[\k].seq(\note,0,"0 3 7",[\speed,4]);

		i.instrument[\k2] = SynthPlayer(\k2,\dist);
		i.instrument[\k2].seq(\note,0,"10 8 14 10 7 15",[\speed,1]);
		i.instrument[\k2].set(\rel,1);


		i.instrument[\k3] = SynthPlayer(\k3,\dist2);
		i.instrument[\k3].seq(\note,0,"10  0 3  2:1/2 3:1/2  13",[\speed,4]);
		i.instrument[\k3].set(\rel,0.2);


		i.instrument.collect({|inst|inst.octave=4});

	}
	}).play;

});

)


/* better controllr notation

c=ctllr
g=ctllr.addGroup( \cc, "name" )
g.addCtl(0,\rv1,[0,1])

map = [
	0 -> (
		parameter: \rv1,
		range: (
			min: 0,
			max: 1
		)
	)

	[ 0  \gain ],
	[ 0, \rv1 ],
	[ 0, \cutoff ],
]

		c = (
			controllers: [(

				protocol: "midi",
				sourceId: 0,
				name: "testCtl",
				controllerGroups: [
				(
					type: \cc,
					name: "testGrp"
					controllers: [(
						parameter: \rv1,
						range: (
							min: 0,
							max: 1
						)
					)]
				)
				]
			)]
		);

*/
