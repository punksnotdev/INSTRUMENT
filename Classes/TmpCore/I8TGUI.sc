I8TGUI {

	// var <midiDeviceList;
	var <patterns;

	var <currentPattern;
	var <currentPatternViews;
	var <currentPatternViewsList;

	var <tracks;
	var <tracksView;
	var <trackActionsView;

	var <synthdefs;
	var <synthdefsView;
	var <synthdefActionsView;

	var main;

	*new {|main_|
		^super.new.init(main_);
	}

	init {|main_|

		var f,w,h,v,k,b,t,tc;

		main = main_;
		w = Window(bounds:Rect(1620,0,300,580),scroll:true);
		w.alwaysOnTop=true;
		h = HLayout();
		v = VLayout(h,33);
		w.layout =  v;

		w.onClose_({ f.kill });

		// midiDeviceList = PopUpMenu();
		// v.add(midiDeviceList);
		// midiDeviceList.action = {|element|
		// 	["midi connect", element.value].postln;
		// };

		synthdefsView = ListView(bounds: Rect(10,10,300,100));
		synthdefsView.selectionMode = \extended;

		v.add(synthdefsView,8);

		synthdefActionsView = HLayout(
			Button().states_([["New Synth"]]),
	        [StaticText().string_("BarBarBar"), stretch:4]
	    );

		v.add(synthdefActionsView);


		tracksView = ListView(bounds: Rect(10,10,300,100));
		v.add(tracksView,8);


		trackActionsView = HLayout(
			Button().states_([["Pause"],["Play"]]),
			Button().states_([[1]]),
			Button().states_([[2]]),
	        Button().states_([[3]]),
	    );
		v.add(trackActionsView);

		v.add(nil,3);



		currentPatternViews=VLayout(v,1);
		currentPatternViewsList=IdentityDictionary.new;
		currentPatternViewsList[\track] = StaticText();
		//bounds:Rect(10,10,300,30));
		currentPatternViewsList[\pattern] = StaticText();
		//bounds:Rect(10,10,300,30));
		currentPatternViewsList[\beats] = StaticText();
		//bounds:Rect(10,10,300,30));
		currentPatternViewsList[\param] = StaticText();
		//bounds:Rect(10,10,300,30));
		// currentPatternViewsList[\key] = TextView(bounds:Rect(10,10,300,30));
		// currentPatternViewsList[\play_params] = TextView(bounds:Rect(10,10,300,30));

		currentPatternViewsList.collect({
			arg item;
			currentPatternViews.add(item,0);
		});
		v.add(currentPatternViews,1);




		synthdefsView.action = {|element|
			["synthdef select", element.value].postln;
		};

		// tracksView.selectionAction = {|element|
		// 	// ["track select", element.value, element.selection].postln;
		// 	// main.selectPlayingTracks( element.selection );
		// };




		w.front;
		w.alwaysOnTop = true;

	}


	synthdefs_ {|list, callback|

		synthdefs = list;

		{
			synthdefsView.items = list;
			synthdefsView.action = callback;
		}.defer;

		^synthdefs;

	}


	tracks_ {|list, callback|

		var names = list.collect({arg item; item.name});

		var selection = list.collect({arg item,index; if(item.playing==true) { index } });


		tracks = list;
		{

			tracksView.items = ['']++list.collect({arg item; item.name});

			tracksView.selection = list.collect({arg item,index; if(item.playing==true) { index + 1 } });

		}.defer;

		^tracks;

	}



	currentPattern_ {|currentPattern_|
		currentPattern = currentPattern_;
		{
			currentPatternViewsList[\track].string=
			"track: " ++ currentPattern.track;
			currentPatternViewsList[\pattern].string="pattern: " ++ currentPattern.pattern;
			currentPatternViewsList[\beats].string="beats: " ++ currentPattern.beats;
			currentPatternViewsList[\param].string="param: " ++ currentPattern.param;
			// currentPatternViewsList[\key].string="key: " ++ currentPattern.key;
			// currentPatternViewsList[\play_params].string="play_params: " ++ currentPattern.play_params;
		}.defer;

	}

}
