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
				refKey = key%(this.reject(_.isKindOf(I8TFolder)).size);

			};
			record = refs.at(refKey);

		};

		if(record.isNil) {
			var blockResult = block {|break|
				this.collect({|v,k|
					if(record.isNil) {
						if(v.isKindOf(I8TFolder)) {
							var valid = (v[key].isKindOf(SynthDef));
							valid = (valid && (v[key].isKindOf(I8TFolder)));

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
	//
	// 	if(((value.isKindOf(SynthDef))||(value.isKindOf(I8TFolder))), {
	// 		^super.put(key,value);
	// 	}, {
	// 		"I8TFolder: value must be SynthDef or I8TFolder type"
	// 	});
	//
	// }

	ref {|key,value|
		if( (refs.at(key).isNil||refs.at(key).isKindOf(SynthDef)) ) {
			^refs.put(key,value);
		};
	}

	list {

		arg expand=false,recursive=false;

		var folders=List.new;
		var items=List.new;
		var padding="";
		var totalItems=this.keys.size;
		this.do({|v|
			if( v.isKindOf(I8TFolder) ) {
				totalItems = totalItems + v.size;
			}
		});
		(this.getAncestors.size-1).max(0).do({padding=padding++"······";});

		("\n"++padding++"-------------------------").postln;
		(padding++"> "++name++": "++ totalItems ++ " items:").postln;

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
		Task.new({

			folders.do({|folder|
				(padding++"> "++" "++folder.name++": "++folder.size ++ " items").postln;
				0.002.wait;
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
				0.002.wait;
			};

		}).play;

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


	getRootFolder {
		^this.getAncestors.reject({|a|a.folderParent.notNil})[0]
	}

	organizeByFamilies{
		this.keysValuesDo({|k,v|
			if(this.getRootFolder.notNil) {
				if(this.getRootFolder[k].isKindOf(I8TFolder)) {
					if(v.isKindOf(SynthDef)) {
						this.getRootFolder[k].put(name,v);
					}
				}
			}
		})
	}

	makeRefs {

		if(name!='root'){

			var synthDefs = List.new;
			var newKeys=List.new;
			var numKeys=List.new;
			var keysOrder=Event();

			var keysToRef = this.keys;

			// delete numeric indexes
			this.keysValuesDo({|k,v|

				if((k.isNumber||(v.isKindOf(SynthDefVariant)==false)&&(v.isKindOf(SynthDef)==false)&&(v.isKindOf(Event)==false))) {
					this.removeAt(k);
					refs.removeAt(k);
				};
				// if( folderParent.notNil ){
				// 	["parent is ", folderParent.name].postln;
				// };



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


			// generate numeric indexes:


			// if this folder includes variants,
			if( this.hasVariants(), {
				// it should include a synthdef with the same name as this folder.
				// make that the first index

				this.keysValuesDo({|k,v|
					if( ((this.at(name).notNil) && (k===name))) {
						numKeys.add(v);
						keysToRef.remove(name);
					};
				});

				// then add other indexes alphabetically
				keysToRef.asArray.do({|rk|
					if(rk.isKindOf(Symbol)){
						if( (this.at(rk).isKindOf(SynthDef)||this.at(rk).isKindOf(SynthDefVariant))) {
							if(numKeys.includes(this.at(rk))==false) {
								numKeys.add(this.at(rk));
							}
						}
					}
				});


			}, {

				this.keysValuesDo({|k,v|

					keysToRef.asArray.sort.do({|rk|
						if(rk.isKindOf(Symbol)){
							if( (this.at(rk).isKindOf(SynthDef)||this.at(rk).isKindOf(SynthDefVariant))) {
								if(numKeys.includes(v)==false) {
									numKeys.add(v);
								}
							}
						}
					});

				});

			});


			numKeys.do({|synthdef,index|
				this.ref(index,synthdef);
			});


		};

	}

	addVariants {
		// add variants information
		this.keysValuesDo({|k,v|
			if(this[k].isKindOf(SynthDef)) {
				if( this[k].variants.notNil ) {

					var synthFolder = I8TFolder();

					var folderKey = k.asString.replace(name.asString,"").toLower.asSymbol;

					synthFolder.name = folderKey;

					synthFolder.put(folderKey,this[k]);

					this[k].variants.keysValuesDo({|vk,vv|

						var synthDefVariant = SynthDefVariant(
							(this[k].name.asString++"."++vk).asString.asSymbol,
							vv,
							this[k]
						);

						synthFolder.put(vk.asString.toLower.asSymbol,synthDefVariant);

						this.getRootFolder.ref((this[k].name.asString++"."++vk).toLower.asSymbol,synthDefVariant);

					});


					synthFolder.folderParent = this;

					this.refInAncestors(k,synthFolder);
					// synthFolder.addVariants();
					synthFolder.makeRefs();

					this[folderKey]=synthFolder;

				}
			}
		});
	}


	refInAncestors {|key,value|
		this.getAncestors().do({|a|
			a.ref(key,value);
			if(value.isKindOf(I8TFolder)){
				value.keysValuesDo({|vk,vv|
					a.ref(vk,vv);
				});
			};
		});

	}

	sortName {|a,b|
		^(a.name < b.name)
	}

	hasVariants {

		var result = false;

		block{|break|
			this.do({|v|
				if(v.isKindOf(SynthDefVariant)) {
					result = true;
					break.value(true)
				}
			})
		};

		^result;

	}

}
