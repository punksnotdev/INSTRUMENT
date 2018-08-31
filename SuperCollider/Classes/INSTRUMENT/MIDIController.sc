MIDIController {

	var < controllers;
	var <> controllerSource;
	var <> name;

	var listener;

	var callbacks;

	*new {|source_|
		^super.new.init(source_);
	}

	init {|source_|

		controllerSource = source_;
		controllers = IdentityDictionary.new;
		callbacks = List.new;

	}

	addResponder {|messageType, controllerId, channel|
		["messageType",messageType].postln;
		switch(messageType,
			\cc, {


				var func = MIDIFunc.cc(
					{arg ...args;
						// "cc!!".postln;

						args.postln;

						// ("received: "++messageType).postln;

						this.set( )
					}//,
					// controllerId, channel, controllerSource
				);

				callbacks.add( func );

			},
			\noteOn, { ("received: "++messageType).postln; },
			\noteOff, { ("received: "++messageType).postln; },
			{ "message type not recognized".postln; }
		);

	}


	addListener{|listener_|
		listener = listener_;
	}


	set {|value|

		listener.set(this, value);

	}

}
