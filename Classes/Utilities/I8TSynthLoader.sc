I8TSynthLoader {

    classvar <>synthsLoaded;

    var <synths;

    *new {
        ^super.new;
    }

    loadSynths {|path, parent|


    	if( synthsLoaded.isNil, {

    		var files;
    		var folder;
    		// var level;
    		var scdFiles = List.new;
    		var folders = List.new;

    		var items = ();

    		folder = I8TFolder();

    		if( path.notNil, {
    			files = path.pathMatch;
    			folder.name=PathName(path).folderName;
    		}, {
    			files = (Platform.userExtensionDir++"/INSTRUMENT/Sounds/SynthDefs/*").pathMatch;
    			folder.name='root';
    		});



    		files.collect({|fileName, index|

    			var pathName = PathName( fileName );

    			if( pathName.isFile ) {
    				scdFiles.add( fileName );
    			};

    			if( pathName.isFolder ) {
    				folders.add( fileName );
    			};


    		});



    		scdFiles.collect({|fileSrc, index|
    			var pathName = PathName( fileSrc );

    			var fileName = pathName.fileNameWithoutExtension;

    			var synthdef = fileSrc.load;

    			if( synthdef.isKindOf(SynthDef) ) {
    				items[ synthdef.name.asString.replace(" ","_").asSymbol ] = synthdef;
    			};
    		});

    		folders.collect({|folderSrc, index|

    			var pathName = PathName( folderSrc );

    			var folderName = pathName.folderName.replace(" ","_").asSymbol;

    			// "-------".postln;
    			// folderName.postln;
    			// "-------".postln;

    			items[folderName]=this.loadSynths( folderSrc++"*", folder );
    			items[folderName].name = folderName;
    			items[folderName].folderParent = folder;

    		});



    		items.keysValuesDo({|k,v| folder[k]=v; });


    		Task.new({
    			0.1.wait;
    			folder.refInAncestors(folder.name, folder);
    			// 0.1.wait;
    			// folder.organizeByFamilies();
    			0.1.wait;
    			folder.addVariants();
    			0.2.wait;
    			folder.makeRefs();
    		}).play;

    		if( parent.isNil ) {
                synths = folder;
                I8TSynthLoader.synthsLoaded = synths;
    		};

            ^folder


		}, {

            synths = I8TSynthLoader.synthsLoaded;

        });

	}



	synths_ {|list|


		var newList=List.new;
		var counter = 0;

		// store synths dictionary
		synths = list;


		// convert to array for displaying in a list
		synths.keysValuesDo({|k,v|

			if( v.isKindOf(Event)) {
				counter = counter + 1;
				newList.add(k);
				counter = counter + 1;

				v.keysValuesDo({|key,value|
					newList.add(value);
					counter = counter + 1;
				});
			}
		});

	}



    listSynths {|item|

		if( item.isNil ) {
			this.listSynths(synths);
		};

		if( item.isKindOf(Collection) ) {
			item.collect({|value,key|
				"".postln;
				"---------------".postln;
				key.postln;
				"---------------".postln;
				value.keysValuesDo({|k,v|
					v.postln;
				});
				// ([value, key, value[key]]).postln;
				// this.listSynths(value[key]);
			});
		};

	}





    getSynthDefByName {|synthName|

		var synthdef;

		synthdef = synths[synthName.asString.asSymbol];

		if( synthdef.isNil ) {
			synthdef = synths[synthName.asSymbol];
		};


		if(
			(
				synthdef.isNil
				||
				(
					synthdef.isKindOf(Event)
					&&
					(synthdef.isKindOf(SynthDefVariant)==false)
				)
			)
		) {
			synthdef = SynthDescLib.default.at(synthName.asSymbol);
		};

		^synthdef


	}

    getFolderByName {|folderName|

		var folder;

        if( synths.isKindOf(Dictionary)){

            folder = synths[folderName.asSymbol];
    		if( folder.isKindOf(I8TFolder) == false ) {
                folder = synths[folderName.asString.toLower.asSymbol];
    		};

        };

        if( folder.isKindOf(I8TFolder) == false ) {
            ^nil
        };

        ^folder

	}



	validateSynthName{|synthName|

		var synthdef;

		if( synthName.isKindOf(String) || synthName.isKindOf(Symbol) ) {

			^this.getSynthDefByName(synthName).notNil

		};

		^false;
	}

	validateFolderName{|folderName|

		if( folderName.isKindOf(String) || folderName.isKindOf(Symbol) ) {

            var folder = this.getFolderByName(folderName);

			^folder.notNil

		};

		^false;

	}


	validateSynthDef {|synthdef|

		var isValid = true;
		var outputs;

		var isSynth = (
			(
				synthdef.isKindOf(SynthDef)
				||
				synthdef.isKindOf(SynthDesc)
				||
				synthdef.isKindOf(SynthDefVariant)
			)


		);

		if( (
				(isSynth == false) &&
				synthdef.isKindOf(Dictionary)
			)
		) {
			isSynth = (
				synthdef.values.select(_.isKindOf(SynthDef)||_.isKindOf(SynthDefVariant)).size > 0
			);
		};




		if( isSynth == false) {
			isValid=false;
			^isValid;
		};

		if( synthdef.isKindOf(SynthDef) ) {

			outputs = SynthDescLib.global[synthdef.name.asSymbol].outputs;

			if(outputs.size>1) {
				// TODO: do not add multichannel synths?
				// isValid = false;
				// ("SynthDef "++ synthdef.name ++" has more than 1 output: Total " ++ outputs.size ).warn;
			}

		};

		^isValid;
	}




}
