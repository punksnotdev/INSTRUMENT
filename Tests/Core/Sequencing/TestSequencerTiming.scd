/*
	TestSequencerTiming.scd
	Standalone timing measurement and audible tests for the sequencer overhaul.

	Run each section independently by selecting and evaluating the code block.
	Requires: server booted, INSTRUMENT loaded.

	SynthDefs used (preloaded from Sounds/SynthDefs/Test/):
	  \jitterTest, \jitterTest130, \jitterStress, \jitterLong,
	  \jitterTempo, \clickTest, \stressPad

	See TESTSEQ.md for full description of what these tests validate.
*/


// =====================================================================
// TEST 1: Jitter Measurement via SendReply
// =====================================================================
// Measures actual server-side onset timing of triggered synths.
// Expected: stddev < 0.5ms, no outliers > server.latency (50ms).
// At 120 BPM with 16th notes, expected delta = 0.125s.
(
s.waitForBoot({

	var times, expectedDelta, numNotes, done;

	i = INSTRUMENT();

	times = List.new;
	numNotes = 64;  // collect 64 onsets (4 bars of 16th notes)
	done = false;

	OSCdef(\jitterTest, {|msg, time|
		if(done.not) {
			times.add(time);
			if(times.size >= numNotes) {
				done = true;
				{
					var deltas = times.differentiate.drop(1);
					var mean = deltas.mean;
					var stddev = (deltas.collect({|d| (d - mean).squared }).mean).sqrt;
					var maxJitter = deltas.collect({|d| (d - mean).abs }).maxItem;

					"".postln;
					"=== Jitter Measurement Results ===".postln;
					("Collected " ++ times.size ++ " onsets").postln;
					("Expected delta: 0.125s (16th notes at 120 BPM)").postln;
					("Mean delta:     " ++ mean.round(0.00001) ++ "s").postln;
					("Stddev:         " ++ (stddev * 1000).round(0.01) ++ "ms").postln;
					("Max jitter:     " ++ (maxJitter * 1000).round(0.01) ++ "ms").postln;
					"".postln;

					if(stddev < 0.0005) {
						"PASS: stddev < 0.5ms".postln;
					} {
						("WARN: stddev " ++ (stddev * 1000).round(0.01) ++ "ms exceeds 0.5ms target").postln;
					};

					if(maxJitter < 0.05) {
						"PASS: max jitter < 50ms (server.latency)".postln;
					} {
						("FAIL: max jitter " ++ (maxJitter * 1000).round(0.01) ++ "ms exceeds server.latency").postln;
					};

					"==================================".postln;

					OSCdef(\jitterTest).free;
					i.stop;
				}.defer;
			};
		};
	}, '/noteOn');

	// 120 BPM, 16th notes via speed(4)
	i.tempo = 120;
	i[\jitterTest] = \jitterTest;
	i[\jitterTest].seq("1").speed(4);

});
)


// =====================================================================
// TEST 2: Non-Round Tempo (130 BPM)
// =====================================================================
// Tests timing at 130 BPM where beat durations are irrational.
// The old system produced drift; the new system uses TempoClock natively.
(
s.waitForBoot({

	var times, numNotes, done;

	i = INSTRUMENT();

	times = List.new;
	numNotes = 64;
	done = false;

	OSCdef(\jitterTest130, {|msg, time|
		if(done.not) {
			times.add(time);
			if(times.size >= numNotes) {
				done = true;
				{
					// At 130 BPM, 16th note = 60/130/4 = 0.115384...s
					var expectedDelta = 60 / 130 / 4;
					var deltas = times.differentiate.drop(1);
					var mean = deltas.mean;
					var stddev = (deltas.collect({|d| (d - mean).squared }).mean).sqrt;
					var drift = (mean - expectedDelta).abs;

					"".postln;
					"=== 130 BPM Irrational Tempo Test ===".postln;
					("Expected delta: " ++ expectedDelta.round(0.000001) ++ "s").postln;
					("Mean delta:     " ++ mean.round(0.000001) ++ "s").postln;
					("Drift:          " ++ (drift * 1000).round(0.01) ++ "ms").postln;
					("Stddev:         " ++ (stddev * 1000).round(0.01) ++ "ms").postln;

					if(drift < 0.001) {
						"PASS: drift < 1ms".postln;
					} {
						("WARN: drift " ++ (drift * 1000).round(0.01) ++ "ms — possible beat accumulation error").postln;
					};

					"======================================".postln;

					OSCdef(\jitterTest130).free;
					i.stop;
				}.defer;
			};
		};
	}, '/noteOn130');

	i.tempo = 130;
	i[\jitterTest130] = \jitterTest130;
	i[\jitterTest130].seq("1").speed(4);

});
)


