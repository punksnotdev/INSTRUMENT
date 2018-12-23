(

s.boot;
s.doWhenBooted({

	i=INSTRUMENT();
	i[\kick]=SynthPlayer(\kickDeep);
	i[\kick].seq("1 :0.5 1xx ");
	i[\kick].seq("1:2 :0.5 1");
	i[\kick][0].seq("1:0.5");
	i[\kick][1].seq("1xx:0.25");
	i[\kick][2].seq(":0.125 1xx ");

});

)

// remove individually

i[\kick].rm(\trigger,2);
i[\kick].rm(\trigger,0);
i[\kick].rm(\trigger,1);


// remove all

i[\kick][0].seq("1:0.5");
i[\kick][1].seq("1xx:0.25");
i[\kick][2].seq(":0.125 1xx ");

i[\kick].rm(\trigger);
