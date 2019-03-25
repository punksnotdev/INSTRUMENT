ControlMode {

    var <>loadCallback;
    var <callbacks;
    var <name;
    var <>midiTarget;

    *new {|name_|
        ^super.new.init(name_);
    }


    init {|name_|
        name = name_;
        callbacks = IdentityDictionary.new;
    }

    setup {|callback|
        callback.value();

    }

    addCallback{|n,callback|

        callbacks[n] = callback;

    }


}
