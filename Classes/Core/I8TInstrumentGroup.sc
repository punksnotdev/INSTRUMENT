InstrumentGroup : Event
{

	var <amp;
	var <octave;
	var <>main;
	var <clock;
	var <baseClock;
	var fx;
	var <name;
	var <sequenceable;
	var <sequencer;

	var <childrenStopped;

	var dictionary;

	*new {
		^super.new.init;
	}

	init {
		sequenceable = Sequenceable.new;
		sequencer = sequenceable.sequencer;
		dictionary = ();
		childrenStopped = IdentityDictionary.new;
	}

	sequencer_ {|sequencer_|
		sequencer = sequencer_;
		sequenceable.sequencer = sequencer_;
	}

	play {

		sequenceable.play;

		this.collect({|item,key|
			if(( (childrenStopped[key] == true) || (childrenStopped[key].isNil == true)) ) {

				if( (item.isKindOf(I8TNode)) || (item.isKindOf(InstrumentGroup))) {
					item.play;
				};
			};
		});
	}


	stop {|key|

		sequenceable.stop;

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

	pause {|key|

		sequenceable.pause;

		if( key.isNil, {

			this.collect({|item|
				if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
					item.pause;
				};
			});

		}, {

			var item = this.at(key);

			item.pause;
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

	octave_ {|value_|

		this.collect({|item|
			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {
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


	fx {|key|

		var fxChains = I8TFXChain.new;

		this.collect({|item,itemKey|

			if( key.isNil, {

				item.fx.collect({|chainItem,cIKey|
					if( fxChains[cIKey].isNil) {
						fxChains[cIKey] = I8TFXChain.new;
					};

					fxChains[cIKey][itemKey]=chainItem;

				});

			}, {

				if( key.isKindOf(I8TFolder), {
					var synthdef = key.values.detect(_.isKindOf(SynthDef)).name.asSymbol;
					if( item.fx[synthdef].notNil ) {
						fxChains.put(itemKey,item.fx[synthdef]);
					};
				}, {
				// 	fxChains.put(itemKey,item.fx);

					if(
						(
							main.validateSynthDef(key)
							||
							main.validateSynthName(key)
						)
					,{

						var chainItem;
						var fxKey;


						if(( key.isKindOf(SynthDef)||key.isKindOf(SynthDefVariant)),{
							fxKey = key.name.asSymbol;
						}, {
							fxKey = key.asSymbol;
						});

						chainItem = item.fx[fxKey];



						if( fxChains[itemKey].isNil) {
							fxChains[itemKey] = I8TFXChain.new;
						};
						if(chainItem.notNil, {
							// if(chainItem.isKindOf(Collection), {
							// 	"isEvent".warn;
							// 	chainItem = chainItem.values.select(_.isKindOf(SynthDef));
							// 	if(chainItem.notNil) {
							//
							fxChains[itemKey][fxKey]=chainItem;
							//
							// 	};
							// }, {
							//
							// });
						}, {
							if( item.fx.isKindOf(Dictionary)) {

								var chainItem = item.fx[key];

								fxChains[itemKey][fxKey]=chainItem;
								["not found!", item.fx, key].postln;
							};
						});



					}, {

						if( key.isKindOf(Array) ) {

							key.collect({|listKey|

								var chainItem = item.fx[listKey];

								if(chainItem.notNil) {
									if( fxChains[itemKey].isNil) {
										fxChains[itemKey] = I8TFXChain.new;
									};
									fxChains[itemKey][listKey]=chainItem;
								};

							});

						};

					});

				});


			});
		});


		^fxChains;

	}
	fx_ {|value_|


		this.collect({|item|

			if( (item.isKindOf(Instrument)) || (item.isKindOf(InstrumentGroup))) {


				if(
					(
						value_.isKindOf(SynthDef)
						|| value_.isKindOf(Collection)
						|| value_.isKindOf(SynthDefVariant)
						|| value_.isKindOf(Symbol)
						|| value_.isKindOf(String)
						|| value_.isNil
						|| (value_===false)
					)
				) {
					item.fx = value_;
				};
			};
		});

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


	put {|key,something|

		if( something.isKindOf(I8TNode), {


			if( this.at(key).isNil, {


				dictionary.put(key,something);

				if( main.notNil ) {
					main.updateMixerGroup( this );
				};


			}, {

				this.at(key).setContent(something);

				if( childrenStopped[key] == true ) {
					this.at[key].play;
					childrenStopped[key] = false;
				};

			});

			^dictionary.at(key)

		}, {

			if(something.isKindOf(SynthDef)) {
				this.at(key).synthdef = something;
			};
			if(something.isKindOf(Symbol)||something.isKindOf(Symbol)) {
				if( sequenceable.graph.synths[something.asSymbol].notNil ) {
					this.at(key).synthdef=sequenceable.graph.synths[something.asSymbol];
				};
			};

		});

		if( something.isNil ) {
			var item = dictionary.at(key);
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

	at {|key|

		if(key.isNumber) {

			^sequenceable.at(key);

		};

		^dictionary.at(key);

	}


	keysValuesDo {|func|
		if( func.isKindOf(Function), {

			^dictionary.keysValuesDo(func)
		}, {
			"I8TNodeInstrumentGroup: keysValuesDo input should be function".warn;
		});
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


		main.addMixerGroup( newGroup, name );

	}

	name_ {|name_|
		name = name_;
		sequenceable.name = name;
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
}
