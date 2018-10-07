ModeMatrix : ControllerLogic {

    var < modes;

    var currentMode;

    var functions;
    var currentCallbacks;

    var selectMode;

    *new {
        ^super.new.init();
    }


    // create modeMatrix





    loadMode{|index|

        ["load mode", index].postln;

        // modes.keysValuesDo{|k,v|[k.isKindOf(Integer),v].postln;};

        72.do {|j|
            currentCallbacks[j]=nil;
        }
        8.do{|j|

            modes[j] = ControlMode.new;

            64.do{|k|
                modes[j].addCallback(k,{ "callback!!!".postln; });
            };

        };

        modes[index.asInteger].callbacks.keysValuesDo({|k,v|
            currentCallbacks[k]=(callback:v,parameter: k );
        });

    }

    init {

        currentCallbacks = IdentityDictionary.new;

        functions = IdentityDictionary.new;

        functions[\selectMode] = {|e,index|
            this.loadMode( index );
        };

        modes = IdentityDictionary.new;

            // modeMatrix callback Types

            // modeMatrix

                8.do{|j|

                    modes[j] = ControlMode.new;

                    64.do{|k|
                        modes[j].addCallback(k,{ "callback!!!".postln; });
                    };

                };


            	// modeNavigation

                // load modeDef


                this.setupModeNavigation();






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

        if(currentCallbacks[param1.val].notNil,{

            if(param1.amplitude==1,{

                currentCallbacks[param1.val].callback(
                    currentCallbacks[param1.val].parameter
                );

            });

        });


    }

    send { arg ...args;

        args.postln;

    }


    updateView {

    }

    setupModeNavigation{
        Array.series(8,8,9).collect({|index|

            currentCallbacks[index] = (
                callback: functions[\selectMode],
                parameter: (index/9).floor
            );

        });
    }

}
