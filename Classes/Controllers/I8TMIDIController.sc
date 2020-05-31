MIDIController {

	var <> midiTarget;
	var <> name;
	var <> type;
	var <> key;
	var <> controllerId;
	var <> channel;
	var <> sourceId;
	var <> range;
	var <> protocol;

	var <listener;

	var callbacks;

	*new {|midiTarget_, type_, controllerId_, channel_, sourceId_, name_ |
		^super.new.init(midiTarget_, type_, controllerId_, channel_, sourceId_, name_ );
	}

	init {|midiTarget_, type_, controllerId_, channel_, sourceId_, name_ |

		midiTarget = midiTarget_;
		type = type_;
		protocol = "midi";

		name = name_;

		callbacks = List.new;

		controllerId = controllerId_;

		this.addListener( midiTarget, controllerId );
		this.addResponder( type_, controllerId_, channel_, sourceId_ );

	}

	addResponder {|messageType, controllerId, channel, sourceId|
		// ["add Responder", controllerId, channel, sourceId].postln;
		switch(messageType,
			\cc, {

				var func = MIDIdef.cc( (name ++"_"++controllerId++"_cc").asSymbol,
					{arg ...args;
						// args.postln;
						this.set(args[0])

					}, controllerId, channel, sourceId
				);
// [func, name, controllerId, channel, sourceId].postln;
				callbacks.add( func );

			},
			\note, {

				var func;

				func = MIDIdef.noteOn( (name ++ "_noteOn" ).asSymbol,
					{

						arg ...args;

						this.set(args[1],args[0]);

					}, controllerId, channel, sourceId
				);

				callbacks.add( func );

				func = MIDIdef.noteOff( (name ++ "_noteOff" ).asSymbol,
					{arg ...args;

						this.set(args[1],args[0]);

					}, controllerId, channel, sourceId
				);

				callbacks.add( func );


			},
			{ "message type not recognized".postln; }
		);

	}


	addListener{|listener_|
		listener = listener_;
	}


	set {|param1,param2|

		listener.set(this, param1, param2);

	}

}
