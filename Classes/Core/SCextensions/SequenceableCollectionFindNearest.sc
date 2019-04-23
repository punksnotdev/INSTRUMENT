+ SequenceableCollection {


	findNearest{|n|

		var index=this.indexOfNearest(n);

		if (index.notNil) {

			^this.at(index);

		}{

			^nil

		}

	}



	indexOfNearest{|n|

		var diff=inf, index;

		this.do{|i,j|

			var d = (n-i).abs;

			if (d<diff) {

				diff=d;

				index=j

			};

		};

		^index

	}



}
