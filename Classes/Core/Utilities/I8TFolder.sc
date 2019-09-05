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

		if(key.asString!="forward"){
			("Key \""++key++"\" not found in folder: "++name).warn;
		};
		^nil

	}

	ref {|key,value|
		^refs.put(key,value);
	}

	list {

		("-------------------------").postln;
		("List: "++name).postln;
		Tdef(\do,{
			this.keysValuesDo{|k,v|
				if( v.isKindOf(I8TFolder) ) {
					("> "++k++v.name++": "++ v.size.postln++ " items:").postln;
					v.list();

				};
				if( v.isKindOf(SynthDef) ) {
					("    - "++" "++k++": "++v.name).postln;
				};
				0.02.wait;
			}
		}).play;

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
					if(kLowerCase.contains(fLowerCase)) {

						var newKey = kLowerCase.replace(fLowerCase,"").asSymbol;

						this.ref(newKey,v);

						if(newKey.asInteger>=0) {
							this.ref(("s"++(newKey.asInteger+1).asString).asSymbol,v);
						}

					};
				};
			});



		};

	}



}
