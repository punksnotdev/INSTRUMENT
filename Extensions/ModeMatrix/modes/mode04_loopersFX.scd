var m = ControlMode("loopFX");

m.loadCallback = {
	if( i.data.notNil ) {

		i.data.lastChanThatUsedFX=IdentityDictionary.new;

		i.data.loopFX = [
			nil,
			\reverb,
			\gateDistort,
			\reverbLPF,
			\shineDestroy,
			\delay1,
			\moogFilter,
			\pitchShift,
		];


	};


	8.do {|channelIndex|

		var currentFx = i.data.loopers[channelIndex].fx;

		if( currentFx.notNil ) {

			var targetColor = 60;
			var targetKey;

			var currentValue = 0;

			currentValue = currentFx.asInteger;

			if( currentValue.notNil ) {

				targetKey = ((channelIndex*9)+currentValue).asInteger;

				9.do{|l|
					if(currentValue != l, {
					  targetColor=0;
					  m.midiTarget.send(((channelIndex*9)+l).asInteger,targetColor);
					});
				};

				targetColor=60;


				m.midiTarget.send(targetKey,targetColor);

			}

		}

	};

};

m.setup({


	64.do{|index|

		m.addCallback(index,{|e,param1,param2,midiTarget|

			var offset;
			var targetKey;


			var targetColor;

			var currentRow;
			var currentColumn;
			var fxName;

				if( param2 > 0, {

					currentRow = ((param1/8).floor).asInteger;
					currentColumn = (param1%8).asInteger;


					fxName = i.data.loopFX[currentColumn].asSymbol;

					if( fxName.notNil )
					{

					// TO DO : check if next line should be here
					i.data.loopers[currentRow].fx = currentColumn;


					offset = (param1/9).floor;

					9.do{|l|
						if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
					};

					targetKey = (param1+offset).asInteger;


					targetColor=0;
					9.do{|l|
						midiTarget.send(targetKey+((l-1)-currentColumn),targetColor);
					};

					targetColor=60;

					midiTarget.send(targetKey,targetColor);

					["FX: ", currentRow, currentColumn, ": ", fxName ].postln;

					i.data.currentLoop = i.data.loopers[currentRow];
					i.data.currentLoopFX = fxName;

					i.data.loopers[currentRow].instrument.fx=fxName;
					i.data.loopers[currentRow].fx=currentColumn;

					i.data.lastChanThatUsedFX[ fxName ] = currentRow;
				}
			});

		});

	};

});

m;
