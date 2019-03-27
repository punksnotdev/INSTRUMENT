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
			\delay2,
			\moogFilter,
			\pitchShift,
		];


	};
	64.do{|index|

		var currentRow;
		var currentColumn;
		currentRow = (index/8).floor;
		currentColumn = index%8;

		if( i.notNil ) {
			if( i.data.notNil ) {

				if( i.data.loopers[currentRow].fx.notNil ) {

					var targetColor = 3;
					var targetKey;
					var offset;

					var currentValue = 0;

					offset = (index/9).floor;

					9.do{|l|
						if(( (index%9==(8-l))&&(index > (7+(8*l)))),{ offset=offset+1; });
					};

					currentValue = i.data.loopers[currentRow].fx;

					targetKey = ((currentRow*9)+currentValue).asInteger;


					targetColor=0;
					9.do{|l|
					  m.midiTarget.send(((currentRow*9)+l).asInteger,targetColor);
					};

					targetColor=60;

					m.midiTarget.send(targetKey,targetColor);

				};

			};
		};
	};

};

m.setup({


	64.do{|index|

		m.addCallback(index,{|e,param1,param2,midiTarget|

			var offset;
			var targetKey;


			var targetColor;

			var currentChannel;
			var currentValue;
			var fxName;

				if( param2 > 0, {

					currentChannel = ((param1/8).floor).asInteger;
					currentValue = (param1%8).asInteger;


					fxName = i.data.loopFX[currentValue].asSymbol;

					if( fxName.notNil )
					{

					// TO DO : check if next line should be here
					i.data.loopers[currentChannel].fx = currentValue;


					offset = (param1/9).floor;

					9.do{|l|
						if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
					};

					targetKey = (param1+offset).asInteger;


					targetColor=0;
					9.do{|l|
						midiTarget.send(targetKey+((l-1)-currentValue),targetColor);
					};

					targetColor=60;

					midiTarget.send(targetKey,targetColor);

					["FX: ", currentChannel, currentValue, ": ", fxName ].postln;

					i.data.currentLoop = i.data.loopers[currentChannel];
					i.data.currentLoopFX = fxName;

					i.data.loopers[currentChannel].instrument.fx=fxName;
					i.data.loopers[currentChannel].fx=currentValue;

					i.data.lastChanThatUsedFX[ fxName ] = currentChannel;
				}
			});

		});

	};

});

m;
