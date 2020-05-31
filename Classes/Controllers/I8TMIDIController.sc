MIDIController {

	var <> target;
	var <> name;
	var <> type;
	var <> key;
	var <> ctlNum;
	var <> channel;
	var <> sourceId;
	var <> range;
	var <> protocol;

	var callbacks;

	*new {|target_, sourceId_, type_, ctlNum_, channel_ |
		^super.new.init(target_, sourceId_, type_, ctlNum_, channel_ );
	}

	init {|target_, sourceId_, type_, ctlNum_, channel_ |

		protocol = "midi";

		target = target_;
		sourceId = sourceId_;
		type = type_;
		ctlNum = ctlNum_;
		channel = channel_;

		name = protocol++'_'++sourceId++'_'++type++'_'++ctlNum++'_'++channel;

		callbacks = List.new;


		this.addResponder( type_,  sourceId, ctlNum_, channel_ );


	}

	addResponder {|messageType, sourceId, ctlNum, channel|
		// ["add Responder", ctlNum, channel, sourceId].postln;
		switch(messageType,
			\cc, {

				var func = MIDIdef.cc( (name ++"_"++ctlNum++"_cc").asSymbol,
					{arg ...args;
						// args.postln;
						this.set(args[0])

					}, ctlNum, channel, sourceId
				);
// [func, name, ctlNum, channel, sourceId].postln;
				callbacks.add( func );

			},
			\note, {

				var func;

				func = MIDIdef.noteOn( (name ++ "_noteOn" ).asSymbol,
					{

						arg ...args;

						this.set(args[1],args[0]);

					}, ctlNum, channel, sourceId
				);

				callbacks.add( func );

				func = MIDIdef.noteOff( (name ++ "_noteOff" ).asSymbol,
					{arg ...args;

						this.set(args[1],args[0]);

					}, ctlNum, channel, sourceId
				);

				callbacks.add( func );


			},
			{ "message type not recognized".postln; }
		);

	}



	set {|param1,param2|

		target.set(this, param1, param2);

	}

}
