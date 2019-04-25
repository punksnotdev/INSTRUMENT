/*
Modes: LoopMatrix

- LoopMatrix Page1
- LoopMatrix Page2
- Volumes
- Rates
- FX
- FXSet x8
- FXSet Detail
-
- Tempo

*/

ModeMatrix : ControllerLogic {
    var name;
    var < modes;
    var <midiTarget;

    var currentMode;
    var currentModeIndex;

    var functions;
    var currentCallbacks;

    var selectMode;

    *new {
        ^super.new.init();
    }


    init {

        name = "ModeMatrix";
        currentCallbacks = IdentityDictionary.new;

        functions = IdentityDictionary.new;


        functions[\selectMode] = {|e,index|

            if( currentModeIndex != index ){



                this.loadMode( index );

                if( midiTarget.isKindOf(MIDIDevice), {


                    8.do{|k|
                        if(((k+1)*9)!=((index+1)*9),{
                            midiTarget.send(8+((k)*9),0);
                            });
                        };

                        midiTarget.send(((index+1)*9)-1,60);

                        // if( currentModeIndex.notNil, {
                        //
                        //     72.do{|j|
                        //         if(j%9<8){
                        //             midiTarget.send(j,0);
                        //         }
                        //     };
                        //
                        // });


                });
            }
        };

        // functions[\test_callback_0] = {|e,param1,param2|
        //
        //     var offset;
        //     var targetKey;
        //
        //     offset = (param1/9).floor;
        //     var offset;
        //     var targetKey;
        //
            // offset = (param1/9).floor;


            //loadCallback.value();
            // 9.do{|l|
        //         if(( (param1%9==(8-l))&&(param1 > (7+(8*l)))),{ offset=offset+1; });
        //     };
        //
        //     targetKey = (param1+offset).asInteger;
        //
        //     midiTarget.send(targetKey,param2*3);
        //
        // };

        modes = IdentityDictionary.new;

        8.do{|j|

            var mode;

            var modeFiles = "/home/furenku/Music/SuperCollider/INSTRUMENT/TmpTests/ManualTesting/featureTests/modeMatrix/modes/mode*.scd".pathMatch;

            mode=modeFiles[j%modeFiles.size].load;

            modes[j] = mode;

        };


        this.setupModeNavigation();

    }





    addMode {|key, mode|

        modes[key] = mode;

    }

    loadMode{|index|

        if( currentMode.isKindOf(ControlMode) ) {
            if( currentMode.destroyCallback.isKindOf(Function) ) {
                currentMode.destroyCallback.value();
            };
        };

        64.do {|j|
            currentCallbacks[j]=nil;
        };

        if( modes[index.asInteger].isKindOf(ControlMode) ) {
            modes[index.asInteger].callbacks.keysValuesDo({|k,v|
                currentCallbacks[k]=(callback:v,parameter: k );
            });
        };

        // modes[index.asInteger].callbacks.keysValuesDo({|k,v|
        //     currentCallbacks[k]=(callback:v,parameter: k );
        // });
        modes[index.asInteger].loadCallback.value();


        currentModeIndex = index.asInteger;

        currentMode = modes[currentModeIndex];

        currentMode.setup();

        ["Mode:",currentMode.name].postln;
    }





    set {|source,param1,param2|

        var key = param1.val.asInteger;

        if(currentCallbacks[key].notNil,{
            currentCallbacks[key].callback(
                currentCallbacks[key].parameter,
                param1.amplitude,
                midiTarget
            );
        }, {
            "no callback".postln;
        });

    }

    send { arg ...args;

        // ["send",args].postln;

    }


    updateView {

    }

    midiTarget_{|target_|
        midiTarget = target_;
        modes.collect({|mode|
            mode.midiTarget = midiTarget;
        });
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
