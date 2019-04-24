(

	e=Environment[

		'tempo' -> 192,
		'readyMessage' -> '¡ L I S T O !'

	];

	e.know=true;

	s.options.memSize=2048*1024;
	s.options.maxNodes=128*1024;
	// (thisProcess.nowExecutingPath.dirname ++ "/tools/setup/variables.scd").load;

	s.boot;
	s.doWhenBooted({

		s.volume=(-3);
		().play;

		(thisProcess.nowExecutingPath.dirname ++ "/tools/setup/setup.scd").load;

	})

)
