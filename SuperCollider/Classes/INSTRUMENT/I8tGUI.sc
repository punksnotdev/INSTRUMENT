I8tGUI {

	var <midiDeviceList;

	*new {
		^super.new.init();
	}

	init {

		var w,h,v, k;

		w = Window(bounds:Rect(1400,0,500,1080));
		h = HLayout();
		v = VLayout(h);
		w.layout =  v;


		midiDeviceList = PopUpMenu();
		v.add(midiDeviceList);
		midiDeviceList.action = {|element|
			["midi connect", element.value].postln;
		};


		w.front;
		w.alwaysOnTop = true;

	}


	setMIDIDevices {|midiDevices, callback|
		["set mdi", midiDevices].postln;
		{
			midiDeviceList.items = midiDevices;
			midiDeviceList.callback = callback;
		}.defer;

	}


}
