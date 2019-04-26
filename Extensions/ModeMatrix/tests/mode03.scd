var m = ControlMode.new;

64.do{|index|
	m.addCallback(index,{|e,param1,param2,midiTarget|

		var offset;
		var targetKey;


		var targetColor;

		var currentChannel;

		currentChannel = (param1/8).floor;

		["currentChannel",currentChannel].postln;




		offset = (param1/9).floor;

		9.do{|l|
			if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
		};

		targetKey = (param1+offset).asInteger;

		midiTarget.send(targetKey,20+(23*(currentChannel%5)));

	});
};

m;
