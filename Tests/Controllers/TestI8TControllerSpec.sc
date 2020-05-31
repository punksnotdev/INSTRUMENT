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

	test_afterInit_inputMap_hasCorrectValues {
		this.assert(
			(spec.getInputByCtlNum(31).name == specFile.inputs.group2.name)
			&&
			(spec.getInputByCtlNum(31).index == 1 )
		);
	}
	test_afterInit_outputMap_hasCorrectValues {
		this.assert(
			(spec.getOutputByCtlNum(72).name == specFile.outputs.group2.name)
			&&
			(spec.getOutputByCtlNum(72).index == 2 )
		);
	}



}
