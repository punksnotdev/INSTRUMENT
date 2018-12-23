m = ControlMode.new;

64.do{|index|
	m.addCallback(index,{|e,param1,param2,midiTarget|

		var offset;
		var targetKey;

		offset = (param1/9).floor;

		9.do{|l|
			if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
		};

		targetKey = (param1+offset).asInteger;

		midiTarget.send(targetKey,param2*3);

	});
};

m;
