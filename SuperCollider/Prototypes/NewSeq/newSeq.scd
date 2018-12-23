(
/*


- beat based seqs vs millisecond duration
- each track ~tracks its current beat
- beats > ms conversion


- limit each tracks total duration


*/
var bpmToMs;


~bpm = 144.0;

bpmToMs = ( ( 60.0 / ~bpm ) )*1000;


(thisProcess.nowExecutingPath.dirname ++ "/track-sequences.scd").load;


~tracks.collect({|track|
	track.totalMs =
	(floor(track.sequenceKeys[track.sequence.size-1])+1)*bpmToMs;
	track.msSpeed = (track.totalMs / track.speed);
});

~playtracks = ~tracks;

Tdef(\sequencer, {

	inf.do{|i|


		~playtracks.collect({|track|

			var nextBeat;
			var nextMs;
			var counter;


			if( track.currentBeat.isNil,
				{
					nextBeat = 0;
				},
				{
					nextBeat = (track.currentBeat + 1);
				}
			);






			counter = i % floor(track.msSpeed);

			nextBeat = nextBeat % track.sequence.size;

			nextMs = (track.sequenceKeys[ nextBeat ] * bpmToMs);
			nextMs = nextMs / track.speed;

			if(
				((nextBeat == 0) && (counter == 0) )
				||
				((nextBeat > 0) && (counter >= (nextMs-1)))
			)
			{

				// get value
				var nextIndex = track.sequenceKeys[ nextBeat ];
				var event = track.sequence[nextIndex];
				// trigger
				Synth(track.synthdef,[\note,event.note,\amp,event.amp]);

				(track.synthdef).postln;
				(event).postln;
				// set track's currentBeat

				track.currentBeat = nextBeat;
			};

			// };





		});

		0.001.wait;

	}

}).play;

)