// =====================================================================
// TEST 3: Stress Load (96 instruments)
// =====================================================================
// Runs 8 instruments with patterns simultaneously.
// Measures jitter on one instrument to verify no CPU-induced timing degradation.
(
s.waitForBoot({

	var times, numNotes, done;

	i = INSTRUMENT();

	times = List.new;
	numNotes = 64;
	done = false;

	OSCdef(\jitterStress, {|msg, time|
		if(done.not) {
			times.add(time);
			if(times.size >= numNotes) {
				done = true;
				{
					var deltas = times.differentiate.drop(1);
					var mean = deltas.mean;
					var stddev = (deltas.collect({|d| (d - mean).squared }).mean).sqrt;
					var maxJitter = deltas.collect({|d| (d - mean).abs }).maxItem;

					"".postln;
					"=== Stress Load Jitter Test (96 instruments) ===".postln;
					("Stddev:     " ++ (stddev * 1000).round(0.01) ++ "ms").postln;
					("Max jitter: " ++ (maxJitter * 1000).round(0.01) ++ "ms").postln;

					if(stddev < 0.001) {
						"PASS: stddev < 1ms under load".postln;
					} {
						("WARN: stddev " ++ (stddev * 1000).round(0.01) ++ "ms under 8-instrument load").postln;
					};

					"================================================".postln;

					OSCdef(\jitterStress).free;
					i.stop;
				}.defer;
			};
		};
	}, '/noteOnStress');

	i.tempo = 120;

	// Measurement instrument
	i[\stress0] = \jitterStress;
	i[\stress0].seq("1").speed(4);

	// 7 additional stress instruments
	96.do {|idx|
		var key = ("stress" ++ (idx + 1)).asSymbol;
		i[key] = \stressPad;
		i[key].amp=1/96;
		i[key].seq("1").speed([2, 4	, 8].choose);
	};

});
)


// =====================================================================
// TEST 4: Long-Running Stability
// =====================================================================
// Run for 2+ minutes, then compare first-half vs second-half jitter.
// The old system accumulated float drift; new system has no cumulative state.
(
s.waitForBoot({

	var times, targetDuration, startTime, done;

	i = INSTRUMENT();

	times = List.new;
	targetDuration = 120;  // 2 minutes
	done = false;

	"Long-running test started. Will collect data for 2 minutes...".postln;

	OSCdef(\jitterLong, {|msg, time|
		if(done.not) {
			if(startTime.isNil) { startTime = time };
			times.add(time);
			if((time - startTime) >= targetDuration) {
				done = true;
				{
					var deltas = times.differentiate.drop(1);
					var halfIdx = (deltas.size / 2).asInteger;
					var firstHalf = deltas[0..halfIdx-1];
					var secondHalf = deltas[halfIdx..];
					var stddev1 = (firstHalf.collect({|d| (d - firstHalf.mean).squared }).mean).sqrt;
					var stddev2 = (secondHalf.collect({|d| (d - secondHalf.mean).squared }).mean).sqrt;

					"".postln;
					"=== Long-Running Stability Test (2 min) ===".postln;
					("Total onsets: " ++ times.size).postln;
					("First half stddev:  " ++ (stddev1 * 1000).round(0.01) ++ "ms").postln;
					("Second half stddev: " ++ (stddev2 * 1000).round(0.01) ++ "ms").postln;
					("Drift ratio:        " ++ (stddev2 / stddev1).round(0.01) ++ "x").postln;

					if((stddev2 / stddev1) < 1.5) {
						"PASS: no significant jitter increase over time".postln;
					} {
						"WARN: jitter increased in second half — possible drift".postln;
					};

					"=============================================".postln;

					OSCdef(\jitterLong).free;
					i.stop;
				}.defer;
			};
		};
	}, '/noteOnLong');

	i.tempo = 120;
	i[\jitterLong] = \jitterLong;
	i[\jitterLong].seq("1").speed(4);

});
)


