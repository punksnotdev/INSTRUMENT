
i=INSTRUMENT();

// each array gets repeated 4 times, per the default settings

m = [
	[ 0, 2, 3, 7 ],
	[ 5, 2, 3, 12 ],
	[ 10, 2, 3, 2  ]
];


i.lead = i.synths.noise[2];

i.lead.clock=19;

i.lead.seq(\note, m );

i.lead.seq(\note, m ).speed(4);





// reusing data:

m = [ 31, 30, 24, 23, 23, 15, 12, 11, 11, 3, 2, 0 ];

d = i.data;

d.melodies = [
	[m, 2],
	[([]++m).put(1,33), 1],
	[([]++m).put(11,-12), 1],
];

i.lead = i.synths.noise[2];

i.lead.clock=19;

i.lead.seq(\note, d.melodies );
