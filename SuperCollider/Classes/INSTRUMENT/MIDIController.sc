MIDIController {

	var < controllers;
	var <> controllerManager;
	var <> name;
	var <> type;
	var <> key;
	var <> controllerId;
	var <> channel;
	var <> sourceId;
	var <> range;

	var listener;

	var callbacks;

	*new {|controllerManager_, type_, controllerId_, channel_, sourceId_ |
		^super.new.init(controllerManager_, type_, controllerId_, channel_, sourceId_ );
	}

	init {|controllerManager_, type_, controllerId_, channel_, sourceId_ |

		controllerManager = controllerManager_;
		controllers = IdentityDictionary.new;
		callbacks = List.new;

		controllerId = controllerId_;

		this.addListener( controllerManager, controllerId );
		this.addResponder( type_, controllerId_, channel_, sourceId_ );

	}

	addResponder {|messageType, controllerId, channel, sourceId|
[messageType, controllerId, channel, sourceId].postln;
		switch(messageType,
			\cc, {


				var func = MIDIFunc.cc(
					{arg ...args;

						this.set(args[0])

					}, controllerId, channel, sourceId
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
