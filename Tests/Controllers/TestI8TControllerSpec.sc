TestI8TControllerSpec : I8TUnitTest
{

	var specFile;
	var spec;

	setUp {

		specFile = (this.class.filenameSymbol.asString.dirname ++ "/testSpec.scd").load;

		spec = I8TControllerSpec.new(specFile);


	}

	test_afterInit_hasInputs {
		this.assert( spec.inputs == specFile.inputs );
	}
	test_afterInit_hasOutputs {
		this.assert( spec.outputs == specFile.outputs );
	}




}
