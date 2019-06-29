if( e['showScope'] ) {
	s.scope;
};

if( e['showMeter'] ) {
	s.meter;
};

if( e['showFreqscope'] ) {
	s.freqscope;
};

(thisProcess.nowExecutingPath.dirname ++ "/load-everything.scd").load;
