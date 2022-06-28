# Anonymizer Runtime for Java
This is the java runtime code to be supplied as a maven dependency to the generated code as in project ts-ano-gen.

### Custom
The src/custom/java contains transformations, conversions, distributions which are not included in the main runtime because they are considered "special case".

### Test
The src/test/java contains the generated code for the ***nb.ano*** model with the addition of the custom Person_NO transformation.

* Eclipse - use the RuntimeTest.launch
* Other - Find Anonymizer.java - run that!

The test opens the console
1. ping to see csv in/out config
2. run to anonymize to output directory 
