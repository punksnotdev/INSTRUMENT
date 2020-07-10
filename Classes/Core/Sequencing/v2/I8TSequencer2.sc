+ Sequencer {



	signature_ {|number, count, length|

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


	queueDo {

		queue.size.do({
			var q = queue.removeAt(0);

			switch( q.action,
				\play, {
					q.item.play();
				},
				\go, {
					q.item.go( q.data.position );
				},
			);
		});
	}


}
