ModeMatrix : ControllerLogic {

    var < modes;
    var <>midiTarget;

    var currentMode;
    var currentModeIndex;

    var functions;
    var currentCallbacks;

    var selectMode;

    *new {
        ^super.new.init();
    }


    init {

        currentCallbacks = IdentityDictionary.new;

        functions = IdentityDictionary.new;

        functions[\selectMode] = {|e,index|

            if( midiTarget.isKindOf(MIDIDevice), {


                if( currentModeIndex.notNil, {

                    midiTarget.send(((currentModeIndex+1)*9)-1,3);

                    if( currentModeIndex>0, {

                        72.do{|j|
                            if(j%9<8){
                                midiTarget.send(j,127.rand);
                            }
                        };

                    }, {

                        72.do{|j|
                            if(j%9<8){
                                midiTarget.send(j,0);
                            }
                        };

                    });
                });

                midiTarget.send(((index+1)*9)-1,124);

            });

            this.loadMode( index );

        };

        functions[\test_callback_0] = {|e,param1,param2|

            var offset;
            var targetKey;

            offset = (param1/9).floor;

            9.do{|l|
                if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
            };
            // if(( (param1%9==7)&&(param1 > 15)),{ offset=offset+1; });
            // if(( (param1%9==6)&&(param1 > 23)),{ offset=offset+1; });
            // if(( (param1%9==5)&&(param1 > 31)),{ offset=offset+1; });
            // if(( (param1%9==4)&&(param1 > 39)),{ offset=offset+1; });
            // if(( (param1%9==3)&&(param1 > 47)),{ offset=offset+1; });
            // if(( (param1%9==2)&&(param1 > 55)),{ offset=offset+1; });
            // if(( (param1%9==1)&&(param1 > 62)),{ offset=offset+1; });
            // if(( (param1%9==0)&&(param1 > 70)),{ offset=offset+1; });

            targetKey = (param1+offset).asInteger;
            midiTarget.send(targetKey,param2*3);
            targetKey.postln;
        };
        functions[\test_callback_1] = {|e,param1,param2|
            ["mode", 1, "callback type",1,"functype 2","param",param1,param2].postln;
        };
        functions[\test_callback_2] = {|e,param1,param2|
            ["mode", 2, "callback type",2,"functype 3","param",param1,param2].postln;
        };
        functions[\test_callback_3] = {|e,param1,param2|
            ["mode", 3, "callback type",3,"functype 4","param",param1,param2].postln;
        };
        functions[\test_callback_4] = {|e,param1,param2|
            ["mode", 4, "callback type",4,"functype 5","param",param1,param2].postln;
        };
        functions[\test_callback_5] = {|e,param1,param2|
            ["mode", 5, "callback type",5,"functype 6","param",param1,param2].postln;
        };
        functions[\test_callback_6] = {|e,param1,param2|
            ["mode", 6, "callback type",6,"functype 7","param",param1,param2].postln;
        };
        functions[\test_callback_7] = {|e,param1,param2|
            ["mode", 7, "callback type",7,"functype 8","param",param1,param2].postln;
        };

        modes = IdentityDictionary.new;

            // modeMatrix callback Types

            // modeMatrix

                8.do{|j|

                    modes[j] = ControlMode.new;

                    64.do{|k|
                        modes[j].addCallback(k,functions[(\test_callback_++j).asSymbol]);
                    };



                };


                this.setupModeNavigation();

    }





    addMode { |key, mode|

        modes[key] = mode;

    }

    loadMode{|index|

        64.do {|j|
            currentCallbacks[j]=nil;
        };

        modes[index.asInteger].callbacks.keysValuesDo({|k,v|
            currentCallbacks[k]=(callback:v,parameter: k );
        });

        currentModeIndex = index.asInteger;

        currentMode = modes[currentModeIndex];

    }





    set{|source,param1,param2|

        var key = param1.val.asInteger;

        if(currentCallbacks[key].notNil,{
            currentCallbacks[key].callback(
                currentCallbacks[key].parameter,
                param1.amplitude
            );
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
                parameter: index
            );

        });
    }

}
