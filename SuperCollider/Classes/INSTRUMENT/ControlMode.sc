ControlMode {

    var <callbacks;

    *new {
        ^super.new.init();
    }


    init {
        callbacks = IdentityDictionary.new;
    }

    addCallback{|n,callback|

        callbacks[n] = callback;
        
    }


}
