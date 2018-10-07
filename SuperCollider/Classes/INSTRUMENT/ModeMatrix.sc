ModeMatrix : ControllerLogic {

    var < modes;

    *new {
        ^super.new.init();
    }

    init {

        modes = IdentityDictionary.new;

    }

    addMode { |key, mode|

        modes[key] = mode;

    }


    send { arg ...args;

        args.postln;

    }


    updateView {

    }

}
