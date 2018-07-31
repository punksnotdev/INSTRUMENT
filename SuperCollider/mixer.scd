(
s.options.memSize=2048*1024;
s.boot;
)
(
s.doWhenBooted({

	().play;
	s.scope;




ProxySpace.clearAll;
	p = ProxySpace.push(s);
		  TempoClock.tempo = ((166/2)/120);
		  p.makeTempoClock(1,'instrument',TempoClock.default);



		  p.quant = 4;

		  ~out=NodeProxy.audio(s,2);
		  ~z=NodeProxy(s,\audio,2);

		  ~mix=NodeProxy.audio(s,2);

		  ~z= { Silent.ar ! 2 };
		  ~z.fadeTime = 5;
		  ~z = { WhiteNoise.ar(1/[2,2]) };

		  ~amp = 0.5;
		  ~mix = ~z;

		  ~mix.fadeTime=8;

		  ~out = {
		    Pan2.ar(Limiter.ar(CompanderD.ar(Mix.new(HPF.ar(~mix.ar,30)), 0.9, 1, 0.9, 0.045,0.3 )*~amp),0);
		  };


		  ~out.play;

		  ("~out"++~out).postln;



		  p.quant=4;
		  ~out.quant=4;
		  // ~x.quant_(4);




		  // p.makeTempoClock( bpm / 60 );



		  ~out.play
		  // [~mix,~x].postln;ix



		  ["play",~z,~out,p].postln;


		  "INSTRUMENT v.0.1".postln;


		  // ~x = { Silent.ar ! 2 };



		// ().play;
		// b = Tdef(\boot,{

	// 		1.do {
	//
	// 			1.wait;
	//
	//
	// 			4.wait;
	//
	// 			TempoClock.tempo = (bpm/120);
	// 			p.makeTempoClock(bpm/120);
	// [\bpm,~bpm].postln;
	// 			1.wait;
	//
	// 			p.quant = 4;
	// 			//
	// 			~out=NodeProxy.audio(s,2);
	// 			~x=NodeProxy.audio(s,2);
	// 			~mix=NodeProxy.audio(s,2);
	//
	// 			3.wait;
	//
	// 			~amp = 0.25;
	// 			~mix = ~x;
	//
	// 			~out = {
	// 				Pan2.ar(Limiter.ar(CompanderD.ar(Mix.new(HPF.ar(~mix.ar,30)), 1, 0.9, 45, 300 )*~amp),0);
	// 			};
	//
	// 			3.wait;
	//
	// 			~out.play;
	// 			("~out"++~out).postln;
	//
	// 			// 1.wait;
	//
	//
	// 			p.quant=4;
	// 			~out.quant=4;
	// 			// ~x.quant_(4);
	//
	// 			3.wait;
	//
	//
	//
	// 			// p.makeTempoClock( bpm / 60 );
	//
	//
	// 			~x= { Silent.ar ! 2 };
	// 			~x.fadeTime = 5;
	// 			~x = { WhiteNoise.ar(1/2) ! 2 };
	// 			// [~mix,~x].postln;ix
	//
	//
	// 			["play",~x,~out,p].postln;
	//
	// 			// 1.wait;
	//
	// 			"INSTRUMENT v.0.1".postln;
	//
	// 			5.wait;
	//
	// 			~x = { Silent.ar ! 2 };
	//
	// 		}

		});

		// ~x = { Silent.ar ! 2 };

		b.play;



});
)
