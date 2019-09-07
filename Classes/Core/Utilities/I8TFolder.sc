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

		if(record.isNil) {
			var blockResult = block {|break|
				this.collect({|v,k|
					if(record.isNil) {
						if(v.isKindOf(I8TFolder)) {
							var valid = (
								(v[key].isKindOf(SynthDef))
								||
								(v[key].isKindOf(I8TFolder))
							);
							if(valid) {
								break.value(v[key]);
							};
						};
					};
				});
			};

			if(
				(
					(blockResult.isKindOf(SynthDef))
					||
					(blockResult.isKindOf(I8TFolder))
				)
			) {
				record = blockResult;
			};
		};


		if( record.notNil ) {
			^record
		};

		if(((name!='root')&&(key.asString!="forward"))){
			("Key \""++key.asString++"\" not found in folder: "++name.asString).warn;
		};

		^nil

	}

	// put {|key,value|

		// if(((value.isKindOf(SynthDef))||(value.isKindOf(I8TFolder))), {
		// 	^super.put(key,value);
		// }, {
		// 	"I8TFolder: value must be SynthDef or I8TFolder type"
		// });

	// }

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

			ancestors.add(nextParent);

			nextParent.getAncestors.do({ arg a; a !? { ancestors.add(a); }})

		};


		^ancestors;
	}



	organizeByFamilies{
	/*
	For each key in this event,
	looks for a same-named folder inside ancestor folders.

	For each found folder, adds the value  associated with key in that folder
	*/

	  this.keysValuesDo({|k,v|

	    if(v.isKindOf(SynthDef)) {
	      if(k.isKindOf(Number)==false) {
			  this.getAncestors().do({|a|
				  if(a[name].isKindOf(I8TFolder)){
					  a[name].ref(name,v);
				  };
			  })
	      }
	    };

		// if(v.isKindOf(I8TFolder)) {
		// 	v.organizeByFamilies();
		// };

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
					if(k.isKindOf(Symbol)){
						newKeys.add(k);
					}
				});

				newKeys = newKeys.as(Set).asArray.sort;

				newKeys.collect({|k,i|
					this.ref(i,this[k]);
					this.refInAncestors(i,this[k]);
				});

				// add simplified keys that remove this folder name from any synths that include it
				if(k.isKindOf(String)||k.isKindOf(Symbol)) {

					var newKey = k.asString;

					this.getAncestors.collect({|value|
						newKey=newKey.replace(value.name.asString.toLower,"");
					});

					newKey=newKey.replace(name.asString.toLower,"");

					if(((newKey!="0")&&(newKey.asInteger>0)), {
						this.ref(("s"++newKey).asSymbol,v);
						this.refInAncestors(("s"++newKey).asSymbol,v);
					},{
						this.ref(newKey.asSymbol,v);
						this.refInAncestors(newKey.asSymbol,v);
					});

				};
			});



		};

	}


	refInAncestors {|key,value|
		this.getAncestors().collect({|a|
			a.ref(key,value);
		});
	}

	sortName {|a,b|
		^(a.name < b.name)
	}

}
