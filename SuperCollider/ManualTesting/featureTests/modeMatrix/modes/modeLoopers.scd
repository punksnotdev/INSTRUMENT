var m = ControlMode("loopMatrix");

m.data.pressed = IdentityDictionary.new;

m.loadCallback={

	64.do{|index|

		var currentRow;
		var currentColumn;
		currentRow = (index/8).floor;
		currentColumn = index%8;

		if( i.notNil ) {
			if( i.data.notNil ) {

				if( i.data.loopers[currentRow].status.notNil ) {
					var targetColor = 0;
					var targetKey;
					var offset;

					offset = (index/9).floor;

					9.do{|l|
						if(( (index%9==(8-l))&&(index > (7+(8*l)))),{ offset=offset+1; });
					};

					targetKey = (index+offset).asInteger;

					switch( i.data.loopers[currentRow].status[currentColumn],

						1, {
							targetColor=15;
						},
						2, {
							targetColor=60;
						},
						3, {
							targetColor=17;
						}
					);

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

			var looperData;

			currentChannel = (param1/8).floor.asInteger;
			currentValue = param1%8;

			looperData = i.data.loopers[currentChannel];


			if( param2 > 0, {

				m.data.pressed[index] = TempoClock.default.beats;

			});

			if( ((param2 == 0) && (m.data.pressed[index].notNil) ), {

				var delta = TempoClock.default.beats - m.data.pressed[index];

				if( delta > 2, {

					if(looperData.status.notNil) {
						looperData.status[index] = 4;
					};

				}, {

					if(
						looperData.status.notNil
					) {
						if(looperData.status[index] == 4, {
							looperData.status[index] == 5
						});
					};
				});

				m.data.pressed[index] = nil;




				offset = (param1/9).floor;

				9.do{|l|
					if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
				};

				targetKey = (param1+offset).asInteger;

				if( looperData.status.isNil, {
					looperData.status = ();
				});

				if( looperData.status[currentValue].isNil, {
					looperData.status[currentValue]=1;
					looperData.instrument.rec(currentValue);
					targetColor = 15;
					midiTarget.send(targetKey,targetColor);
				}, {


					switch( looperData.status[currentValue],
						1, {
							looperData.status[currentValue] = 2;
							looperData.instrument.start(currentValue);
							targetColor = 60;
							midiTarget.send(targetKey,targetColor);
						},
						2, {
							looperData.status[currentValue] = 3;
							looperData.instrument.stop(currentValue);
							targetColor = 17;
							midiTarget.send(targetKey,targetColor);
						},
						3, {
								looperData.status[currentValue] = 2;
								looperData.instrument.start(currentValue);
								targetColor = 60;
								midiTarget.send(targetKey,targetColor);
						},
						4, {
							looperData.status[currentValue] = 4;
							targetColor = 15;
							midiTarget.send(targetKey,targetColor);
						},
						5, {
							looperData.status[currentValue] = nil;
							looperData.instrument.stop(currentValue);
							targetColor = 0;
							midiTarget.send(targetKey,targetColor);
						}
					);

				})

			})

		});



	};

});

m;
