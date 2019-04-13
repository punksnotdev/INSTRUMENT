ControlMode {

    var <>loadCallback;
    var <>destroyCallback;
    var <callbacks;
    var <name;
    var <>midiTarget;
    var <>data;

    *new {|name_|
        ^super.new.init(name_);
    }


    init {|name_|
        name = name_;
        callbacks = IdentityDictionary.new;
        data = ();
    }

    setup {|callback|
        callback.value();
    }

    addCallback{|n,callback|

        callbacks[n] = callback;

    }


}
