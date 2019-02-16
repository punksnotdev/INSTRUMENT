Proxy : Instrument
{

	var <proxy;
	var amp;

	*new{|proxy_|
		^super.new.init(this.graph,proxy_);
	}

	init{|graph_,proxy_|
		if( proxy_.isKindOf(NodeProxy), {
			"input is a nodeproxy".postln;
			proxy_.postln;
			proxy = proxy_;
			this.createSynth();
			super.init(graph_,proxy_.key,);
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
					proxy.set(\t_trig,1,\note,(octave*12)+value,\freq,((octave*12)+value).midicps);
				},
				\trigger, {
					proxy.set(\t_trig,1,\amp,value);
				},
				\chord, {
					["chord",value].postln;
					proxy.setn(\notes,(octave*12)+value,\freqs,((octave*12)+value).midicps,\t_trig,1);
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
