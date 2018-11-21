Looper : Instrument
{


		var buffers;


		*new{|name_,synthdef_,mode_=nil|
			^super.new.init(name_,this.graph,synthdef_,mode_);
		}

		init{|name_,graph_,synthdef_,mode_=nil|

			buffers = IdentityDictionary.new;

			super.init(name_,graph_);

		}



		rec {|layer|

			// if buffer for layer not allocated,
			if( buffers[ layer ].isKindOf(Buffer), {

			}, {

			});
			// allocate one



		}

		delete {|layer|
			// if no layers selected
			// delete all available layers

				// if layer exists
				// delete it
		}

		play {|layer|

			// if no layers selected
			// play all available layers

				// if layer exists
				// play it

		}

		stop {|layer|

			// if no layers selected
			// stop all layers

				// if layer exists

				// stop it
		}


		seek {|layer|

			// if no layers selected
			// seek all available layers

				// if layer exists
				// seek it

		}


}
