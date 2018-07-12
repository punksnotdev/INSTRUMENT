+ IdentityDictionary {

	findKey{|value|
		var found = -1;
		block{|break|
			this.keysValuesDo({|key,item|
				if( item == value, {
					found = key;
					break.value;
				});
			});
		}
		^found;
	}

}
