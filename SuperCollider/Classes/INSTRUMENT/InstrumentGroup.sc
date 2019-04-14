InstrumentGroup : Event
{

	var <amp;
	var <>main;
	var <clock;
	var <baseClock;
	var <fx;
	var <>name;


	play {
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.play;
			};
		});
	}
	stop {
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.stop;
			};
		});
	}
	go {|time|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.go(time);
			};
		});
	}
	set {|parameter,value_|

		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.set(parameter,value_);
			};
		});

	}

	amp_ {|value_|

		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.amp = value_;
			};
		});
		amp = value_;
	}


	setClock {|speed_|
		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {
				var newClock = speed_;
				if( baseClock.notNil) {
					newClock = speed_ * baseClock;
				};
				this.collect({|item|

					if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
						item.setClock(speed_);
					};

				});
			}
		}
	}

	clock_ {|speed_|

		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {
				baseClock = speed_;
				clock = speed_;
				this.collect({|item|
					if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
						item.setClock(speed_);
					};
				});
			}

		}



	}


	fx_ {|value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.fx=value_;
			};
		});
		fx = value_;
	}


	fxSet{|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.fxSet(parameter_,value_);
			};
		});
	}

	seq {|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.seq(parameter_,value_);
			};
		});
	}

	rm {|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
				item.rm(parameter_,value_);
			};
		});
	}


	put{|key,something|

		if( something.isKindOf(I8TNode) ) {

			["put",key,something].postln;
			if( main.notNil ) {
				main.addNodeToGroup( this, something );
			};

			super.put(key,something);

			^super.at(key)

		};

		if( something.isKindOf(InstrumentGroup) ) {

			something.name = name + "_" + key;
			something.main = main;

			^super.put(key,something);

		};
		if( something.isNil ) {
			var item = super.at(key).postln;
			["at",key,item].postln;
			^item
		};

	}

}
