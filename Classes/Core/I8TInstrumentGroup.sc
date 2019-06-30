InstrumentGroup : Event
{

	var <amp;
	var <>main;
	var <clock;
	var <baseClock;
	var <fx;
	var <>name;

	var <childrenStopped;

	*new {
		^super.new.init;
	}

	init {
		childrenStopped = IdentityDictionary.new;
	}

	play {

		childrenStopped.postln;
		this.collect({|item,key|
			if(( (childrenStopped[key] == true) || (childrenStopped[key].isNil == true)) ) {

				if( (item.isKindOf(I8TNode)) || (item.isKindOf(InstrumentGroup))) {
					item.play;
				};
			};
		});
	}
	stop {|key|

		if( key.isNil, {

			this.collect({|item|
				if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
					item.stop;
				};
			});

		}, {

			var item = this.at(key);

			item.stop;
			childrenStopped[key] = true;

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


			if( this.at(key).isNil, {


				super.put(key,something);

				if( main.notNil ) {
					main.updateMixerGroup( this );
				};


			}, {

				if( childrenStopped[key] == true ) {
					this.at[key].play;
					childrenStopped[key] = false;
				};

			});

			^super.at(key)

		};

		if( something.isNil ) {
			var item = super.at(key);
			^item
		};

		// if( something.isKindOf(InstrumentGroup) ) {
		//
		// 	["got group", key, something].postln;
		//
		// 	something.name = name ++ "_" ++ key;
		// 	something.main = main;
		//
		// 	^super.put(key,something);
		//
		// };
	}



}
