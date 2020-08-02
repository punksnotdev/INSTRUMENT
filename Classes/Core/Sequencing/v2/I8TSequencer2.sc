+ Sequencer {



	setTimeSignature {|beats, tick|
		timeSignature = (
			beats: beats,
			tick: tick
		);
	}

	initV2 {|main|

		timeSignature = (
			beats: 4,
			tick: 1/4
		);

		queue = List.new;

// 		tdef = Tdef(("seq_"++main_.threadID).asSymbol, {
// 			inf.do {|i|
// 				if((i%(timeSignature.beats/timeSignature.tick))==0) {
// 					t.collect({|tdef|
// 						if( tdef.isPlaying == false ) {
// 							tdef.play;
// 						};
// 					});
// 				};
// 				if((i%16)==0) {
// 					t.collect({|tdef| tdef.reset; });
// 				};
// 				timeSignature.tick.wait;
// 			};
// 		});


	}


	addToQueue {|action,q|

		if( action.isKindOf(Symbol) && q.isKindOf(Event) && q.item.isKindOf(SequencerTrack) ) {
			q.action = action;
			queue.add((
				action: action,
				item: q.item,
				data: q.data,
			))
		};

	}


	queueDo {|action|

		queue.select({|q|q.action==action}).do({
			var q = queue.removeAt(0);

			switch( q.action,
				\play, {
					var pos = 0;
					if(q.data.notNil) {
						if(q.data.position.notNil) {
								pos = q.data.position.asInteger;
						}
					};
					q.item.play(pos);
				},
				\stop, {
					q.item.stop();
				},
				\go, {
					q.item.go( q.data.position );
				},
			);
		});
	}


}
