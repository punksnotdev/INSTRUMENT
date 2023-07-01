+ SequenceableCollection {


	findNearest{|n|

		var index=this.indexOfNearestIrregularIndex(n);

		if (index.notNil, {

			^this.at(index);

		}, {

			^nil

		});

	}
	

	searchIndex {|x, start, end|
        
        var mid;
        var i;
		var val;
		
        // (x:x, start:start, end:end).postln;

        if (start > end, {
            ^ start;
        });

        mid=((start+end)/2).floor.asInteger;
		val = this[mid];
        
        
        if ( x <= val, {
            
            
            if( mid > 0, {
                
                var prevVal = this[mid-1];

                if( prevVal <= x, {
                    
                    
                    if( (val-x) < (x-prevVal), {
                        i = mid;
                    }, {
                        i = (mid-1);
                    });

                }, {
                    i = this.searchIndex(x, start, mid);
                });

            }, {	
                i = 0;
            });

		}, {
			if( mid < (this.size-1), {
				
			
                var nextVal = this[mid+1];

				if( x<=nextVal, {

					if( ( x - val ).abs < (x-nextVal).abs, {
						i = mid;
					}, {
						i = this.searchIndex(x, mid+1,  end );
					});

				}, { // if( this[mid+1]<x ){
					
					i = this.searchIndex(x, mid+1,  end );
					
				});

			}, {			
				i = (this.size-1);
			});
			

		});

		^ i		
	}

    indexOfNearestIrregularIndex {|n|
        
        ^ this.searchIndex(n, 0, this.size - 1)

    }



}
