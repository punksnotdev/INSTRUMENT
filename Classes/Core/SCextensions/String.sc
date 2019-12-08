+ String {

    capitalize {
        ^String.capitalize(this);
    }
    uncapitalize {
        ^String.uncapitalize(this);
    }


    *capitalize {|str|
        var operation={|c|c.toUpper};
        ^this.changeFirst(str,operation);
    }
    *uncapitalize {|str|
        var operation={|c|c.toLower};
        ^this.changeFirst(str,operation);
    }


    *changeFirst {|str,operation|

        if((str.isKindOf(String)||str.isKindOf(Symbol))){
            var newStr = "";
            str.asString.do{|c,i|
                if( i==0 ) {
                    c=operation.value(c);
                };
                newStr = newStr++c;
            };
            if((str.isKindOf(Symbol))){
                newStr = newStr.asSymbol;
            };
            ^newStr;
        };

        ^str

    }



}
