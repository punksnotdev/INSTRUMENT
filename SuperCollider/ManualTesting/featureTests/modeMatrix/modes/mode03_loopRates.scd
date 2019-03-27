var m = ControlMode("loopRates");

m.loadCallback = {

	i.data.looperRates = [ -1/2, -3, -2, -1, 1, 1/2, 2, 4 ];

	64.do{|index|

		var currentRow;
		var currentColumn;
		currentRow = (index/8).floor;
		currentColumn = index%8;

		if( i.notNil ) {
			if( i.data.notNil ) {
				var targetRate = 4;

				if( i.data.loopers[currentRow].rate.notNil ) {
					targetRate = i.data.loopers[currentRow].rate;
				};

				if( targetRate.notNil ) {

					var targetColor = 3;
					var targetKey;
					var offset;

					var currentValue = 0;

					offset = (index/9).floor;

					9.do{|l|
						if(( (index%9==(8-l))&&(index > (7+(8*l)))),{ offset=offset+1; });
					};

					currentValue = targetRate;

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

			if( param2 > 0, {

				currentChannel = (param1/8).floor;
				currentValue = param1%8;

				i.data.loopers[currentChannel].rate = i.data.looperRates[currentValue.asInteger];


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


				i.data.loopers[currentChannel.asInteger].instrument.rate_( i.data.looperRates[currentValue.asInteger] );
				i.data.loopers[currentChannel.asInteger].rate = currentValue.asInteger;

			});

		});

	};

});

m;
