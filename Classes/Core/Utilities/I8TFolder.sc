I8TFolder : Event
{
	var <> name;

	at {|key|

		if(key.isNumber, {

			var numberKeys = this.keys.reject({|v|v.isNumber==false});

			^super.at( key % numberKeys.size )

		}, {
			// not a number
			^super.at(key);
		});
	}

}
