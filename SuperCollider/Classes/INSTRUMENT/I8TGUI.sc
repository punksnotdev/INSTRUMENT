I8TGUI {

	// var <midiDeviceList;
	var <patterns;

	var <currentPattern;
	var <currentPatternViews;
	var <currentPatternViewsList;

	var <tracks;
	var <tracksView;

	var <synthdefs;
	var <synthdefsView;

	var main;

	*new {|main_|
		^super.new.init(main_);
	}

	init {|main_|

		var w,h,v, k;

		main = main_;
		w = Window(bounds:Rect(1620,0,300,580),scroll:true);
		h = HLayout();
		v = VLayout(h);
		w.layout =  v;


		// midiDeviceList = PopUpMenu();
		// v.add(midiDeviceList);
		// midiDeviceList.action = {|element|
		// 	["midi connect", element.value].postln;
		// };

		synthdefsView = ListView(bounds: Rect(10,10,300,100));
		synthdefsView.selectionMode = \extended;

		v.add(synthdefsView);

		tracksView = ListView(bounds: Rect(10,10,300,100));
		tracksView.selectionMode = \extended;

		v.add(tracksView);

		currentPatternViews=VLayout(v);
		currentPatternViewsList=IdentityDictionary.new;
		currentPatternViewsList[\track] = TextView();
		currentPatternViewsList[\pattern] = TextView();
		currentPatternViewsList[\beats] = TextView();
		currentPatternViewsList[\param] = TextView();
		currentPatternViewsList[\key] = TextView();
		currentPatternViewsList[\play_params] = TextView();

		currentPatternViewsList.collect({
			arg item;
			currentPatternViews.add(item);
		});
		v.add(currentPatternViews);

		synthdefsView.action = {|element|
			["synthdef select", element.value].postln;
		};

		tracksView.selectionAction = {|element|
			["track select", element.value, element.selection].postln;
			// main.selectPlayingTracks( element.selection );
		};




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
			currentPatternViewsList[\key].string="key: " ++ currentPattern.key;
			currentPatternViewsList[\play_params].string="play_params: " ++ currentPattern.play_params;
		}.defer;

	}

}
