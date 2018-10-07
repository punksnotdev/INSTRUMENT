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

        64.do {|j|
            currentCallbacks[j]=nil;
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
                        modes[j].addCallback(k,{|param1,param2| ["callback!!!",param1,param2].postln; });
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

        var key = param1.val.asInteger;

        if(currentCallbacks[key].notNil,{

            if(param1.amplitude==1,{

            currentCallbacks[key].callback(
                currentCallbacks[key].parameter,
                param1.amplitude
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
        8.do({|index|

            currentCallbacks[64+index] = (
                callback: functions[\selectMode],
                parameter: (index/9).floor
            );

        });
    }

}
