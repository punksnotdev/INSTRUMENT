CountDown {

	var minutes;
	var pollInterval;
	var name;

	*new {|minutes_,pollInterval_,name_|
		^super.new.init(minutes_,pollInterval_,name_);
	}

	init {|minutes_=1,pollInterval_=20,name_|
		minutes=minutes_;
		pollInterval=pollInterval_;
		name=name_;
	}

	play {

		var seconds = (minutes*60).floor.asInteger;


		var t = (

			clock: TempoClock.new,

			pad: {|event,number|

				if(number.asString.size<2) {
					number = "0"++number.asString;
				};

				number

			}

		);


		Tdef(\timer,{

			(seconds+1).do{|i|

				var secondsLeft = (seconds-i);
				if( secondsLeft%pollInterval==0 ) {
					[
						name
						++": "++

						t.pad((secondsLeft/60).floor.asInteger)
						++":"++
						t.pad( (secondsLeft%60).floor.asInteger)
					].postln;
				};
				1.wait;
			}
		});


		Tdef(\timer).play(t.clock);

		(
			"Go: "
			++
			t.pad( seconds.floor.asInteger )
			++":"++
			t.pad( seconds.floor.asInteger )
		).postln;
	}
}
