(

	name: 'testSpec',
	protocol: 'MIDI',

	inputs: (
		group1: (
			type: 'note',
			channel: 0,
			controllers: (
				0: 10,
				1: 11,
				2: 12,
			)
		),
		group2: (
			type: 'cc',
			channel: 1,
			controllers: (
				0: 30,
				1: 31,
				2: 32,
			)
		)
	),

	outputs: (
		group1: (
			type: 'note',
			channel: 2,
			controllers: (
				0: 50,
				1: 51,
				2: 52,
			)
		),
		group2: (
			type: 'cc',
			channel: 3,
			controllers: (
				0: 70,
				1: 71,
				2: 72,
			)
		)
	)

)
