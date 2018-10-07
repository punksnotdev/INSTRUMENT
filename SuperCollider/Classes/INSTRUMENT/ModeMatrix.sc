ModeMatrix : ControllerLogic {

    var < modes;

    var currentMode;

    var callbacks;

    var selectMode;

    *new {
        ^super.new.init();
    }


    // create modeMatrix







    init {
        callbacks = IdentityDictionary.new;
        modes = IdentityDictionary.new;



            // modeMatrix callback Types

            // modeMatrix

            	// modeNavigation

                // load modeDef

                selectMode = {|index|
                    // currentMode = modes[index];
                };

                Array.series(8,8,9).collect({|index|

                    callbacks[index] = (
                        callback: selectMode,
                        parameter: (index/9).floor
                    );

                });


            	// modes
            		// mixer
            			// 8 channels
                        // load modeDef
    }

    addMode { |key, mode|

        modes[key] = mode;

    }



    set{|source,param1,param2|

        [source,param1,param2].postln;
        currentMode = param1;
        param1.postln;
    }

    send { arg ...args;

        args.postln;

    }


    updateView {

    }

}
