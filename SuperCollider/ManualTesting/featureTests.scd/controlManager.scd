MIDIIn.connectAll;

(

i = INSTRUMENT();

c = (
	controllers: [
		(
			protocol: "midi",
			sourceId: 0,
			name: "test",
			type: \cc,
			parameter: \rv2
		)
	]
);


i.mapController(c);

)
