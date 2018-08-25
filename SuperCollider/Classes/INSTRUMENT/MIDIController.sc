MIDIController {

	var < controllers;
	var <> controllerSource;

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

		switch(messageType,
			\cc, {

				var func = MIDIFunc.cc(
					{
						("received: "++messageType).postln;
					},
					controllerId, channel, controllerSource
				);

				callbacks.add( func );

			},
			\noteOn, { ("received: "++messageType).postln; },
			\noteOff, { ("received: "++messageType).postln; },
			{ "message type not recognized".postln; }
		);

	}

	addController{|key,controller|

		controllers[key] = InstrumentController

	}

}
