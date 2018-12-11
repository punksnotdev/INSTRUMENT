ControlMode {

    var <callbacks;

    *new {
        ^super.new.init();
    }


    init {
        callbacks = IdentityDictionary.new;
    }

    setup {|callback|
        callback.value();

    }

    addCallback{|n,callback|

        callbacks[n] = callback;

    }


}
