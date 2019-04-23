(
	s.options.memSize=2048*1024;
	s.options.maxNodes=128*1024;
	s.boot;
)

(thisProcess.nowExecutingPath.dirname ++ "/tools/setup/setup.scd").load;
