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

		// if(record.isNil) {
		// 	var blockResult = block {|break|
		// 		this.collect({|v,k|
		// 			if(record.isNil) {
		// 				if(v.isKindOf(I8TFolder)) {
		// 					var valid = (v[key].isKindOf(SynthDef));
		// 					valid = (valid && (v[key].isKindOf(I8TFolder)));
		//
		// 					if(valid) {
		// 						break.value(v[key]);
		// 					};
		// 				};
		// 			};
		// 		});
		// 	};
		//
		// 	if(
		// 		(
		// 			(blockResult.isKindOf(SynthDef))
		// 			||
		// 			(blockResult.isKindOf(I8TFolder))
		// 		)
		// 	) {
		// 		record = blockResult;
		// 	};
		// };


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
			if( (v.isKindOf(I8TFolder)&&(v.hasVariants()==false)) ) {
				folders.add(v);
			};
			if(
				(
					(v.isKindOf(SynthDef)||v.isKindOf(SynthDefVariant))
					||
					(v.isKindOf(I8TFolder)&&(v.hasVariants()==true))
				)
			) {
				items.add(v)
			};

		};

		items.do({|item,index| item.isNil ?? { items.removeAt(index)}});
		folders.do({|folder,index| folder.isNil ?? { folders.removeAt(index)}});

		items = items.asArray.sort({|a,b|this.sortName(a,b)});
		folders = folders.asArray.sort({|a,b|this.sortName(a,b)});

		Task.new({

			folders.do({|folder|
				var folderString = (padding++"> "++" "++folder.name++": "++folder.size ++ " ");

				if( folder.hasVariants() == false ) {
					folderString = folderString ++ "items";
					folderString.postln;
				};


				0.002.wait;

			});

			items.do{|item,index|
				var itemString;

				var thisItemRefs = refs.collect({|v,k| if(v===item, { k }, { nil }); })
				.keys
				.reject(_.isNumber)
				.reject({|k|k.asString=="0"});

				thisItemRefs = thisItemRefs ++ this.keys.reject({|itemKey|this.at(itemKey)!==item}).asArray;

				thisItemRefs=thisItemRefs.asArray.sort.reject(_===item.name).collect(name++"."++_)
				;


				itemString = padding++"······ "++index++": ";
				itemString=itemString++item.name++": ";



				if( item.isKindOf(I8TFolder) ) {
					var variantList = [item.name]++item.keys.asArray.reject(_==item.name).sort;
					thisItemRefs = (item.size.asString ++ " variants: " ++ variantList.asString );
				};

				if( item.isKindOf(SynthDefVariant), {
					var paramString;
					itemString.postln;
					item.parameters.do({|param,paramIndex|
						if( param.notNil ) {

							if((paramIndex%2==0), {
								paramString = padding++"······ "++"       ";
	 							paramString = paramString ++ "\\"++ param;
							}, {
								paramString = paramString ++ ": " ++ param;
								paramString.postln;
								paramString="";
							});
						};
					});
				}, {
					itemString=itemString++thisItemRefs;
					itemString.postln;
				});



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
		^this.getAncestors.detect({|a|a.folderParent.isNil})
	}


	organizeByFamilies {

		this.keysValuesDo({|k,v|
			if(this.getRootFolder.notNil) {
				if(this.getRootFolder[k].isKindOf(I8TFolder)) {

					var parentNameChildren = this.keys.asArray.select({|ck|ck.asString.toLower.contains(name.asString.toLower)})
					++
					this.refs.keys.asArray.select({|ck|ck.asString.toLower.contains(name.asString.toLower)});

					// parentNameChildren.collect({|childName|
					// 	[name,this.makeChildKeyWithoutParent(childName)].postln;
					// });
					if( name.asString == "electro" ) {
						["electropnc",name,parentNameChildren].postln;
					};
					if(parentNameChildren.size>0){
						// ["pnc",name,parentNameChildren].postln;
						// this.getRootFolder[k].keysValuesDo({|rk,rv|
						// 	[rk,rv].postln;
						// });
					};
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

				var  newKey;

				if((k.isNumber||(v.isKindOf(SynthDefVariant)==false)&&(v.isKindOf(SynthDef)==false)&&(v.isKindOf(Event)==false))) {
					this.removeAt(k);
					refs.removeAt(k);
				};
			// if( folderParent.notNil ){
			// 	["parent is ", folderParent.name].postln;
			// };


				newKey = this.makeChildKeyWithoutParent( k );

				if(((newKey!="0")&&(newKey.asInteger>0)), {
					this.ref(("s"++newKey).asSymbol,v);
					this.refInAncestors(("s"++newKey).asSymbol,v);
				},{
					this.ref(newKey,v);
					this.refInAncestors(newKey,v);
					if(this.getRootFolder()[newKey].isKindOf(I8TFolder),{
						this.getRootFolder()[newKey].ref(name.asString.toLower.asSymbol,v);
						this.getRootFolder()[newKey].ref(name.asSymbol,v);
					}, { "doesntexist".warn; [newKey].postln; });
				});


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
				keysToRef.asArray.sort.do({|rk|
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

			numKeys=numKeys.reject(_.isKindOf(I8TFolder)).sort({|a,b| b.name<a.name});
			numKeys.reverse.do({|synthdef,index|
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

					var folderKey = k.asString.replace(name.asString,"").asSymbol;

					synthFolder.name = folderKey;

					synthFolder.put(folderKey,this[k]);
					synthFolder.ref(folderKey.asString.toLower,this[k]);

					this[k].variants.keysValuesDo({|vk,vv|

						var synthDefVariant = SynthDefVariant(
							(this[k].name.asString++"."++vk).asString.asSymbol,
							vv,
							this[k]
						);

						synthFolder.put(vk.asSymbol,synthDefVariant);
						synthFolder.ref(vk.asString.toLower.asSymbol,synthDefVariant);

						this.getRootFolder.ref((this[k].name.asString++"."++vk).toLower.asSymbol,synthDefVariant);
						this.getRootFolder.ref((this[k].name.asString++"."++vk).asSymbol,synthDefVariant);

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
			a.ref(key.asString.toLower.asSymbol,value);
			if(value.isKindOf(I8TFolder)){
				value.keysValuesDo({|vk,vv|
					a.ref(vk,vv);
					a.ref(vk.asString.toLower.asSymbol,vv);
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


	getMainSynthDef {

		var synthDef;


		synthDef = this.detect({|item| item.name==this.name });

		if( synthDef.notNil ) {
			^synthDef
		};

		synthDef = this.detect({|item| item.isKindOf(SynthDef) });

		if( synthDef.notNil ) {
			^synthDef
		};

		synthDef = this.detect({|item| item.isKindOf(SynthDefVariant) });

		^synthDef


	}



	makeChildKeyWithoutParent {|k|
		// add simplified keys that remove this folder's name from any of its child synths (electro.kickElectro becomes electro.kick and kick.electro)
		if(k.isKindOf(String)||k.isKindOf(Symbol)) {

			var newKey = k.asString.toLower;

			this.getAncestors.collect({|ancestor|
				newKey=newKey.replace(ancestor.name.asString.toLower,"");
			});

			^newKey=newKey.replace(name.asString.toLower,"").toLower.asSymbol;

		};

	}




}
