I8TInstrument : Sequenceable
{


    var group;
	var groupID;

	var outbus;

	var <>synth;
	var <>synths;
	var <>volume;
	var <>octave;
	var <main;
    var <>channel;
	// var synthGroup;
	var outGroup;


	*new{|name_,main_|
		^super.new.init(name_,main_);
	}


	init{|main_,name_|

		volume = 1;
		octave = 4;
		synths = List.new;
		main = main_;
		super.init(main_,name_);


	}

	trigger {

	}

	set {|parameter,value|

	}


	createParametersArray {|array|
		var parameters_array = List.new;

		array.keysValuesDo({|key,value|
			parameters_array.add(key.asSymbol);
			parameters_array.add(value);
		})

		^parameters_array
	}

	parseNote {|raw|
		var noteStrings = ['A','B','C','D','E','F','G'];
		var notes = (
			'Cb': -1,
			'C': 0,
			'C#': 1,
			'Db': 1,
			'D': 2,
			'D#': 3,
			'Eb': 3,
			'E': 4,
			'E#':5,
			'F': 5,
			'Gb': 6,
			'G': 7,
			'G#': 8,
			'Ab': 8,
			'A': 9,
			'A#': 10,
			'Bb': 10,
			'B': 11,
			'B#': 12
		);
		var noteNumber = 4;
		var noteName = "";
		var note;

		if( raw.isKindOf(String) || raw.isKindOf(Symbol), {
			var str = raw.asString;
			if( (str.size > 0) && (noteStrings.includes(str[0].asString.toUpper.asSymbol) == true), {
				str.do({|c|
					if(c.isDecDigit, {
						noteNumber = c.asString.asInteger;
					}, {
						if( noteName.size == 0, {
							noteName = noteName ++ c.asString.toUpper;
						}, {
							noteName = noteName ++ c.asString;
						});
					});
				});

				note = notes[noteName.asSymbol];

				if( note.notNil, {
					note = (note + ((noteNumber-4)*12)).asInteger;
				}, {
					note = 0;
				});
			}, {
				note = raw.asFloat.min(128);
			});
		}, {
			note = raw.asFloat.min(128);
		});

		^note;
	}


	group_ {|group_| }
  	group {
  		^group;
  	}


  	outbus_ {|outbus_|
  		outbus = outbus_;
  	}
  	outbus {
  		^outbus;
  	}


    send {|targetChannel,connect=true|
        ^channel.send(targetChannel,connect);
    }

    connect {|targetChannel|
        ^channel.connect(targetChannel);
    }

    disconnect {|targetChannel|
        ^channel.disconnect(targetChannel);
    }



}
