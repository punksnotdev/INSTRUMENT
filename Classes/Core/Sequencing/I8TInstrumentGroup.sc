InstrumentGroup : Sequenceable
{

	var <amp;
	var <octave;
	var <clock;
	var <baseClock;
	var fx;

	var <childrenStopped;

	var <dictionary;

	*new {
		^super.new.init;
	}

	init {
		dictionary = ();
		childrenStopped = IdentityDictionary.new;
	}


	play {

		this.collect({|item,key|
			if(( (childrenStopped[key] == true) || (childrenStopped[key].isNil == true)) ) {

				if(
					(item.isKindOf(I8TNode)) || (item.isKindOf(InstrumentGroup))) {
					item.play;
				};
			};
		});

	}


	stop {|key|

		if( key.isNil, {

			this.collect({|item|
				if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
					item.stop;
				};
			});

		}, {

			var item = dictionary.at(key);

			item.stop;
			childrenStopped[key] = true;

		});
	}

	pause {|key|

		if( key.isNil, {

			this.collect({|item|
				if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
					item.pause;
				};
			});

		}, {

			var item = dictionary.at(key);

			item.pause;
			childrenStopped[key] = true;

		});
	}

	go {|time|
		this.collect({|item|
			if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
				item.go(time);
			};
		});
	}
	set {|parameter,value_|

		this.collect({|item|
			if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
				item.set(parameter,value_);
			};
		});

	}

	amp_ {|value_|

		this.collect({|item|
			if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
				item.amp = value_;
			};
		});
		amp = value_;
	}

	octave_ {|value_|

		this.collect({|item|
			if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
				item.octave = value_;
			};
		});
		octave = value_;
	}


	setClock {|speed_|
		if( speed_.isKindOf(Number) ) {
			if( speed_>0 && speed_ < 256 ) {
				var newClock = speed_;
				if( baseClock.notNil) {
					newClock = speed_ * baseClock;
				};
				this.collect({|item|

					if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
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
					if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
						item.setClock(speed_);
					};
				});
			}

		}


	}

	seq {|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
				item.seq(parameter_,value_);
			};
		});
	}

	rm {|parameter_,value_|
		this.collect({|item|
			if( (item.isKindOf(I8TInstrument)) || (item.isKindOf(InstrumentGroup))) {
				item.rm(parameter_,value_);
			};
		});
	}

	fx_ {|something|
		this.put('fx', something);
	}

	put {|key,something|


		if( key.asSymbol == 'fx' ) {
			^this.fx.channel.setFxChain(something);
		};

		if( something.isKindOf(I8TNode), {


			if( dictionary.at(key).isNil, {


				dictionary.put(key,something);


				if( graph.notNil ) {
					graph.updateMixerGroup( this );
				};


			}, {

				dictionary.at(key).setContent(something);

				if( childrenStopped[key] == true ) {
					dictionary.at[key].play;
					childrenStopped[key] = false;
				};

			});

			^dictionary.at(key)

		}, {

			if(something.isKindOf(SynthDef)) {
				dictionary.at(key).synthdef = something;
			};

			if(something.isKindOf(Symbol)||something.isKindOf(Symbol)) {
				if( graph.synths[something.asSymbol].notNil ) {
					dictionary.at(key).synthdef=graph.synths[something.asSymbol];
				};
			};

		});

		if( something.isNil ) {
			var item = dictionary.at(key);
			^item
		};

		// if( something.isKindOf(InstrumentGroup) ) {
		//
		//
		// 	something.name = name ++ "_" ++ key;
		// 	something.graph = graph;
		//
		// 	^super.put(key,something);
		//
		// };
	}



	at {|key|

		if(key.isNumber) {

			^super.at(key);

		};

		^dictionary.at(key);

	}


	collect {
		arg function;
		^dictionary.collect({|v,k|function.value(v,k)});
	}

	includes {
		arg value;
		^dictionary.includes(value);
	}

	keysValuesDo {|func|
		if( func.isKindOf(Function), {
			^dictionary.keysValuesDo({|k,v|func.value(k,v)});
		}, {
			"I8TNodeInstrumentGroup: keysValuesDo input should be function".warn;
		});
	}

	keys {
		^dictionary.keys
	}



    doesNotUnderstand {

        arg key ... args;

		var value = args[0];

		var instrument;

		if( key.isSetter, {

			if( key.asGetter.asSymbol == 'fx' ) {

				this.fx.channel.setFxChain(value);

			};

			instrument = dictionary.at(key.asGetter);

			if( instrument.isNil ) {
				if( value.notNil, {
					^instrument = this.put(key.asGetter,value);
				});
			};

			if( instrument.isKindOf(I8TChannel) ) {
				if(value.isNil, {
					instrument.kill;
					^nil
				}, {
					^instrument.setFxChain(value);
				});
			};

		}, {

			^dictionary.at(key);

		});

		^nil

    }




	chooseInstrument {|probability=0.5|

		var newGroup=();

		this.collect({|v,k|
			if( 1.0.rand > (1-probability.asFloat) ) {
				newGroup[k]=v
			};
		});

		if((newGroup.keys.size==0) && (probability.asFloat > 0)) {
			var key = this.keys.choose;
			newGroup[key]=this[key]
		};


		graph.addMixerGroup( newGroup, name );

	}





	trigger {|parameter,value|


		if( parameter.notNil && value.notNil, {
			switch(parameter,
				\go, {
					this.go(value.val.asInteger);
				},
				\amp, {
					this.amp_(value.val.asFloat);
				},
				\octave, {
					this.octave_(value.val.asInteger);
				},
				\clock, {
					this.clock_(value.val.asFloat);
				},
				\fx, {
					this.fx_(value.val.asSymbol);
				},
				\fxSet, {
					this.fxSet(value.val.asFloat);
				}, {

					this.collect({|instrument|
						instrument.set(parameter,value.val.asFloat);
					});

				}
			);
		});
	}





	fx {|key|
		var channel;
		var fx;

		channel = graph.mixer.getChannel( name );

		if( channel.isKindOf(IdentityDictionary) ) {

			channel = channel['group'];

			if( key.isNil ) {
				^channel.fx;
			};

			fx = channel.fx.at(key);

			if( fx.isKindOf(Synth) ) {
				^fx
			}

		}
	}


	// fx_ {|fx|

	// 	dictionary.collect({|i| i.fx = fx });

	// }



}
