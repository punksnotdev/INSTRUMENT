var m = ControlMode("looperFXParams");

m.loadCallback = {};

m.setup({


	64.do{|index|

		m.addCallback(index,{|e,param1,param2,midiTarget|

			var offset;
			var targetKey;


			var targetColor;

			var currentChannel;
			var currentValue;

			if( param2 > 0, {

				currentChannel = (param1/8).floor;
				currentValue = param1%8;

				i.data.loopers[currentChannel].amp = currentValue;


				offset = (param1/9).floor;

				9.do{|l|
					if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
				};

				targetKey = (param1+offset).asInteger;


				9.do{|l|
					if(currentValue >= l, {
						targetColor=60;
						midiTarget.send((targetKey-currentValue)+l,targetColor);
					},{
						targetColor=0;
						midiTarget.send(targetKey+((l-1)-currentValue),targetColor);
					});

				};

				targetColor=60;

				if( i.data.currentLoopFX.notNil ) {
					var fxName = i.data.currentLoopFX;

					if( i.data.synths.parameters[ fxName ].notNil, {
						[i.data.synths.parameters[ fxName ][currentChannel], currentValue].postln;
					}, {
						("No parameter data for " ++ i.data.currentLoopFX ).postln;
					});
				};

				midiTarget.send(targetKey,targetColor);


				i.data.loopers[currentChannel.asInteger].instrument.amp_((currentValue/8)*2);
				i.data.loopers[currentChannel.asInteger].amp=(currentValue/8)*2;

			});

		});

	};

});

m;
