var m = ControlMode("loopersFXMainControl");



m.loadCallback = {

	if( i.notNil && i.data.notNil ) {


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

		i.data.loopFXMainControls = IdentityDictionary.new;

		i.data.loopFXMainControls[\reverb]			= (
			parameter: \roompdamp,
			range: [0,1]
		);

		i.data.loopFXMainControls[\gateDistort]		= (
			parameter: \gain,
			range: [1,333]
		);

		i.data.loopFXMainControls[\reverbLPF]		= (
			parameter: \cutoff,
			range: [60,12000]
		);

		i.data.loopFXMainControls[\shineDestroy]	= (
			parameter: \delay,
			range: [0.02,2]
		);

		i.data.loopFXMainControls[\delay2]			= (
			parameter: \delay,
			range: [0.02,2]
		);

		i.data.loopFXMainControls[\moogFilter]		= (
			parameter: \cutoff,
			range: [60,12000]
		);

		i.data.loopFXMainControls[\pitchShift]		= (
			parameter: \pitch,
			range: [0,1]
		);


	};
	64.do{|index|


		if( i.notNil ) {
			if( i.data.notNil ) {

				var currentRow;
				var currentColumn;

				var eachFx;
				var eachParameter;
				if( i.loopFX.isKindOf(Collection) ) {

				currentRow = (index/8).floor.asInteger;
				currentColumn = (index%8).asInteger;


					eachFx = i.loopFX[currentRow];
					eachParameter = i.data.loopFXMainControls[eachFx];

				};

				if( eachParameter.notNil ) {
					if( eachParameter.parameterValue.notNil )  {

						var targetColor = 3;
						var targetKey;
						var offset;

						var currentValue = 0;

						offset = (index/9).floor;

						9.do{|l|
							if(( (index%9==(8-l))&&(index > (7+(8*l)))),{ offset=offset+1; });
						};

						currentValue = (eachParameter.parameterValue*8).asInteger;

						targetKey = ((currentRow*9)+currentValue).asInteger;


						targetColor=0;

						9.do{|l|

							if(currentValue >= l, {
							  m.midiTarget.send(((currentRow*9)+l).asInteger,targetColor);
							},{
							  targetColor=0;
							  m.midiTarget.send(((currentRow*9)+l).asInteger,targetColor);
							});
						};

						targetColor=22+(8*((currentValue)));



						m.midiTarget.send(targetKey,targetColor);

					};
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

			var currentFX;

			var rangeStart;
			var rangeEnd;
			var interpolatedValue;

			if( param2 > 0, {

				currentChannel = (param1/8).floor.asInteger;
				currentValue = (param1%8).asInteger;

				if( i.data.loopFX.isKindOf(Collection) ) {

					var fxName = i.data.loopFX[ currentChannel ].asSymbol;

					if( fxName.notNil ) {

						currentFX = fxName;
						["currentFX",currentFX].postln;

					}
					// ["oldFX",this.data.currentFX].postln;
				};



				offset = (param1/9).floor;

				9.do{|l|
					if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
				};

				targetKey = (param1+offset).asInteger;


				9.do{|l|
					if(currentValue >= l, {
						targetColor=22+(8*l);
						midiTarget.send((targetKey-currentValue)+l,targetColor);
					},{
						targetColor=0;
						midiTarget.send(targetKey+((l-1)-currentValue),targetColor);
					});

				};

				targetColor=22+(8*((currentValue)));

				midiTarget.send(targetKey,targetColor);

				rangeStart = i.data.loopFXMainControls[ currentFX ].range[0];
				rangeEnd = i.data.loopFXMainControls[ currentFX ].range[1];

				interpolatedValue = (((currentValue+1) / 8)* rangeEnd - rangeStart)+rangeStart;

				[
					"Set:",
					i.data.loopFXMainControls[ currentFX ].parameter.asSymbol,
					interpolatedValue
				].postln;

				i.data.loopers[currentChannel].instrument.fxSet(
					i.data.loopFXMainControls[ currentFX ].parameter.asSymbol,
					interpolatedValue
				);

				i.data.loopFXMainControls[currentFX].parameterValue=interpolatedValue;

				// i.data.loopers[currentChannel].fxMainControl = i.data.loopFXMainControls[ currentFX ];
				//
				// i.data.loopers[currentChannel].fxMainControl.parameterValue = currentValue/8;



			});

		});

	};

});

m;
