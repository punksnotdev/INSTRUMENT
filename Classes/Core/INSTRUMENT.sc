INSTRUMENT {
    classvar <>instance;

    *new {|server,createNew=false|
        instance = super.new.init(server,createNew);
        ^instance;
    }

    init {|server=nil,createNew=false|
		^I8TMain(server,createNew);
    }

}
