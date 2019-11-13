// STAR-CCM+ macro: post_process.java
// Written by STAR-CCM+ 11.04.012
package macro;

import java.util.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class PostProcessing extends StarMacro {

	/** Version 19v2_rad */

	private Simulation simulation;

	/**
	 * 
	 */
	private int clipping;
	private DoubleVector scaleSCp;
	private DoubleVector scaleTCp;
	private DoubleVector scaleVel;
	private DoubleVector scaleVor;


	private double sliceStep;
	private ArrayList<DoubleVector> ranges;

	public void execute() {
		run();
	}

	private void run() {

		clipping = 0;


		sliceStep = 0.05;
		// Maybe change this from -0.1
		// Reference
		ranges = new ArrayList<>();
		ranges.add(new DoubleVector(new double[] { -0.05, 2.95 })); 	// X
		ranges.add(new DoubleVector(new double[] { 0, 0.8 })); 			// Y [in the case of non-symmetry, goes from -0.8]
		ranges.add(new DoubleVector(new double[] { 0, 1.1 })); 			// Z

		// TODO: Number of slices is absolute distance [(last point - first point) / sliceStep]
		// Z doesn't display but it outputs
		/**
		 * Scale variables
		 */
		scaleSCp = new DoubleVector(new double[] { -5, 1 });
		scaleTCp = new DoubleVector(new double[] { -.1, 1 });
		scaleVel = new DoubleVector(new double[] { 0, 30});
		scaleVor = new DoubleVector(new double[] { 0, 1000 });

		// TODO: Fix vector velocities, SCp

		String namePath, simName, figName, mainFolderName;
		String iterCpY, iterCpYName, iterTCpy, iterSCpx, iterVel, iterTCpx, iterVorX;
		String TCpZFolder, TCpYFolder, TCpXFolder;
		String SCpXFolder, SCpYFolder, SCpZFolder;
		String SkinFolder, SCpFolder;
		String VelXFolder, VelYFolder, VelZFolder;
		String VorXFolder;
		String VectVelYFolder, VectVelXFolder, VectVelZFolder;

		String simPath;

		/*
		 * -----------------------------------------------------------------------------
		 * --------------        CREATE FOLDER TO SAVE SIMULATIONS        --------------
		 * -----------------------------------------------------------------------------
		 * 
		 */

		simulation = getActiveSimulation();

		simName = simulation.getPresentationName();
		simPath = simulation.getSessionDir();

		mainFolderName = simPath + "/PostProcessing#" + simName;
		TCpZFolder = mainFolderName + "/TCpZ";
		TCpYFolder = mainFolderName + "/TCpY";
		TCpXFolder = mainFolderName + "/TCpX";
		SCpXFolder = mainFolderName + "/SCpX";
		SCpYFolder = mainFolderName + "/SCpY";
		SCpFolder = mainFolderName + "/SCp";
		SkinFolder = mainFolderName + "/Skin";
		SCpZFolder = mainFolderName + "/SCpZ";
		VelXFolder = mainFolderName + "/VelX";
		VelYFolder = mainFolderName + "/VelY";
		VelZFolder = mainFolderName + "/VelZ";
		VorXFolder = mainFolderName + "/VorX";
		VectVelYFolder = mainFolderName + "/VectVelY";
		VectVelXFolder = mainFolderName + "/VectVelX";
		VectVelZFolder = mainFolderName + "/VectVelZ";

		new File(mainFolderName).mkdir();
		new File(TCpZFolder).mkdir();
		new File(TCpYFolder).mkdir();
		new File(TCpXFolder).mkdir();
		new File(SCpXFolder).mkdir();
		new File(SCpYFolder).mkdir();
		new File(SCpZFolder).mkdir();
		new File(SCpFolder).mkdir();
		new File(VelXFolder).mkdir();
		new File(VelYFolder).mkdir();
		new File(VelZFolder).mkdir();
		new File(VorXFolder).mkdir();
		new File(VectVelYFolder).mkdir();
		new File(VectVelXFolder).mkdir();
		new File(VectVelZFolder).mkdir();

		PlaneSection planeSection = (PlaneSection) simulation.getPartManager().createImplicitPart(
				new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

		Coordinate coordinate_4 = planeSection.getOriginCoordinate();
		Units units_0 = ((Units) simulation.getUnitsManager().getObject("m"));
		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.1, 0.0 }));

		Coordinate coordinate_5 = planeSection.getOrientationCoordinate();
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		simulation.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");
		Scene scalarScene = simulation.getSceneManager().getScene("Scalar Scene 1");
		scalarScene.initializeAndWait();

		PartDisplayer partDisplayer_3 = ((PartDisplayer) scalarScene.getDisplayerManager().getDisplayer("Outline 1"));
		partDisplayer_3.initialize();

		ScalarDisplayer scalarDisplayer_2 = ((ScalarDisplayer) scalarScene.getDisplayerManager().getDisplayer("Scalar 1"));
		scalarDisplayer_2.initialize();
		scalarScene.open(true);

		SceneUpdate sceneUpdate_2 = scalarScene.getSceneUpdate();
		HardcopyProperties hardcopyProperties_2 = sceneUpdate_2.getHardcopyProperties();
		hardcopyProperties_2.setCurrentResolutionWidth(1271);
		hardcopyProperties_2.setCurrentResolutionHeight(587);

		scalarScene.resetCamera();
		scalarScene.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_3 }));
		scalarDisplayer_2.getInputParts().setQuery(null);
		scalarDisplayer_2.getInputParts().setObjects(planeSection);
		

		log(simulation.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_1 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_2 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_3 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));

		log(simulation.getFieldFunctionManager().getFunction("MeanSkinFrictionMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_4 = ((PrimitiveFieldFunction) simulation.getFieldFunctionManager().getFunction("MeanSkinFrinctionMonitor"));

		// log("Scalar shit");
		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		// planeSection.getInputParts().setQuery(null);
		
		log("Boundary check accross all regions");

		int regionId = 0, boundaryIndex = 0;
		Map<Integer, ArrayList<Object>> regionBoundaries = new HashMap<Integer, ArrayList<Object>>();
		
		ArrayList<Boundary> allBoundaries = new ArrayList<Boundary>();
		ArrayList<Region> allRegions = new ArrayList<Region>();
		
		ArrayList<ModelPart> objects = new ArrayList<ModelPart>();

		for (Region region: simulation.getRegionManager().getObjects()) {
			regionBoundaries.put(regionId, new ArrayList<Object>());

			for (Boundary el: region.getBoundaryManager().getObjects()) {
				System.out.println(el.toString() + " ID: " + boundaryIndex);
				regionBoundaries.get(regionId).add(el);
				allBoundaries.add(el);
				objects.add(el);
				boundaryIndex++;
			}
			allRegions.add(region);
			objects.add(region);
			regionId++;
		}

		planeSection.getInputParts().setObjects(objects);

		CurrentView currentView_2 = scalarScene.getCurrentView();
		scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleSCp);
		scalarDisplayer_2.getScalarDisplayQuantity().setClip(clipping);

		SimpleAnnotation simpleAnnotation_1 = simulation.getAnnotationManager().createSimpleAnnotation();
		simpleAnnotation_1.setPresentationName("Figure_name");
		simpleAnnotation_1.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scalarScene
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_1);

		// for (int iterY = 0; iterY < 17; iterY++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0001 + sliceStep * iterY, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + sliceStep * iterY, 0.5 }),
		// 			new DoubleVector(new double[] { 0.6, -50 + sliceStep * iterY, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			
		// 	iterCpY = (iterY > 10) ? String.valueOf(iterY) :  "0" + String.valueOf(iterY);
			
		// 	figName = simName + " " + "y=" + String.format("%.2f", sliceStep * iterY) + "m";
		// 	simpleAnnotation_1.setText(figName);
		// 	namePath = SCpYFolder + "/SCpY_" + iterCpY + ".png";

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleVel);

		// // 0.80 cm
		// for (int iterY = 0; iterY < 17; iterY++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0001 + sliceStep * iterY, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + sliceStep * iterY, 0.5 }),
		// 			new DoubleVector(new double[] { 0.6, -50 + sliceStep * iterY, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterVel = (iterY > 10) ? String.valueOf(iterY) :  "0" + String.valueOf(iterY);
		// 	figName = simName + " " + "y=" + String.format("%.2f", sliceStep* iterY) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	namePath = VelYFolder + "/VelY_" + iterVel + ".png";
		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleTCp);

		// for (int iterY = 0; iterY < 17; iterY++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0001 + sliceStep* iterY, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + sliceStep* iterY, 0.5 }),
		// 			new DoubleVector(new double[] { 0.6, -50 + sliceStep* iterY, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);
			
		// 	iterTCpy = (iterY > 10) ? String.valueOf(iterY) :  "0" + String.valueOf(iterY);
		// 	figName = simName + " " + "y=" + String.format("%.2f", sliceStep* iterY) + "m";
		// 	namePath = TCpYFolder + "/TCpY_" + iterTCpy + ".png";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// Think of a way to generalise

			
		SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation.getTransformManager().getObject("Symmetry 1"));
		
		// if(simulation.getTransformManager().getObject("Symmetry 1") != null || simulation.getTransformManager().getObject("Symmetry") != null) {
		// 	SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation.getTransformManager().getObject("Symmetry 1"));
		// 	scalarDisplayer_2.setVisTransform(symmetricRepeat_0);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleTCp);
		// coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		// for (int iterX = -1; iterX < 0; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep* iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep* iterX) + "m";
		// 	namePath = TCpXFolder + "/TCpX__" + iterTCpx + ".png";

		// 	simpleAnnotation_1.setText(figName);
		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterX = 0; iterX < 60; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep* iterX) + "m";
		// 	namePath = TCpXFolder + "/TCpX_" + iterTCpx + ".png";

		// 	simpleAnnotation_1.setText(figName);
		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleSCp);

		// for (int iterX = -1; iterX < 0; iterX++) {  

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterSCpx = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	namePath = SCpXFolder + "/SCpX__" + iterSCpx + ".png";

		// 	simpleAnnotation_1.setText(figName);
		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterX = 0; iterX < 60; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterSCpx = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	namePath = SCpXFolder + "/SCpX_" + iterSCpx + ".png";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleVel);

		// for (int iterX = -1; iterX < 0; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterSCpx = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	namePath = VelXFolder + "/VelX__" + iterSCpx + ".png";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterX = 0; iterX < 60; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterSCpx = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	namePath = VelXFolder + "/VelX_" + iterSCpx + ".png";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleVor);

		// for (int iterX = -1; iterX < 0; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterVorX = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	namePath = VorXFolder + "/VorX__" + iterVorX + ".png";

		// 	simpleAnnotation_1.setText(figName);
		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterX = 0; iterX < 60; iterX++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.0 }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { -0.85 + sliceStep * iterX, 0.0, 0.5 }),
		// 			new DoubleVector(new double[] { -40 - 0.8 + sliceStep * iterX, 0.0, 1.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterVorX = (iterX > 10) ? String.valueOf(iterX) :  "0" + String.valueOf(iterX);
		// 	namePath = VorXFolder + "/VorX_" + iterVorX + ".png";
		// 	figName = simName + " " + "x=" + String.format("%.2f", sliceStep * iterX) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleTCp);
		// coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.1 }));

		// for (int iterZ = 0; iterZ < 6; iterZ++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
		// 			new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterZ > 10) ? String.valueOf(iterZ) :  "0" + String.valueOf(iterZ);

		// 	namePath = TCpZFolder + "/TCpZ_" + iterTCpx + ".png";
		// 	figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterZ = 6; iterZ < 27; iterZ++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep * (iterZ - 4) }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * (iterZ - 4) }),
		// 			new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterZ > 10) ? String.valueOf(iterZ) :  "0" + String.valueOf(iterZ);
		// 	namePath = TCpZFolder + "/TCpZ_" + iterTCpx + ".png";
		// 	figName = simName + " " + "z=" + String.format("%.3f", sliceStep * (iterZ - 4)) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleSCp);

		// for (int iterZ = 0; iterZ < 6; iterZ++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
		// 			new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterZ > 10) ? String.valueOf(iterZ) :  "0" + String.valueOf(iterZ);
		// 	namePath = SCpZFolder + "/SCpZ_" + iterTCpx + ".png";
		// 	figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterZ = 6; iterZ < 27; iterZ++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep * (iterZ - 4) }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * (iterZ - 4) }),
		// 			new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);
			
		// 	iterTCpx = (iterZ > 10) ? String.valueOf(iterZ) :  "0" + String.valueOf(iterZ);
		// 	namePath = SCpZFolder + "/SCpZ_" + iterTCpx + ".png";
		// 	figName = simName + " " + "z=" + String.format("%.3f", sliceStep * (iterZ - 4)) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(scaleVel);

		// for (int iterZ = 0; iterZ < 6; iterZ++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
		// 			new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterZ > 10) ? String.valueOf(iterZ) :  "0" + String.valueOf(iterZ);
		// 	namePath = VelZFolder + "/VelZ_" + iterTCpx + ".png";
		// 	figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// for (int iterZ = 6; iterZ < 27; iterZ++) {

		// 	coordinate_4.setCoordinate(units_0, units_0, units_0,
		// 			new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep * (iterZ - 4) }));
		// 	scalarScene.setMeshOverrideMode(0);
		// 	currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep * (iterZ - 4) }),
		// 			new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
		// 			new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// 	iterTCpx = (iterZ > 10) ? String.valueOf(iterZ) :  "0" + String.valueOf(iterZ);
		// 	namePath = VelZFolder + "/VelZ_" + iterTCpx + ".png";
		// 	figName = simName + " " + "z=" + String.format("%.3f", sliceStep* (iterZ - 4)) + "m";
		// 	simpleAnnotation_1.setText(figName);

		// 	scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		// }

		// Scales toggles
		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		// scalarDisplayer_2.getInputParts().setQuery(null);

		// // Magic numbers defined by the previous cfd macro guy, corresponding to the stuff he wanted to add
		scalarDisplayer_2.getInputParts().setObjects(planeSection
		// 	regionBoundaries.get(0)
		);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));
		// scalarDisplayer_2.getScalarDisplayQuantity().setClip(clipping);

		// coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
		// 		new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
		// 		1);

		// figName = simName + " " + " " + " Side";
		// simpleAnnotation_1.setText(figName);
		// namePath = SCpFolder + "/SCp_" + "Side" + ".png";
		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));
		// scalarScene.setMeshOverrideMode(0);

		// currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
		// 		new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
		// 		1);

		// namePath = SCpFolder + "/SCp_" + "Top" + ".png";
		// figName = simName + " " + "Top";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// namePath = SCpFolder + "/SCp_" + "Bottom" + ".png";
		// figName = simName + " Bottom";

		// simpleAnnotation_1.setText(figName);
		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
		// 		new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// namePath = SCpFolder + "/SCp_" + "Front" + ".png";
		// figName = simName + " Front";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { -1.0, 0.0, 0.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// namePath = SCpFolder + "/SCp_" + "Rear" + ".png";
		// figName = simName + " Rear";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_4);
		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		// scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0.0, 0.05 }));
		// scalarDisplayer_2.getScalarDisplayQuantity().setClip(clipping);
		// coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);

		// currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
		// 		new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
		// 		1);

		// figName = simName + " " + " Side";
		// simpleAnnotation_1.setText(figName);
		// namePath = SkinFolder + "/Skin_" + "Side" + ".png";

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
		// 		new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
		// 		1);

		// namePath = SkinFolder + "/Skin_" + "Top" + ".png";
		// figName = simName + " " + "Top";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		// namePath = SkinFolder + "/Skin_" + "Bottom" + ".png";
		// figName = simName + " Bottom";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));
		// scalarScene.setMeshOverrideMode(0);
		// currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
		// 		new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		// namePath = SkinFolder + "/Skin_" + "Front" + ".png";
		// figName = simName + " Front";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		// scalarScene.setViewOrientation(new DoubleVector(new double[] { 1.0, 0.0, 0.0 }),
		// 		new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		// namePath = SkinFolder + "/Skin_" + "Rear" + ".png";
		// figName = simName + " Rear";
		// simpleAnnotation_1.setText(figName);

		// scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		simulation.getSceneManager().createVectorScene("Vector Scene", "Outline", "Vector");
		Scene vectorScene = simulation.getSceneManager().getScene("Vector Scene 1");
		vectorScene.initializeAndWait();

		PartDisplayer partDisplayer_4 = ((PartDisplayer) vectorScene.getDisplayerManager().getDisplayer("Outline 1"));
		VectorDisplayer vectorDisplayer_4 = ((VectorDisplayer) vectorScene.getDisplayerManager().getDisplayer("Vector 1"));
		vectorDisplayer_4.initialize();

		vectorScene.open(true);

		vectorDisplayer_4.setDisplayMode(1);
		vectorDisplayer_4.getInputParts().setQuery(null);
		vectorDisplayer_4.getInputParts().setObjects(planeSection);
		vectorDisplayer_4.getVectorDisplayQuantity().setClip(clipping);

		UserFieldFunction userFieldFunction_4 = ((UserFieldFunction) simulation.getFieldFunctionManager()
				.getFunction("Mean of Velocity"));

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);
		vectorDisplayer_4.getVectorDisplayQuantity().setRange(scaleVel);
		vectorScene.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_4 }));

		CurrentView currentView_3 = vectorScene.getCurrentView();
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		SimpleAnnotation simpleAnnotation_2 = simulation.getAnnotationManager().createSimpleAnnotation();
		simpleAnnotation_2.setPresentationName("Figure_name");
		simpleAnnotation_2.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_2 = (FixedAspectAnnotationProp) vectorScene
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_2);

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);
		vectorDisplayer_4.getVectorDisplayQuantity().setRange(scaleVel);
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + sliceStep* iterY, 0.0 }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + sliceStep* iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + sliceStep* iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);
			figName = simName + " " + "y=" + String.format("%.2f", sliceStep* iterY) + "m";
			simpleAnnotation_2.setText(figName);
			namePath = VectVelYFolder + "/VectVelY_" + iterVel + ".jpg";

			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			vectorScene.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);
			iterTCpx = String.valueOf(iterZ);

			namePath = VectVelZFolder + "/VectVelZ_" + iterTCpx + ".jpg";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_2.setText(figName);
			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + sliceStep* (iterZ - 4) }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + sliceStep* (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = VectVelZFolder + "/VectVelZ_" + iterTCpx + ".jpg";
			figName = simName + " " + "z=" + String.format("%.3f", sliceStep* (iterZ - 4)) + "m";
			simpleAnnotation_2.setText(figName);

			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);
		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);
		vectorDisplayer_4.getVectorDisplayQuantity().setRange(scaleVel);
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.0 }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + sliceStep* iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", sliceStep* iterX) + "m";
			namePath = VectVelXFolder + "/VectVelX__" + iterVel + ".jpg";
			simpleAnnotation_2.setText(figName);
			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.0 }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + sliceStep* iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + sliceStep* iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", sliceStep* iterX) + "m";
			namePath = VectVelXFolder + "/VectVelX_" + iterVel + ".jpg";
			simpleAnnotation_2.setText(figName);

			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		log("Finished.");
	}

	private void log(Object x) {
		/** A logging utility for debugging and additional information. Needs initialization beforehand */
		System.out.println("[LOG]: " + x); // This logs in the terminal if you've run STAR-CCM through shell.
		simulation.println("[LOG]: " + x); // This logs in the output window in the program itself.
	}
}