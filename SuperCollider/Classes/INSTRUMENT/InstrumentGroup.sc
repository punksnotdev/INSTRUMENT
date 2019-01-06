InstrumentGroup : List {

	var <amp;
	var <speed;
	var <fx;
	var <>name;

	play {
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.play;
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.play;
					});
				});

			});
		});
	}
	stop {
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.stop;
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.stop;
					});
				});

			});
		});
	}
	go {|time|
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.go(time);
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.go(time);
					});
				});

			});
		});
	}
	set {|parameter,value_|

		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.set(parameter,value_)
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.set(parameter,value_)
					});
				});

			});
		});

	}

	amp_ {|value_|
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.amp = value_;
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.amp = value_ * item.amp;
					});
				});

			});
		});
		amp = value_;
	}



	speed_ {|value_|
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.speed=value_;
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.speed=value_ * item.speed;
					});
				});

			});
		});
		speed = value_;

	}

	// speed {|value_|
	//
	//
	// 	^speed
	//
	// }

	fx_ {|value_|
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.fx=value_;
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.fx=value_;
					});
				});

			});
		});
		fx = value_;
	}


	fxSet{|parameter_,value_|
		this.collect({|item|
			if( item.isKindOf(Instrument),{
				item.fxSet(parameter_,value_);
			}, {

				if( item.isKindOf(InstrumentGroup),{
					item.collect({|subItem|
						subItem.fxSet(parameter_,value_);
					});
				});

			});
		});
	}


}
