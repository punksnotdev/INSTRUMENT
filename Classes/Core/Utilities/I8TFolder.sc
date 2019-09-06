I8TFolder : Event
{
	var <> name;
	var <> folderParent;

	var <refs;

	*new {
		^super.new.init;
	}

	init {
		refs=();
	}

	at {|key|

		var record = super.at( key );

		if(record.isNil) {

			var refKey = key;

			if( key.isNumber ) {
				refKey = key%this.size;
			};

			record = refs.at(refKey);

		};

		if( record.notNil ) {
			^record
		};

		if(((name!='root')&&(key.asString!="forward"))){
			("Key \""++key++"\" not found in folder: "++name).warn;
		};
		^nil

	}

	ref {|key,value|
		^refs.put(key,value);
	}

	list {

		arg expand=false,recursive=false;

		var folders=List.new;
		var items=List.new;
		var padding="";

		(this.getAncestors.size-1).max(0).do({padding=padding++"······";});

		("\n"++padding++"-------------------------").postln;
		(padding++"> "++name++": "++ this.keys.size++ " items:").postln;

		this.keysValuesDo{|k,v|
			if( v.isKindOf(I8TFolder) ) {
				folders.add(v);
			};
			if( v.isKindOf(SynthDef) ) {
				items.add(v)
			};

		};

		items.do({|item,index| item.isNil ?? { items.removeAt(index)}});
		folders.do({|folder,index| folder.isNil ?? { folders.removeAt(index)}});

		items = items.asArray.sort({|a,b|this.sortName(a,b)});
		folders = folders.asArray.sort({|a,b|this.sortName(a,b)});

		folders.do({|folder|
			(padding++"> "++" "++folder.name++": "++folder.size ++ " items").postln;
		});
		items.do{|item,index|
			var thisItemRefs = refs.collect({|v,k| if(v===item, { k }, { nil }); })
			.keys
			.reject(_.isNumber)
			.reject({|k|k.asString=="0"});
			thisItemRefs = thisItemRefs ++ this.keys.reject({|itemKey|this.at(itemKey)!==item}).asArray;

			thisItemRefs=thisItemRefs.asArray.sort.reject(_===item.name).collect(name++"."++_)
			;
			(padding++"······ "++index++": "++item.name++": "++thisItemRefs).postln;
		};

		folders.do({|folder|
			if( expand == true ) {
				folder.list(recursive,recursive);
			};
		});
		^"-------------------------"
	}

	tree {|recursive|
		this.list(true,recursive);
		^"-------------------------"
	}

	getParent {
		^folderParent
	}

	getAncestors {

		var nextParent=this.getParent;

		var ancestors = List.new;

		nextParent !? {

			ancestors.add(nextParent.name);

			nextParent.getAncestors.do({ arg a; a !? { ancestors.add(a); }})

		};


		^ancestors;
	}



	organizeByFamilies{|rootFolder|

	  this.keysValuesDo({|k,v|

	    if(v.isKindOf(I8TFolder)) {
	      v.organizeByFamilies(rootFolder);
	    };

	    if(v.isKindOf(SynthDef)) {
	      if(k.isKindOf(Number)==false) {
			if(rootFolder[k].isKindOf(I8TFolder)){
	          rootFolder[k].ref(name,v);
		  	};
	      }
	    };

	  });

	}

	makeIndexes {

		if(name!='root'){

			var synthDefs = List.new;
			var newKeys=List.new;

			// delete numeric indexes
			this.keysValuesDo({|k,v|
				if((k.isNumber||(v.isKindOf(SynthDef)==false)&&(v.isKindOf(Event)==false))) {
					this.removeAt(k);
					refs.removeAt(k);
				};

				this.keys.as(Set).asArray.do({|k|
					if(k.isNumber==false){
						newKeys.add(k);
					}
				});

				newKeys = newKeys.as(Set).asArray.sort;

				newKeys.collect({|k,i|
					this.ref(i,this[k]);
				});

				// add simplified keys that remove this folder name from any synths that include it
				if(k.isKindOf(String)||k.isKindOf(Symbol)) {
					var kLowerCase = k.asString.toLower;
					var fLowerCase = name.asString.toLower;
					if(fLowerCase.contains("neuro")){
						"NEUROOOO".warn;
					};
					if(kLowerCase.contains(fLowerCase)) {

						var newKey = kLowerCase.replace(fLowerCase,"").asSymbol;

						if((newKey!="0"&&newKey.asInteger>0), {
							this.ref(("s"++newKey).asSymbol,v);
						},{
							this.ref(newKey,v);
						});

					};
				};
			});



		};

	}

	sortName {|a,b|
		^(a.name < b.name)
	}

}
