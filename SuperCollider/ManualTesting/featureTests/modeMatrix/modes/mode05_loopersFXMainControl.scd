var m = ControlMode("loopersFXMainControl");



m.loadCallback = {

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



	64.do{|index|

		var currentRow;
		var currentColumn;

		currentRow = (index/8).floor;
		currentColumn = index%8;

		if( i.notNil ) {
			if( i.data.notNil ) {

				if( i.data.loopers[currentRow].fxMainControl.notNil )  {


					var targetColor = 3;
					var targetKey;
					var offset;

					var currentValue = 0;

					offset = (index/9).floor;

					9.do{|l|
						if(( (index%9==(8-l))&&(index > (7+(8*l)))),{ offset=offset+1; });
					};

					currentValue = (i.data.loopers[currentRow].fxMainControl * currentFX r;

					targetKey = ((currentRow*9)+currentValue).asInteger;


					9.do{|l|
						[currentValue,l].postln;
						if(currentValue >= l, {
						  targetColor=22+(8*l);
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

				currentChannel = (param1/8).floor;
				currentValue = param1%8;

				if( i.data.loopFX.isKindOf(Collection) ) {
					currentFX = i.data.loopFX[ currentValue.asInteger ];
				};

				i.data.loopers[currentChannel].fxMainControl = i.data.looperRates[ currentFX ];


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

				rangeStart = i.data.loopersFXMainControl[ currentFX ].range[0];
				rangeEnd = i.data.loopersFXMainControl[ currentFX ].range[1];

				interpolatedValue = ((currentValue / 8)* rangeEnd - rangeStart)+rangeStart;

				i.data.loopers[currentChannel.asInteger].instrument.fxSet(
					i.data.loopersFXMainControl[ currentFX ].parameter.asSymbol,
					mappedValue
				);
				i.data.loopers[currentChannel.asInteger].fxMainControl = i.data.looperRates[ currentFX ];

			});

		});

	};

});

m;