// =====================================================================
// TEST 5: Tempo Change Mid-Playback
// =====================================================================
// Changes tempo during playback. All Routines share one TempoClock,
// so events should stretch uniformly with no re-sync needed.
(
s.waitForBoot({

	var times, phases, numNotes, done;

	i = INSTRUMENT();

	times = List.new;
	phases = List.new;  // track which tempo phase each onset belongs to
	numNotes = 96;
	done = false;

	OSCdef(\jitterTempo, {|msg, time|
		if(done.not) {
			times.add(time);
			phases.add(i.tempo);
			if(times.size >= numNotes) {
				done = true;
				{
					var deltas = times.differentiate.drop(1);

					"".postln;
					"=== Tempo Change Test ===".postln;

					// Group by tempo phase and analyze each
					[\120, \140, \160].do {|tempoSym|
						var tempo = tempoSym.asInteger;
						var indices = phases.selectIndices({|p| p.round(1) == tempo });
						if(indices.size > 2) {
							var phaseDeltas = indices.drop(1).collect({|idx|
								if(idx > 0 && idx < deltas.size) { deltas[idx - 1] } { nil }
							}).reject(_.isNil);
							if(phaseDeltas.size > 0) {
								var expectedDelta = 60 / tempo / 4;
								var mean = phaseDeltas.mean;
								("Tempo " ++ tempo ++ " BPM:").postln;
								("  Expected delta: " ++ expectedDelta.round(0.0001) ++ "s").postln;
								("  Mean delta:     " ++ mean.round(0.0001) ++ "s").postln;
								("  Drift:          " ++ ((mean - expectedDelta).abs * 1000).round(0.01) ++ "ms").postln;
							};
						};
					};

					"=========================".postln;

					OSCdef(\jitterTempo).free;
					i.stop;
				}.defer;
			};
		};
	}, '/noteOnTempo');

	i.tempo = 120;
	i[\jitterTempo] = \jitterTempo;
	i[\jitterTempo].seq("1").speed(4);

	// Change tempo after 32 and 64 notes
	Task({
		8.wait;  // ~32 notes at 120 BPM 16th
		"Changing tempo to 140 BPM".postln;
		i.tempo = 140;
		7.wait;
		"Changing tempo to 160 BPM".postln;
		i.tempo = 160;
	}).play(i.clock);

});
)


// =====================================================================
// AUDIBLE TEST 1: Hi-Hat Grid Tightness
// =====================================================================
// Tight 16th-note hi-hats at 140 BPM alongside quarter-note kick.
// Listen for: even spacing (no "drunk" feel), no flamming between kick+hat.
(
s.waitForBoot({

	i = INSTRUMENT();
	i.tempo = 140;

	i[\hat] = \hihat;
	i[\kick] = \kick;

	i[\hat].seq("1").speed(4);      // 16th notes
	i[\kick].seq("1");               // quarter notes

	"Audible Test 1: Hi-hat grid at 140 BPM".postln;
	"Listen for even 16th-note spacing and tight kick alignment.".postln;
	"Run i.stop to end.".postln;

});
)


// =====================================================================
// AUDIBLE TEST 2: Stereo Flamming (Two Identical Patterns)
// =====================================================================
// Two instruments with identical patterns panned hard L and R.
// Uses \clickTest SynthDef (preloaded from Sounds/SynthDefs/Test/).
// Listen for: clean center image (no stereo flamming/widening from timing offset).
	(
		s.waitForBoot({

			i = INSTRUMENT();
			i.tempo = 120;

			i[\clickL] = \clickTest;
			i[\clickR] = \clickTest;
			//
			i[\clickL].pan = -1;  // hard left
			i[\clickR].pan = 1;   // hard right

			// Identical pattern
			i[\clickL].seq("1").speed(2);
			i[\clickR].seq("1x5 ").speed(8);

			"Audible Test 2: Stereo flamming test".postln;
			"Two identical patterns panned hard L and R.".postln;
			"Listen for: clean center image, no stereo widening from timing offset.".postln;
			"Run i.stop to	 end.".postln;

		});
)


