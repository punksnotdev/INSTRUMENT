InstrumentGroup : List {

	var amp;
	var speed;
	var fx;

	play {
		this.collect({|item|
			item.play;
		});
	}
	stop {
		this.collect({|item|
			item.stop;
		});
	}
	go {|time|
		this.collect({|item|
			item.go(time);
		});
	}
	set {|parameter,value_|

		this.collect({|item|
			item.set(parameter,value_)
		});

	}
	
	amp_ {|value_|
		this.amp( value_ );
	}

	amp {|value_|

		this.collect({|item|
			item.amp = value_;
		});

	}

	speed_ {|value_|
		this.speed( value_ );
	}

	speed {|value_|

		this.collect({|item|
			item.speed=value_;
		});

	}

	fx_ {|value_|
		this.fx( value_ );
	}

	fx {|value_|

		this.collect({|item|
			item.fx=value_;
		});

	}

	setFx{|parameter_,value_|
		this.collect({|item|
			item.setFx(parameter_,value_);
		});
	}


}
