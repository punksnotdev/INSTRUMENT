(

var ticksPerSecond;
var bpmToTicks;


~bpm = 120.0;
~beatsPerBar = 4;

ticksPerSecond = 10000;
bpmToTicks = ((60/~bpm))*ticksPerSecond;


(thisProcess.nowExecutingPath.dirname ++ "/synthdefs.scd").load;
(thisProcess.nowExecutingPath.dirname ++ "/track-sequences.scd").load;

~playtracks = ~tracks;
~playtracks.collect({|track|

	var lastItemBeat = ((track.sequenceKeys[track.sequence.size-1]));


	var bars = lastItemBeat/(track.beatsPerBar);
	bars = ceil(bars);


	track.totalMs = (bars * track.beatsPerBar )* bpmToTicks;
	track.msDuration = floor(track.totalMs / track.speed);


});



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

			beatDuration = floor((( 1/track.speed * bpmToTicks )));
			beatDuration = beatDuration*2;
			barDuration = beatDuration*track.beatsPerBar;

			isBeat = i%beatDuration;
			isBar = i%(barDuration);


			counter = i % track.msDuration;

			nextBeat = nextBeat % track.sequence.size;

			nextMs = (track.sequenceKeys[ nextBeat ] * bpmToTicks);
			nextMs = nextMs / track.speed;

			if(
				(
					((nextBeat == 0) && (counter == 0) )
				||
				((nextBeat > 0) && (counter >= (nextMs-1)))
				)

			)
			{

				var nextIndex = track.sequenceKeys[ nextBeat ];
				var event = track.sequence[nextIndex];

				Synth(track.synthdef,[\note,event.note,\amp,event.amp]);

				track.currentBeat = nextBeat;

			};

		});

		ticksPerSecond.reciprocal.wait;

	}

}).play;

)
