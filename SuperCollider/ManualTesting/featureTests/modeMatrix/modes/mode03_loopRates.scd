var m = ControlMode("loopRates");

m.loadCallback = {

	i.data.looperRates = [ -1/2, -3, -2, -1, 1, 1/2, 2, 4 ];


	m.loadCallback = {

		8.do {|channelIndex|

			var currentRate = i.data.loopers[channelIndex].rate;

			if( currentRate.notNil ) {

				var targetColor = 3;
				var targetKey;

				var currentValue = 4;

				currentValue = currentRate.asInteger;

				if( currentValue.notNil ) {

					targetKey = ((channelIndex*9)+currentValue).asInteger;


					9.do{|l|
						if(currentValue != l, {
						  targetColor=0;
						  m.midiTarget.send(((channelIndex*9)+l).asInteger,targetColor);
						});
					};

					targetColor=22+(8*currentValue);



					m.midiTarget.send(targetKey,targetColor);

				}

			}

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
					if(currentValue != l, {
					  targetColor=0;
					  m.midiTarget.send(((currentChannel*9)+l).asInteger,targetColor);
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
