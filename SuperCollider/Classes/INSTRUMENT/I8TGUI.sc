I8TGUI {

	// var <midiDeviceList;
	var <patterns;

	var <currentPattern;
	var <currentPatternViews;
	var <currentPatternViewsList;

	var <currentTracks;
	var <currentTracksView;

	var <synthdefsList;
	var <synthdefsListView;

	*new {
		^super.new.init();
	}

	init {

		var w,h,v, k;

		w = Window(bounds:Rect(1620,0,300,580),scroll:true);
		h = HLayout();
		v = VLayout(h);
		w.layout =  v;


		// midiDeviceList = PopUpMenu();
		// v.add(midiDeviceList);
		// midiDeviceList.action = {|element|
		// 	["midi connect", element.value].postln;
		// };

		synthdefsListView = ListView(bounds:Rect(10,10,300,100));
		v.add(synthdefsListView);

		currentTracksView = ListView(bounds:Rect(10,10,300,100));
		v.add(currentTracksView);

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

		synthdefsListView.action = {|element|
			["synthdef select", element.value].postln;
		};




		w.front;
		w.alwaysOnTop = true;

	}


	synthdefsList_ {|list, callback|

		synthdefsList = list;

		{
			synthdefsListView.items = list;
			synthdefsListView.action = callback;
		}.defer;

		^synthdefsList;

	}


	currentTracks_ {|list, callback|

		currentTracks = list;

		{
			currentTracksView.items = list;
			currentTracksView.action = callback;
		}.defer;

		^currentTracks;

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
