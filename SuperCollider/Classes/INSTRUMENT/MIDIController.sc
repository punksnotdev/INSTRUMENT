MIDIController {

	var < controllers;
	var <> controllerManager;
	var <> name;
	var <> type;
	var <> range;

	var listener;

	var callbacks;

	*new {|controllerManager_, type_ |
		^super.new.init(controllerManager_, type_ );
	}

	init {|controllerManager_, type_ |

		controllerManager = controllerManager_;
		controllers = IdentityDictionary.new;
		callbacks = List.new;

		type = type_;

		this.addListener( controllerManager );
		this.addResponder( type );

	}

	addResponder {|messageType, controllerId, channel|

		switch(messageType,
			\cc, {


				var func = MIDIFunc.cc(
					{arg ...args;

						this.set(args[0])

					}
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
