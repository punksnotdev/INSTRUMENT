var m = ControlMode("loopMatrix");



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
					looperData.instrument.rec;
					targetColor = 15;
					midiTarget.send(targetKey,targetColor);
				}, {
					if( looperData.status[currentValue]==1, {
						looperData.status[currentValue] = 2;
						looperData.instrument.start;
						targetColor = 60;
						midiTarget.send(targetKey,targetColor);
					}, {
						if( looperData.status[currentValue]==2, {
							looperData.status[currentValue] = 3;
							looperData.instrument.stop(currentValue);
							targetColor = 24;
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
