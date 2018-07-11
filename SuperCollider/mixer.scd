(
s.boot;


s.doWhenBooted({
	().play;
	s.scope;
	b = Tdef(\boot,{

		1.do {
			var bpm = 160;

			2.wait;

			TempoClock.tempo = (bpm/120);
			p = ProxySpace.push(s);
			p.makeTempoClock(144/120);

			["set proxyspace",p].postln;

			1.wait;


			p.quant = 4;
			//
			// ~o=NodeProxy.audio(s,2);
			// ~z=NodeProxy.audio(s,2);
			// ~m=NodeProxy.audio(s,2);

			~amp = 0.25;
			~z= { Silent.ar ! 2 };
			~m = ~z;
			~o = {
				Pan2.ar(Limiter.ar(CompanderD.ar(Mix.new(HPF.ar(~m.ar,30)))*~amp),0);
			};
			1.wait;

			~o.play;
			("~o"++~o).postln;

			1.wait;


			p.quant=4;
			~o.quant=4;
			// ~z.quant_(4);

			1.wait;



			// p.makeTempoClock( bpm / 60 );


			~z.fadeTime = 5;
			~z = { WhiteNoise.ar(1/4) ! 2 };
			// [~m,~z].postln;


			["play",~z,~o,p].postln;

			1.wait;

			"INSTRUMENT v.0.1".postln;

			1.wait;

			~z = { Silent.ar ! 2 };

		}

	});

	// ~z = { Silent.ar ! 2 };

	b.play;

});


)

			// s.record;
().play
