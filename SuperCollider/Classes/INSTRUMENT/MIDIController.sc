MIDIController {

	var <> midi;
	var <> name;
	var <> type;
	var <> key;
	var <> controllerId;
	var <> channel;
	var <> sourceId;
	var <> range;
	var <> protocol;

	var listener;

	var callbacks;

	*new {|midi_, type_, controllerId_, channel_, sourceId_ |
		^super.new.init(midi_, type_, controllerId_, channel_, sourceId_ );
	}

	init {|midi_, type_, controllerId_, channel_, sourceId_ |

		midi = midi_;
		protocol = "midi";

		callbacks = List.new;

		controllerId = controllerId_;

		this.addListener( midi, controllerId );
		this.addResponder( type_, controllerId_, channel_, sourceId_ );

	}

	addResponder {|messageType, controllerId, channel, sourceId|

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
