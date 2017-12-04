Proxy : Instrument
{

	var <proxy;

	*new{|name_,proxy_|
		^super.new.init(name_,proxy_,this.graph);
	}

	init{|name_,proxy_,graph_|
		if( proxy_.isKindOf(NodeProxy), {
			proxy = proxy_;
			this.createSynth();
		});

		super.init(name_,graph_);

	}


	proxy_{|proxy_|

		proxy = proxy_;

		this.createSynth();

		^proxy

	}

	createSynth{

		// proxy.play;

	}

	trigger {|parameter,value|

		if( parameter == \note, {
			proxy.set(\t_trig,1,\note,(octave*12)+value);
		}, {
			proxy.set(parameter.asSymbol,value);
		});


	}

}
