var m = ControlMode("animation test");

m.loadCallback = {



	Tdef(\animation, {

			inf.do {|index|

				8.do{|index|

					m.midiTarget.send(index,0);

				};

				// (index%8).asInteger.postln;
				m.midiTarget.send((index%8).asInteger,60);

				(1/2).wait;

			}

	}).play;


};



m.destroyCallback = {

	Tdef(\animation).stop;

};

m.setup({




		64.do{|index|

			m.addCallback(index,{|e,note,velocity,midiTarget|

				var currentRow, currentColumn;

				currentRow = (note / 8).floor;
				currentColumn = (note % 8);

				[ "pressed", currentRow, currentColumn ].postln;


			});

		};



});

m;
