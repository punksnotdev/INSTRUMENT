(
	e=Environment[

		'tempo' -> 192

	];

	e.know=true;

	s.options.memSize=2048*1024;
	s.options.maxNodes=128*1024;
	// (thisProcess.nowExecutingPath.dirname ++ "/tools/setup/variables.scd").load;
	s.volume=(-12);

	s.boot;
	s.doWhenBooted({

		(thisProcess.nowExecutingPath.dirname ++ "/tools/setup/setup.scd").load;

	})

)
