var m = ControlMode("loopVolumes");



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

				i.data.loopers[currentChannel].postln;
				["currentChannel",currentChannel, currentValue].postln;

				offset = (param1/9).floor;

				9.do{|l|
					if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
				};

				targetKey = (param1+offset).asInteger;


				9.do{|l|
					if(currentValue >= l, {
						targetColor=71+(8*l);
						midiTarget.send((targetKey-currentValue)+l,targetColor);
					},{
						targetColor=0;
						midiTarget.send(targetKey+((l-1)-currentValue),targetColor);
					});

				};

				targetColor=71+(8*((currentValue)));

				midiTarget.send(targetKey,targetColor);


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