// =====================================================================
// TEST 6: Shuffle (Swing) Timing
// =====================================================================
// Applies shuffle(0.5) to an 8th-note pattern and verifies the alternating
// long/short delta ratio. With shuffle=0.5 on grid=0.5, off-beats shift by
// 0.25 beats, producing a 3:1 long:short ratio (triplet swing feel).
// Expected: ratio ~ 3.0, overall mean matches straight 8th-note period.
(
s.waitForBoot({

	var times, numNotes, done;

	i = INSTRUMENT();

	times = List.new;
	numNotes = 64;
	done = false;

	OSCdef(\shuffleTest, {|msg, time|
		if(done.not) {
			times.add(time);
			if(times.size >= numNotes) {
				done = true;
				{
					var deltas = times.differentiate.drop(1);
					var longDeltas = List.new;
					var shortDeltas = List.new;
					var ratio, meanLong, meanShort, overallMean;

					deltas.do({|d, idx|
						if(idx.even) { longDeltas.add(d) } { shortDeltas.add(d) };
					});

					meanLong = longDeltas.mean;
					meanShort = shortDeltas.mean;
					overallMean = deltas.mean;
					ratio = meanLong / meanShort;

					"".postln;
					"=== Shuffle (Swing) Timing Test ===".postln;
					("Collected " ++ times.size ++ " onsets").postln;
					("Mean long delta:  " ++ meanLong.round(0.00001) ++ "s").postln;
					("Mean short delta: " ++ meanShort.round(0.00001) ++ "s").postln;
					("Long:short ratio: " ++ ratio.round(0.01) ++ " (expected: 3.0)").postln;
					("Overall mean:     " ++ overallMean.round(0.00001) ++ "s").postln;
					"".postln;

					if((ratio - 3.0).abs < 0.3) {
						"PASS: ratio within 10% of 3.0 (triplet swing)".postln;
					} {
						("FAIL: ratio " ++ ratio.round(0.01) ++ " deviates >10% from expected 3.0").postln;
					};

					if((meanLong + meanShort - (overallMean * 2)).abs < 0.002) {
						"PASS: long+short preserves overall period".postln;
					} {
						"WARN: long+short does not sum to expected period".postln;
					};

					"===================================".postln;

					OSCdef(\shuffleTest).free;
					i.stop;
				}.defer;
			};
		};
	}, '/noteOn');

	i.tempo = 120;
	i[\jitterTest] = \jitterTest;
	i[\jitterTest].seq("1").speed(2);   // 8th notes
	i[\jitterTest].shuffle(0.5);        // triplet swing

});
)


// =====================================================================
// AUDIBLE TEST 3: Pattern Hot-Swap
// =====================================================================
// Start a pattern, then swap it mid-playback.
// The new pattern should start on the next bar boundary.
(
s.waitForBoot({

	i = INSTRUMENT();
	i.tempo = 120;

	i[\hat] = \hihat;
	i[\hat].seq("1").speed(2);
	i[\hat].seq("1 0.5 1 0.3").speed(4);

	"Audible Test 3: Pattern hot-swap".postln;
	"Evaluate the following line after a few bars to swap the pattern:".postln;
	"  i[\\hat].seq(\"1 0.5 1 0.3\").speed(4);".postln;
	"The new pattern should land on a bar boundary.".postln;

});
)


// =====================================================================
// AUDIBLE TEST 4: Shuffle (Swing) Feel
// =====================================================================
// Straight 8th-note hi-hat with shuffle applied.
// Listen for: "triplet" or "bounce" feel vs straight grid.
// Try changing shuffle amount live: i[\hat].shuffle(0.3), i[\hat].shuffle(0), etc.
(
s.waitForBoot({

	i = INSTRUMENT();
	i.tempo = 120;

	i[\hat] = \hihat;
	i[\kick] = \kick;

	i[\kick].seq("1");                  // straight quarter notes (no shuffle)
	i[\hat].seq("1").speed(2);          // 8th notes
	i[\hat].shuffle(0.5);               // triplet swing

	"Audible Test 4: Shuffle (swing) feel".postln;
	"Hi-hat 8th notes with shuffle(0.5), kick on quarters.".postln;
	"Try live:".postln;
	"  i[\\hat].shuffle(0.3);   // lighter swing".postln;
	"  i[\\hat].shuffle(0);     // back to straight".postln;
	"  i[\\hat].shuffle(0.7);   // hard swing".postln;
	"Run i.stop to end.".postln;

});
)
