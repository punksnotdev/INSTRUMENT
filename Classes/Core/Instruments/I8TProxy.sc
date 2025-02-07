Proxy : I8TInstrument
{

	var <proxy;
	var amp;

	*new{|proxy_|
		^super.new.init(this.graph,proxy_);
	}

	init{|main_,proxy_|
		if( proxy_.isKindOf(NodeProxy), {
			proxy_.postln;
			proxy = proxy_;
			sequencer = main_.sequencer;
			// this.createSynth();
			super.init(main_,proxy_.key,);
		},{
			"input not a nodeproxy".postln;
		});

	}

	setContent {|proxy_|

		if( proxy_.isKindOf(NodeProxy), {
			proxy = proxy_.proxy;
		})
	}

	proxy_{|proxy_|

		proxy = proxy_;

		^proxy

	}


	trigger {|parameter,event|
		var value = event;
		if( event.val != \r ) {

			if( event.isKindOf(Event)) {
				value = event.val.asFloat;
			};
	        //
			// if( parameter == \note, {
			// 	proxy.set(\t_trig,1,\note,(octave*12)+value);
			// }, {
			// 	proxy.set(parameter.asSymbol,value);
			// });

			switch( parameter.asSymbol,

			// \trigger, {
			// 	proxy.set(\t_trig,1,\amp,value);
			// },
				\octave, { octave = value },
				\note, {
					if( (value >= 0) && (value < 128) ) {
						proxy.set(\t_trig,1,\note,((octave*12)+value).min(128),\freq,((octave*12)+value).min(128).midicps);
					};
				},
				\trigger, {
					proxy.set(\t_trig,1,\amp,value);
				},
				\chord, {
					// ["chord",value].postln;
					proxy.setn(\notes,((octave*12)+value).min(128),\freqs,((octave*12)+value).min(128).midicps,\t_trig,1);
				},
				{ // default:
					proxy.set(parameter.asSymbol,value);
				}
			);

		}

	}


	set {|parameter,value|
		proxy.set( parameter, value );
	}

	amp_ {|value|
		proxy.set( \amp, value );
	}

	amp {|value|
		if( value.notNil ) {
			proxy.set( \amp, value );
		};
		^amp;
	}


}
