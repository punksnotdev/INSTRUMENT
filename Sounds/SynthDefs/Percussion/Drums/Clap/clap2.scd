SynthDef(\clap2, {|amp=0.5,out=0|

   var claposc, clapenv, clapnoise, clapoutput;

   clapnoise = {BPF.ar(LPF.ar(WhiteNoise.ar(1),7500),1500)};
   clapenv = {Line.ar(1, 0, 0.6, doneAction: 2)};

   clapoutput = {Mix.arFill(7,
   {arg i;
   EnvGen.ar(
   Env.new(
   [0,0,1,0],
   [0.01 * i,0,0.04]
   )
   ) * clapnoise * 0.5
   }
   )};

   Out.ar(out,Pan2.ar((clapoutput*amp) * clapenv, 0) / 2);

}).store;
