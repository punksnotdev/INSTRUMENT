var m = ControlMode("loopMatrix");

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

					switch( i.data.loopers[currentRow].status,

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

					[m.midiTarget,targetKey,targetColor].postln;
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

			if( param2 > 0, {

				currentChannel = (param1/8).floor;
				currentValue = param1%8;

				looperData = i.data.loopers[currentChannel].postln;

				["currentChannel",currentChannel, currentValue].postln;

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
					if( looperData.status[currentValue]==1, {
						looperData.status[currentValue] = 2;
						looperData.instrument.start(currentValue);
						targetColor = 60;
						midiTarget.send(targetKey,targetColor);
					}, {
						if( looperData.status[currentValue]==2, {
							looperData.status[currentValue] = 3;
							looperData.instrument.stop(currentValue);
							targetColor = 17;
							midiTarget.send(targetKey,targetColor);
						}, {
							if( looperData.status[currentValue]==3, {
								looperData.status[currentValue] = 2;
								looperData.instrument.start(currentValue);
								targetColor = 60;
								midiTarget.send(targetKey,targetColor);
							});

						})

					})
				});



			});

		});

	};

});
//
// 64.do{|index|
// 	m.addCallback(index,{|e,param1,param2,midiTarget|
//
// 		var offset;
// 		var targetKey;
//
//
// 		var targetColor;
//
// 		var currentChannel;
//
// 		currentChannel = (param1/8).floor;
//
// 		["currentChannel",currentChannel].postln;
//
//
//
//
// 		offset = (param1/9).floor;
//
// 		9.do{|l|
// 			if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
// 		};
//
// 		targetKey = (param1+offset).asInteger;
//
// 		midiTarget.send(targetKey,20+(23*(currentChannel%5)));
//
// 	});
// };

m;
