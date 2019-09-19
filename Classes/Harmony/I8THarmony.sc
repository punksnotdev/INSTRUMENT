I8THarmony {

    *new {
        ^super.new.init;
    }

    init {
    }

    getDirection {|array,step|
        // returns 1 for increase, 0 for no movement, -1 for decrease
        var direction = 0;

        if( (array.isKindOf(List)||array.isKindOf(Array)), {

            if( (step > 0) && (step < array.size), {

                var lastValue = array[ step - 1 ];
                var thisValue = array[ step ];

                direction = (thisValue - lastValue).max(-1).min(1);

            }, {

                "getDirection: Step out of range".warn;

            });

        }, {

            "getDirection: Not a valid Array".warn;

        });

        ^direction;

    }


    getInterval {|a,b|
        if(a<b, {
            ^b-a
        }, {
            ^a-b
        });
    }

    checkParallel {|array1,array2,step|

        var isParallel = false;

        if( ( step<1) || (step >= array1.size) || (step >= array2.size ), {
            "checkParallel: step out of range".warn;
        }, {

            var lastInterval = array1[step-1] - array2[step-1];
            var thisInterval = array1[step] - array2[step];

                if( (thisInterval == lastInterval) ) {
                    isParallel = true;
                    ("is Parallel"++thisInterval.asString).warn;
                };

        });

        ^isParallel;

    }


    generateVoicings {|numVoices=2|

        var voices = List.new;
        var scale = Scale.minor;

        numVoices.do({
            voices.add(List.new);
        });

        16.do{|step|

            var newVoice1;
            var rejectParallel = [ 4, 7 ];
            var direction;
            var newVoice2;

            newVoice1 = 7.rand;

            voices[0].add(newVoice1);

            if( step > 0, {

                var baseNote = 7.rand;

                direction = this.getDirection(voices[0],step);

                // TODO: implement smart decrement

                newVoice2 = baseNote - ( (6-baseNote).rand*(direction*(-1)));
                newVoice2 = newVoice2.max(0);

                while({
                    (
                        this.checkParallel(voices[0],voices[1]++newVoice2, step)
                        &&
                        (   // check for parallel 5ths and 8ths
                            (this.getInterval(voices[0][step],newVoice2)==4)
                            &&
                            (this.getInterval(voices[0][step],newVoice2)==7)
                        )
                    )
                }, {

                    // newVoice2 = 7.rand*(this.getDirection(voices[0],step)*1).max(0);
                    newVoice2 = 7.rand;
                    newVoice2.postln;

                });


            }, {

                newVoice2 = 7.rand;

            });

            voices[1].add(newVoice2.abs);

        };

        ^voices

    }

}
