(
/*


- beat based seqs vs millisecond duration
- each track ~tracks its current beat
- beats > ms conversion


- limit each tracks total duration


*/
var bpmToMs;


~bpm = 166.0;
~beatsPerBar = 4;

bpmToMs = ((60/~bpm))*1000;


(thisProcess.nowExecutingPath.dirname ++ "/synthdefs.scd").load;
(thisProcess.nowExecutingPath.dirname ++ "/track-sequences.scd").load;


// ~playtracks = [~tracks[\bass]];
// ~playtracks = [~tracks[\noteSynth]];
~playtracks = ~tracks;
~playtracks.collect({|track|
	var bpmToMs =  ((60/~bpm))*1000;

	var lastItemBeat = ((track.sequenceKeys[track.sequence.size-1]));


	var bars = lastItemBeat/(track.beatsPerBar);
	bars = ceil(bars);
	// ["bars",lastItemBeat,bars].postln;


	// track.totalMs = lastItemBeat * bpmToMs;
	track.totalMs = (bars * track.beatsPerBar )* bpmToMs;
	track.msDuration = floor(track.totalMs / track.speed);

	// ["track.totalMs",track.totalMs].postln;
	// ["track.msDuration",track.msDuration].postln;

});



// ~playtracks = ~tracks;

Tdef(\sequencer, {

	inf.do{|i|


		~playtracks.collect({|track|

			var nextBeat;
			var nextMs;
			var counter;
			var isBeat;
			var isBar;
			var beatDuration;
			var barDuration;

			if( track.currentBeat.isNil,
				{
					nextBeat = 0;
				},
				{
					nextBeat = (track.currentBeat + 1);
				}
			);

			beatDuration = floor((( 1/track.speed * bpmToMs )));
			beatDuration = beatDuration*2;
			barDuration = beatDuration*track.beatsPerBar;

			isBeat = i%beatDuration;
			isBar = i%(barDuration);

			if(isBeat==0) {
				// [i/beatDuration,"beat"].postln;
			};

			if(isBar==0) {
				[barDuration,i/barDuration,"bar"].postln;
			};




			// counter = i % track.msDuration;
			counter = i % track.msDuration;

			nextBeat = nextBeat % track.sequence.size;
			// nextBeat.postln;

			nextMs = (track.sequenceKeys[ nextBeat ] * bpmToMs);
			nextMs = nextMs / track.speed;
			// nextMs = nextMs * track.beatsPerBar;
			// if(counter%125==0){[counter,track.msDuration,nextMs].postln;};

			if(
				(
					((nextBeat == 0) && (counter == 0) )
				||
				((nextBeat > 0) && (counter >= (nextMs-1)))
				)
				// &&
				// ((
				// 	nextBeat==0
				// 	&&
				// 	(isBar!=0)
				//
				// )==false)
			)
			{

				// if( nextBeat==0 )
				// get value
				var nextIndex = track.sequenceKeys[ nextBeat ];
				var event = track.sequence[nextIndex];
				// nextBeat.postln;
				// trigger
				Synth(track.synthdef,[\note,event.note,\amp,event.amp]);

				// (track.synthdef).postln;
				// (event).postln;
				// set track's currentBeat

				track.currentBeat = nextBeat;
			};

			// };





		});

		0.001.wait;

	}

}).play;

)
