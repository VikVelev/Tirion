./Tirion/src/                                                                                       0000755 0001750 0001731 00000000000 13551003641 012672  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/main/                                                                                  0000755 0001750 0001731 00000000000 13550751402 013622  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/main/MainMacro.java                                                                    0000644 0001750 0001731 00000015102 13550755211 016334  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  // STAR-CCM+ macro: MainMacro.java
// Last Modified by Viktor Velev (Formula Student Team Delft)
package macro;

import java.util.*;
import java.net.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
 
import star.common.*;
import star.coupledflow.*;
import star.base.neo.*;
import star.flow.*;
import star.meshing.*;
import java.nio.file.Paths;

/** 
 * Brace yourself. You will see some dark magic 
 * JVM tricks to be able to modulize
 * the whole framework while working with STAR-CCM 
 * [JVMH] - An annotation for JVM Hack comments explaining stuff
 * */

public class MainMacro extends StarMacro {
	
	/** 
	 * This script assumes it's running environment 
	 * is the STAR-CCM+ 11.04 Java Virtual Machine (JVM) 
	 * 		- Has STAR-CCM Server configured 
	 * 		and running with the loaded .sim file
	 * */

	/** Change 'maximumIterations' to X to run */
	
	int maximumIterations = 3000;

	/** [JVMH] For future Inter cross-JVM communication hacks */
	// Class CFDPipeline;
	// pulic String pwd = Paths.get(".").toAbsolutePath()
	// public String projectPath = "/home/viktorv/Projects/FSTeamDelft/Tirion/";
	
	public Simulation simulation;

	private MeshPipelineController meshPipelineController;
	private SolverStoppingCriterionManager solverCriterionManager;

	private String OS = System.getProperty("os.name").toLowerCase();

	public void execute() {

		/** The whole sequential CFD pipeline */
		// [JVMH] manageImports();
		initialize();
		//runSimulation(maximumIterations);
		runPostProcessing();
		//csvDataExport();
		//pythonPostProcExport();

		/** 
		 * You can just comment out any step of the pipeline and the rest will work 
		 * (keep initialization though) 
		 * */
	}

	private void initialize() {
		/** Get simulation environment, initialize global variables, initalize database structure */
		
		simulation = getActiveSimulation();
		initializeDatabase();
	}

	private void runSimulation(int maxIterations) {
		/** 
		 * Get the needed criterion, modify them and run the simulation 
		 * The simulation will stop if the number of iterations has reached 
		 * the previously specified "maximumIterations" (class global)
		 * */

		meshPipelineController = simulation.get(MeshPipelineController.class);
		solverCriterionManager = simulation.getSolverStoppingCriterionManager();
		
		meshPipelineController.generateVolumeMesh();
		StepStoppingCriterion stepStoppingCriterion = (StepStoppingCriterion) solverCriterionManager
			.getSolverStoppingCriterion("Maximum Steps");
		
		stepStoppingCriterion.setMaximumNumberSteps(maxIterations);
	
		simulation.getSimulationIterator().run();
	}

	public void runPostProcessing() {
		/** 
		 * TODO: Import from another PostProcessing class the needed methods 
		 * and use them here on the already initialized STAR-CCM Environment 
		 * and after running the specified number of iterations 
		 * 		-- Need to get the Inter-cross JVM communication working in
		 * 		   order for the ultimate software architecture to occur.
		 * */

		log("Running post processing... [!stub]");

		/** Code for post processing goes here
		 * TODO: Implement and optimize properly the post processing code
		 * use .export for the png exporting
		 */

	}

	public void initializeDatabase() {
		/** 
		 * This should act as an interface between the actual processing and 
		 * saving files (in a Database or just FilesystemDB)
		 * 
		 * TODO: Implement this after implementing the post processing 
		 * TODO: Implement post-post-processing, concating pngs into videos with ffmpeg 
		 * 
		 * Initializing consists of creating the folders/db models/
		 * Connecting to the database.
		 * 
		 * TODO: Things to consider: use Ignite || use Postgres + Redis || use MongoDB
		 * 
		 * */
	}

	public void checkpoint(String path) {
		simulation.saveState(path);
	}

	public void export() {
		/** 
		 * This should act as an interface between the actual processing and 
		 * saving files (in a Database or just FilesystemDB)
		 * 
		 * TODO: Implement this after implementing the post processing 
		 * TODO: Implement post-post-processing, concating pngs into videos with ffmpeg 
		 * 
		 * */

		log("STUB!");
	}

	private void csvDataExport() {

		/**
		 * Full export data (residuals) to .csv
		 * 
		 * Previously defined process in the full_export.java #Deprecated#
		 */

		String pathName, simName, mainFolderName, simPath, figName, saveName;

		simName = simulation.getPresentationName();
		simPath = simulation.getSessionDir();
		
		String separator = ((OS.indexOf("win") >= 0) ? "\\" : "/");
		mainFolderName = simPath + separator + "PlotsCSV#" + simName;
		
		log("Saving output to: " + mainFolderName);
		new File(mainFolderName).mkdir();
		
		int all = simulation.getPlotManager().getObjects().size();
		int i = 0;

		for (StarPlot plot : simulation.getPlotManager().getObjects()) {
			
			figName = plot.getPresentationName();
			MonitorPlot monitorPlot = ((MonitorPlot) simulation.getPlotManager().getPlot(figName));
			monitorPlot.export(resolvePath(mainFolderName + separator + figName + ".csv"), ",");	
			// log(resolvePath(mainFolderName + separator + figName + ".csv"));
			i++;
			progress(i, all);
		}
	}

	private void pythonPostProcExport() {
		/**
		 * Basic data perparation with pandas and numpy
		 * 
		 * STUB
		 */
		log("[pythonPostProcExport()]: !stub");
	}

	/** For JVM Hacks */

	private void manageImports() {
		/** [JVMH] For future Inter cross-JVM communication hacks */

		log(Paths.get(".").toAbsolutePath().normalize().toString());
		//log("file://" + projectPath + "/scripts/CFDPipeline.java");
		log("!stub");

		// [JVMH]
		// URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {
		// 	new URL("file://" + projectPath + "/scripts/CFDPipeline.java")
		// });
		
		// CFDPipeline = urlClassLoader.loadClass("deep");
	}

	/** End of JVM Hacks */

	/** Utility Functions */

	private void checkpointLog(String what, String where) {
		log(String.format("Saved %s at %s.", what, where));
	}

	private void progress(int done, int all) {
		StringBuilder progressBar = new StringBuilder();

		for(int i = 0; i < all; i++) {
			if (i < done) {
				progressBar.append("#");
			} else {
				progressBar.append("-");
			} 
		}

		log(
			String.format(
				"Progress: [%s] %d/%d -> %.2f%%", 
				progressBar.toString(), done, all, 
				(((float) done/ (float) all))*100
			)
		);
	}

	private void log(Object x) {
		/** A logging utility for debugging and additional information. Needs initialization beforehand */
		// System.out.println("[LOG]: " + x); // This logs in the terminal if you've run STAR-CCM through shell.
		simulation.println("[LOG]: " + x); // This logs in the output window in the program itself.
	}
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                              ./Tirion/src/main/PostProcessing.java                                                               0000644 0001750 0001731 00000102040 13551012703 017437  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  // STAR-CCM+ macro: post_process.java
// Written by STAR-CCM+ 11.04.012
package macro;

import java.util.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class PostProcessing extends StarMacro {

	/** Version 19v2_rad */

	public void execute() {
		run();
	}

	private void run() {

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

		Simulation simulation = getActiveSimulation();

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

		PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_1 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_2 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_3 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));
		PrimitiveFieldFunction primitiveFieldFunction_4 = ((PrimitiveFieldFunction) simulation
				.getFieldFunctionManager().getFunction("MeanSkinFrinctionMonitor"));

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		planeSection.getInputParts().setQuery(null);

		Region region_0 = simulation.getRegionManager().getRegion("Region 1");
		
		System.out.println("Boundary check accross all regions");

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
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));
		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		SimpleAnnotation simpleAnnotation_1 = simulation.getAnnotationManager().createSimpleAnnotation();
		simpleAnnotation_1.setPresentationName("Figure_name");
		simpleAnnotation_1.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scalarScene
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_1);

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);
			iterCpY = String.valueOf(iterY);
			
			figName = simName + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";
			simpleAnnotation_1.setText(figName);
			namePath = SCpYFolder + "/SCpY_" + iterCpY + ".png";

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);
			figName = simName + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";
			simpleAnnotation_1.setText(figName);

			namePath = VelYFolder + "/VelY_" + iterVel + ".png";
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);
			
			iterTCpy = String.valueOf(iterY);
			figName = simName + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";
			namePath = TCpYFolder + "/TCpY_" + iterTCpy + ".png";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation.getTransformManager()
				.getObject("Symmetry 1"));

		scalarDisplayer_2.setVisTransform(symmetricRepeat_0);
		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = TCpXFolder + "/TCpX__" + iterTCpx + ".png";

			simpleAnnotation_1.setText(figName);
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = TCpXFolder + "/TCpX_" + iterTCpx + ".png";

			simpleAnnotation_1.setText(figName);
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3, 1 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = SCpXFolder + "/SCpX__" + iterSCpx + ".png";

			simpleAnnotation_1.setText(figName);
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = SCpXFolder + "/SCpX_" + iterSCpx + ".png";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = VelXFolder + "/VelX__" + iterSCpx + ".png";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = VelXFolder + "/VelX_" + iterSCpx + ".png";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 1000 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVorX = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = VorXFolder + "/VorX__" + iterVorX + ".png";

			simpleAnnotation_1.setText(figName);
			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVorX = String.valueOf(iterX);
			namePath = VorXFolder + "/VorX_" + iterVorX + ".png";
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.1 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = TCpZFolder + "/TCpZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = TCpZFolder + "/TCpZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = SCpZFolder + "/SCpZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);
			
			iterTCpx = String.valueOf(iterZ);
			namePath = SCpZFolder + "/SCpZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = VelZFolder + "/VelZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));
			scalarScene.setMeshOverrideMode(0);
			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = VelZFolder + "/VelZ_" + iterTCpx + ".png";
			figName = simName + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";
			simpleAnnotation_1.setText(figName);

			scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);
		scalarDisplayer_2.getInputParts().setQuery(null);

		// Magic numbers defined by the previous cfd macro guy, corresponding to the stuff he wanted to add
		scalarDisplayer_2.getInputParts().setObjects(
			(Boundary) regionBoundaries.get(0).get(1), 
			(Boundary) regionBoundaries.get(0).get(2),
			(Boundary) regionBoundaries.get(0).get(3),
			(Boundary) regionBoundaries.get(0).get(4),
			(Boundary) regionBoundaries.get(0).get(7),
			(Boundary) regionBoundaries.get(0).get(9), 
			(Boundary) regionBoundaries.get(0).get(10), 
			(Boundary) regionBoundaries.get(0).get(13), 
			(Boundary) regionBoundaries.get(0).get(14)
		);

		scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));
		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));
		scalarScene.setMeshOverrideMode(0);
		currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		figName = simName + " " + " " + " Side";
		simpleAnnotation_1.setText(figName);
		namePath = SCpFolder + "/SCp_" + "Side" + ".png";
		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));
		scalarScene.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
				new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		namePath = SCpFolder + "/SCp_" + "Top" + ".png";
		figName = simName + " " + "Top";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		namePath = SCpFolder + "/SCp_" + "Bottom" + ".png";
		figName = simName + " Bottom";

		simpleAnnotation_1.setText(figName);
		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));
		scalarScene.setMeshOverrideMode(0);
		currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		namePath = SCpFolder + "/SCp_" + "Front" + ".png";
		figName = simName + " Front";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		scalarScene.setViewOrientation(new DoubleVector(new double[] { -1.0, 0.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		namePath = SCpFolder + "/SCp_" + "Rear" + ".png";
		figName = simName + " Rear";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_4);
		scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));
		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0.0, 0.05 }));
		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);
		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));
		scalarScene.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		figName = simName + " " + " Side";
		simpleAnnotation_1.setText(figName);
		namePath = SkinFolder + "/Skin_" + "Side" + ".png";

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));
		scalarScene.setMeshOverrideMode(0);
		currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
				new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		namePath = SkinFolder + "/Skin_" + "Top" + ".png";
		figName = simName + " " + "Top";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		scalarScene.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		namePath = SkinFolder + "/Skin_" + "Bottom" + ".png";
		figName = simName + " Bottom";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));
		scalarScene.setMeshOverrideMode(0);
		currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		namePath = SkinFolder + "/Skin_" + "Front" + ".png";
		figName = simName + " Front";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		scalarScene.setViewOrientation(new DoubleVector(new double[] { 1.0, 0.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		namePath = SkinFolder + "/Skin_" + "Rear" + ".png";
		figName = simName + " Rear";
		simpleAnnotation_1.setText(figName);

		scalarScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

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
		vectorDisplayer_4.getVectorDisplayQuantity().setClip(0);

		UserFieldFunction userFieldFunction_4 = ((UserFieldFunction) simulation.getFieldFunctionManager()
				.getFunction("Mean of Velocity"));

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);
		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));
		vectorScene.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_4 }));

		CurrentView currentView_3 = vectorScene.getCurrentView();
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		SimpleAnnotation simpleAnnotation_2 = simulation.getAnnotationManager().createSimpleAnnotation();
		simpleAnnotation_2.setPresentationName("Figure_name");
		simpleAnnotation_2.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_2 = (FixedAspectAnnotationProp) vectorScene
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_2);

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);
		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);
			figName = simName + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";
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
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);
			namePath = VectVelZFolder + "/VectVelZ_" + iterTCpx + ".jpg";
			figName = simName + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";
			simpleAnnotation_2.setText(figName);

			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);
		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);
		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));
		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = VectVelXFolder + "/VectVelX__" + iterVel + ".jpg";
			simpleAnnotation_2.setText(figName);
			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));
			vectorScene.setMeshOverrideMode(0);
			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);
			figName = simName + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";
			namePath = VectVelXFolder + "/VectVelX_" + iterVel + ".jpg";
			simpleAnnotation_2.setText(figName);

			vectorScene.printAndWait(resolvePath(namePath), 2, 2200, 1300, true, false);
		}
	}
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ./Tirion/src/deprecated/                                                                            0000755 0001750 0001731 00000000000 13550751402 014776  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/deprecated/post_process_19v2_rad.java                                                  0000777 0001750 0001731 00000112302 13547656516 022021  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  // STAR-CCM+ macro: post_process.java
// Written by STAR-CCM+ 11.04.012
package macro;

import java.util.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class post_process_19v2_rad extends StarMacro {

	/** Version 19v2_rad */

	public void execute() {
		execute0();
	}

	private void execute0() {

		String name_path;
		String Name_Sim;
		String name_figure;
		String iterCpY;
		String iterCpYName;
		String iterTCpy;
		String iterSCpx;
		String iterVel;
		String iterTCpx;
		String iterVorX;
		String MainFolderName;
		String TCpZFolder;
		String TCpYFolder;
		String TCpXFolder;
		String SCpXFolder;
		String SCpYFolder;
		String SkinFolder;
		String SCpFolder;
		String SCpZFolder;
		String VelXFolder;
		String VelYFolder;
		String VelZFolder;
		String VorXFolder;
		String VectVelYFolder;
		String VectVelXFolder;
		String VectVelZFolder;

		String Path_Sim;

		/*
		 * -----------------------------------------------------------------------------
		 * --------------        CREATE FOLDER TO SAVE SIMULATIONS        --------------
		 * -----------------------------------------------------------------------------
		 * 
		 */

		Simulation simulation_0 = getActiveSimulation();

		Name_Sim = simulation_0.getPresentationName();
		Path_Sim = simulation_0.getSessionDir();

		MainFolderName = Path_Sim + "\\PostProcessing_" + Name_Sim;
		TCpZFolder = MainFolderName + "\\TCpZ";
		TCpYFolder = MainFolderName + "\\TCpY";
		TCpXFolder = MainFolderName + "\\TCpX";
		SCpXFolder = MainFolderName + "\\SCpX";
		SCpYFolder = MainFolderName + "\\SCpY";
		SCpFolder = MainFolderName + "\\SCp";
		SkinFolder = MainFolderName + "\\Skin";
		SCpZFolder = MainFolderName + "\\SCpZ";
		VelXFolder = MainFolderName + "\\VelX";
		VelYFolder = MainFolderName + "\\VelY";
		VelZFolder = MainFolderName + "\\VelZ";
		VorXFolder = MainFolderName + "\\VorX";
		VectVelYFolder = MainFolderName + "\\VectVelY";
		VectVelXFolder = MainFolderName + "\\VectVelX";
		VectVelZFolder = MainFolderName + "\\VectVelZ";

		new File(MainFolderName).mkdir();
		new File(TCpZFolder).mkdir();
		new File(TCpYFolder).mkdir();
		new File(TCpXFolder).mkdir();
		new File(SCpXFolder).mkdir();
		new File(SCpYFolder).mkdir();
		new File(SCpZFolder).mkdir();
		new File(VelXFolder).mkdir();
		new File(VelYFolder).mkdir();
		new File(VelZFolder).mkdir();
		new File(VorXFolder).mkdir();
		new File(VectVelYFolder).mkdir();
		new File(VectVelXFolder).mkdir();
		new File(VectVelZFolder).mkdir();

		PlaneSection planeSection_4 = (PlaneSection) simulation_0.getPartManager().createImplicitPart(
				new NeoObjectVector(new Object[] {}), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 0.0 }), 0, 1, new DoubleVector(new double[] { 0.0 }));

		Coordinate coordinate_4 = planeSection_4.getOriginCoordinate();

		Units units_0 = ((Units) simulation_0.getUnitsManager().getObject("m"));

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.1, 0.0 }));

		Coordinate coordinate_5 = planeSection_4.getOrientationCoordinate();

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		simulation_0.getSceneManager().createScalarScene("Scalar Scene", "Outline", "Scalar");

		Scene scene_3 = simulation_0.getSceneManager().getScene("Scalar Scene 1");

		scene_3.initializeAndWait();

		PartDisplayer partDisplayer_3 = ((PartDisplayer) scene_3.getDisplayerManager().getDisplayer("Outline 1"));

		partDisplayer_3.initialize();

		ScalarDisplayer scalarDisplayer_2 = ((ScalarDisplayer) scene_3.getDisplayerManager().getDisplayer("Scalar 1"));

		scalarDisplayer_2.initialize();

		scene_3.open(true);

		SceneUpdate sceneUpdate_2 = scene_3.getSceneUpdate();

		HardcopyProperties hardcopyProperties_2 = sceneUpdate_2.getHardcopyProperties();

		hardcopyProperties_2.setCurrentResolutionWidth(1271);

		hardcopyProperties_2.setCurrentResolutionHeight(587);

		scene_3.resetCamera();

		scene_3.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_3 }));

		scalarDisplayer_2.getInputParts().setQuery(null);

		scalarDisplayer_2.getInputParts().setObjects(planeSection_4);

		PrimitiveFieldFunction primitiveFieldFunction_0 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanStatic_CPMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_1 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanTotal_CPMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_2 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanVelocityMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_3 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanVorticityMonitor"));

		PrimitiveFieldFunction primitiveFieldFunction_4 = ((PrimitiveFieldFunction) simulation_0
				.getFieldFunctionManager().getFunction("MeanSkinFrinctionMonitor"));

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		planeSection_4.getInputParts().setQuery(null);

		Region region_0 = simulation_0.getRegionManager().getRegion("Region 1");

		Boundary boundary_0 = region_0.getBoundaryManager().getBoundary("Belt");

		Boundary boundary_1 = region_0.getBoundaryManager().getBoundary("Chassis_R");

		Boundary boundary_2 = region_0.getBoundaryManager().getBoundary("Diffuser_R");

		Boundary boundary_3 = region_0.getBoundaryManager().getBoundary("Fan_Inlet_R");

		Boundary boundary_4 = region_0.getBoundaryManager().getBoundary("Fan_Outlet_R");

		Boundary boundary_5 = region_0.getBoundaryManager().getBoundary("Floor_R");

		Boundary boundary_6 = region_0.getBoundaryManager().getBoundary("FrontWing_R");

		// Boundary boundary_7 =
		// region_0.getBoundaryManager().getBoundary("Ground");

		Boundary boundary_8 = region_0.getBoundaryManager().getBoundary("Inlet");

		Boundary boundary_9 = region_0.getBoundaryManager().getBoundary("Outlet");

		Boundary boundary_10 = region_0.getBoundaryManager().getBoundary("Rad_Case_R");

		Boundary boundary_11 = region_0.getBoundaryManager().getBoundary("Rad_Inlet_R");

		Boundary boundary_12 = region_0.getBoundaryManager().getBoundary("Rad_Outlet_R");

		Boundary boundary_13 = region_0.getBoundaryManager().getBoundary("RearWing_R");

		InterfaceBoundary interfaceBoundary_0 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Rad_Inlet_R [0]"));

		InterfaceBoundary interfaceBoundary_1 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Rad_Outlet_R [0]"));

		InterfaceBoundary interfaceBoundary_2 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Fan_Outlet_R [0]"));

		InterfaceBoundary interfaceBoundary_3 = ((InterfaceBoundary) region_0.getBoundaryManager()
				.getBoundary("Fan_Inlet_R [0]"));

		Boundary boundary_14 = region_0.getBoundaryManager().getBoundary("Right");

		Boundary boundary_15 = region_0.getBoundaryManager().getBoundary("Susp_FR");

		Boundary boundary_16 = region_0.getBoundaryManager().getBoundary("Susp_RR");

		Boundary boundary_17 = region_0.getBoundaryManager().getBoundary("Symmetry");

		Boundary boundary_18 = region_0.getBoundaryManager().getBoundary("Top");

		Boundary boundary_19 = region_0.getBoundaryManager().getBoundary("Wheel_FR");

		Boundary boundary_20 = region_0.getBoundaryManager().getBoundary("Wheel_RR");

		Region region_1 = simulation_0.getRegionManager().getRegion("Region 2");

		Region region_2 = simulation_0.getRegionManager().getRegion("Region 3");

		Boundary boundary_21 = region_1.getBoundaryManager().getBoundary("Rad_Inlet_R");

		Boundary boundary_22 = region_1.getBoundaryManager().getBoundary("Rad_Outlet_R");

		Boundary boundary_23 = region_1.getBoundaryManager().getBoundary("Rad_Side_R");

		InterfaceBoundary interfaceBoundary_4 = ((InterfaceBoundary) region_1.getBoundaryManager()
				.getBoundary("Rad_Inlet_R [1]"));

		InterfaceBoundary interfaceBoundary_5 = ((InterfaceBoundary) region_1.getBoundaryManager()
				.getBoundary("Rad_Outlet_R [1]"));

		Boundary boundary_24 = region_2.getBoundaryManager().getBoundary("Fan_Inlet_R");

		Boundary boundary_25 = region_2.getBoundaryManager().getBoundary("Fan_Outlet_R");

		Boundary boundary_26 = region_2.getBoundaryManager().getBoundary("Fan_Side_R");

		InterfaceBoundary interfaceBoundary_6 = ((InterfaceBoundary) region_2.getBoundaryManager()
				.getBoundary("Fan_Outlet_R [1]"));

		InterfaceBoundary interfaceBoundary_7 = ((InterfaceBoundary) region_2.getBoundaryManager()
				.getBoundary("Fan_Inlet_R [1]"));

		planeSection_4.getInputParts().setObjects(region_0, boundary_0, boundary_1, boundary_2, boundary_3, boundary_4,
				boundary_5, boundary_6,
				// boundary_7,
				boundary_8, boundary_9, boundary_10, boundary_11, boundary_12, boundary_13, interfaceBoundary_0,
				interfaceBoundary_1, interfaceBoundary_2, interfaceBoundary_3, boundary_14, boundary_15, boundary_16,
				boundary_17, boundary_18, boundary_19, boundary_20, region_1, boundary_21, boundary_22, boundary_23,
				interfaceBoundary_4, interfaceBoundary_5, region_2, boundary_24, boundary_25, boundary_26,
				interfaceBoundary_6, interfaceBoundary_7);

		CurrentView currentView_2 = scene_3.getCurrentView();

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		SimpleAnnotation simpleAnnotation_1 = simulation_0.getAnnotationManager().createSimpleAnnotation();

		simpleAnnotation_1.setPresentationName("Figure_name");

		simpleAnnotation_1.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_1 = (FixedAspectAnnotationProp) scene_3
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_1);

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterCpY = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			simpleAnnotation_1.setText(name_figure);

			name_path = SCpYFolder + "\\SCpY_" + iterCpY + ".png";

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			simpleAnnotation_1.setText(name_figure);

			name_path = VelYFolder + "\\VelY_" + iterVel + ".png";

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpy = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			name_path = TCpYFolder + "\\TCpY_" + iterTCpy + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		SymmetricRepeat symmetricRepeat_0 = ((SymmetricRepeat) simulation_0.getTransformManager()
				.getObject("Symmetry 1"));

		scalarDisplayer_2.setVisTransform(symmetricRepeat_0);

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = TCpXFolder + "\\TCpX__" + iterTCpx + ".png";

			simpleAnnotation_1.setText(name_figure);
			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = TCpXFolder + "\\TCpX_" + iterTCpx + ".png";

			simpleAnnotation_1.setText(name_figure);
			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3, 1 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = SCpXFolder + "\\SCpX__" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = SCpXFolder + "\\SCpX_" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VelXFolder + "\\VelX__" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterSCpx = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VelXFolder + "\\VelX_" + iterSCpx + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_3);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 1000 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVorX = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VorXFolder + "\\VorX__" + iterVorX + ".png";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVorX = String.valueOf(iterX);

			name_path = VorXFolder + "\\VorX_" + iterVorX + ".png";

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_1);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -0.1, 1.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.1 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = TCpZFolder + "\\TCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = TCpZFolder + "\\TCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = SCpZFolder + "\\SCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = SCpZFolder + "\\SCpZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_2);

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VelZFolder + "\\VelZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_3.setMeshOverrideMode(0);

			currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VelZFolder + "\\VelZ_" + iterTCpx + ".png";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_1.setText(name_figure);

			scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_0);

		scalarDisplayer_2.getInputParts().setQuery(null);

		scalarDisplayer_2.getInputParts().setObjects(boundary_1, boundary_2, boundary_5, boundary_6, boundary_13,
				boundary_15, boundary_16, boundary_19, boundary_20);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { -3.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_figure = Name_Sim + " " + " " + " Side";

		simpleAnnotation_1.setText(name_figure);

		name_path = SCpFolder + "\\SCp_" + "Side" + ".png";

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
				new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_path = SCpFolder + "\\SCp_" + "Top" + ".png";

		name_figure = Name_Sim + " " + "Top";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SCpFolder + "\\SCp_" + "Bottom" + ".png";

		name_figure = Name_Sim + " Bottom";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		name_path = SCpFolder + "\\SCp_" + "Front" + ".png";

		name_figure = Name_Sim + " Front";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { -1.0, 0.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SCpFolder + "\\SCp_" + "Rear" + ".png";

		name_figure = Name_Sim + " Rear";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scalarDisplayer_2.getScalarDisplayQuantity().setFieldFunction(primitiveFieldFunction_4);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, -1.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setRange(new DoubleVector(new double[] { 0.0, 0.05 }));

		scalarDisplayer_2.getScalarDisplayQuantity().setClip(0);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0001, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, 0.0001, 0.5 }),
				new DoubleVector(new double[] { 0.6, -50, 1.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_figure = Name_Sim + " " + " Side";

		simpleAnnotation_1.setText(name_figure);

		name_path = SkinFolder + "\\Skin_" + "Side" + ".png";

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 0.0, 0.0001 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 }),
				new DoubleVector(new double[] { 0.6, -0.1, 10.0 }), new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1,
				1);

		name_path = SkinFolder + "\\Skin_" + "Top" + ".png";

		name_figure = Name_Sim + " " + "Top";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 0.0, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SkinFolder + "\\Skin_" + "Bottom" + ".png";

		name_figure = Name_Sim + " Bottom";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		coordinate_4.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { -0.85, 0.0, 0.0 }));

		scene_3.setMeshOverrideMode(0);

		currentView_2.setInput(new DoubleVector(new double[] { -0.85, 0.0, 0.5 }),
				new DoubleVector(new double[] { -40 - 0.8, 0.0, 1.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

		name_path = SkinFolder + "\\Skin_" + "Front" + ".png";

		name_figure = Name_Sim + " Front";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		scene_3.setViewOrientation(new DoubleVector(new double[] { 1.0, 0.0, 0.0 }),
				new DoubleVector(new double[] { 0.0, 0.0, 1.0 }));

		name_path = SkinFolder + "\\Skin_" + "Rear" + ".png";

		name_figure = Name_Sim + " Rear";

		simpleAnnotation_1.setText(name_figure);

		scene_3.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		simulation_0.getSceneManager().createVectorScene("Vector Scene", "Outline", "Vector");

		Scene scene_4 = simulation_0.getSceneManager().getScene("Vector Scene 1");

		scene_4.initializeAndWait();

		PartDisplayer partDisplayer_4 = ((PartDisplayer) scene_4.getDisplayerManager().getDisplayer("Outline 1"));

		VectorDisplayer vectorDisplayer_4 = ((VectorDisplayer) scene_4.getDisplayerManager().getDisplayer("Vector 1"));

		vectorDisplayer_4.initialize();

		scene_4.open(true);

		vectorDisplayer_4.setDisplayMode(1);

		vectorDisplayer_4.getInputParts().setQuery(null);

		vectorDisplayer_4.getInputParts().setObjects(planeSection_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setClip(0);

		UserFieldFunction userFieldFunction_4 = ((UserFieldFunction) simulation_0.getFieldFunctionManager()
				.getFunction("Mean of Velocity"));

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		scene_4.getDisplayerManager().deleteDisplayers(new NeoObjectVector(new Object[] { partDisplayer_4 }));

		CurrentView currentView_3 = scene_4.getCurrentView();

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		SimpleAnnotation simpleAnnotation_2 = simulation_0.getAnnotationManager().createSimpleAnnotation();

		simpleAnnotation_2.setPresentationName("Figure_name");

		simpleAnnotation_2.setDefaultHeight(0.03);

		FixedAspectAnnotationProp fixedAspectAnnotationProp_2 = (FixedAspectAnnotationProp) scene_4
				.getAnnotationPropManager().createPropForAnnotation(simpleAnnotation_2);

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0.0, 1.0, 0.0 }));

		for (int iterY = 0; iterY < 17; iterY++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0001 + 0.05 * iterY, 0.0 }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, 0.0001 + 0.05 * iterY, 0.5 }),
					new DoubleVector(new double[] { 0.6, -50 + 0.05 * iterY, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterY);

			name_figure = Name_Sim + " " + "y=" + String.format("%.2f", 0.05 * iterY) + "m";

			simpleAnnotation_2.setText(name_figure);

			name_path = VectVelYFolder + "\\VectVelY_" + iterVel + ".jpg";

			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 0, 0, 1 }));

		for (int iterZ = 0; iterZ < 6; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.01 * iterZ }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.01 * iterZ }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VectVelZFolder + "\\VectVelZ_" + iterTCpx + ".jpg";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.01 * iterZ) + "m";

			simpleAnnotation_2.setText(name_figure);

			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterZ = 6; iterZ < 27; iterZ++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { 0.0, 0.0, 0.0001 + 0.05 * (iterZ - 4) }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { 0.6, -0.1, 0.0001 + 0.05 * (iterZ - 4) }),
					new DoubleVector(new double[] { 0.6, -0.1, 10.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterTCpx = String.valueOf(iterZ);

			name_path = VectVelZFolder + "\\VectVelZ_" + iterTCpx + ".jpg";

			name_figure = Name_Sim + " " + "z=" + String.format("%.3f", 0.05 * (iterZ - 4)) + "m";

			simpleAnnotation_2.setText(name_figure);

			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		vectorDisplayer_4.setVisTransform(symmetricRepeat_0);

		vectorDisplayer_4.getVectorDisplayQuantity().setFieldFunction(userFieldFunction_4);

		vectorDisplayer_4.getVectorDisplayQuantity().setRange(new DoubleVector(new double[] { 0, 20.0 }));

		coordinate_5.setCoordinate(units_0, units_0, units_0, new DoubleVector(new double[] { 1.0, 0.0, 0.0 }));

		for (int iterX = -1; iterX < 0; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VectVelXFolder + "\\VectVelX__" + iterVel + ".jpg";

			simpleAnnotation_2.setText(name_figure);
			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

		for (int iterX = 0; iterX < 60; iterX++) {

			coordinate_4.setCoordinate(units_0, units_0, units_0,
					new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.0 }));

			scene_4.setMeshOverrideMode(0);

			currentView_3.setInput(new DoubleVector(new double[] { -0.85 + 0.05 * iterX, 0.0, 0.5 }),
					new DoubleVector(new double[] { -40 - 0.8 + 0.05 * iterX, 0.0, 1.0 }),
					new DoubleVector(new double[] { 0.0, 0.0, 1.0 }), 1, 1);

			iterVel = String.valueOf(iterX);

			name_figure = Name_Sim + " " + "x=" + String.format("%.2f", 0.05 * iterX) + "m";

			name_path = VectVelXFolder + "\\VectVelX_" + iterVel + ".jpg";

			simpleAnnotation_2.setText(name_figure);
			scene_4.printAndWait(resolvePath(name_path), 2, 2200, 1300, true, false);

		}

	}
}                                                                                                                                                                                                                                                                                                                              ./Tirion/tirion.sh                                                                                  0000755 0001750 0001731 00000002752 13551075677 013776  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/bash
# Tirion framework... Made for Formula Student Team Delft.
# Interface script for now. TODO: Future to be changed with a fully functional Web UI.
# Default output is in the current folder

macroPath="./src/main/PostProcessing.java"; #Add the absolute path here if you want to run ./tirion.sh from everywhere

function cleanup {                                                                                                                                                   
   echo "[!] Tirion framework exiting..."
   exit 0
}

function finishedJob {
    echo "[*] Exited gracefully."
    exit 0
}

simPath=$1;
cores=$2;

function main {
    echo "[-] Initializing Tirion framework...";
    
    sleep 1;

    trap cleanup 2;
    trap finishedJob 0;

    license_path="1999@flex.cd-adapco.com";
    power_on_demand_license="TtRoFR472Ew3lLUCvel7JQ";

    if [ ! -n "$cores" ]; then
        cores=$(( $(grep -c ^processor /proc/cpuinfo) / 2 ));
        echo "No amount of cores specified, using half of all the cores by default.";
        echo "You can specify the amount of cores by adding an additional parameter after the .sim file";
    fi
    
    echo "[*] Initialized."
    echo "[*] Loaded:"
    echo "    -- Simulation File: $simPath"
    echo "    -- MainMacro Script: $macroPath"
    echo "[%] Running STAR-CCM+ with $cores cores configured..."

    sleep 2

    starccm+ -rsh ssh -np $cores -podkey $power_on_demand_license -licpath $license_path -power $simPath -batch $macroPath;
}

main
                      ./Tirion/                                                                                           0000755 0001750 0001731 00000000000 13551075440 012111  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/                                                                                       0000755 0001750 0001731 00000000000 13551003641 012672  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/main/                                                                                  0000755 0001750 0001731 00000000000 13550751402 013622  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/main/MainMacro.java                                                                    0000644 0001750 0001731 00000000000 13550755211 024147  1./Tirion/src/main/MainMacro.java                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/main/PostProcessing.java                                                               0000644 0001750 0001731 00000000000 13551012703 026370  1./Tirion/src/main/PostProcessing.java                                                               ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/deprecated/                                                                            0000755 0001750 0001731 00000000000 13550751402 014776  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/src/deprecated/post_process_19v2_rad.java                                                  0000777 0001750 0001731 00000000000 13547656516 033274  1./Tirion/src/deprecated/post_process_19v2_rad.java                                                  ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/tirion.sh                                                                                  0000755 0001750 0001731 00000000000 13551075677 017216  1./Tirion/tirion.sh                                                                                  ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/                                                                                      0000755 0001750 0001731 00000000000 13551075760 012757  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/config                                                                                0000644 0001750 0001731 00000000403 13547672426 014153  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  [core]
	repositoryformatversion = 0
	filemode = true
	bare = false
	logallrefupdates = true
[remote "origin"]
	url = https://github.com/VikVelev/Tirion
	fetch = +refs/heads/*:refs/remotes/origin/*
[branch "master"]
	remote = origin
	merge = refs/heads/master
                                                                                                                                                                                                                                                             ./Tirion/.git/branches/                                                                             0000755 0001750 0001731 00000000000 13547672423 014550  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/ORIG_HEAD                                                                             0000644 0001750 0001731 00000000051 13551012623 014204  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  b6268a8174a93ee8857450af9d5fca669dc1fc35
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ./Tirion/.git/logs/                                                                                 0000755 0001750 0001731 00000000000 13547672426 013732  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/logs/HEAD                                                                             0000644 0001750 0001731 00000001670 13551012627 014344  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  0000000000000000000000000000000000000000 53087fae8f2b7d3b8512dd27063b527e60f48446 Viktor Velev <viktorvelev8@gmail.com> 1570731286 +0200	clone: from https://github.com/VikVelev/Tirion
53087fae8f2b7d3b8512dd27063b527e60f48446 c3cf70b54ba189933966129750b4a6c02298b54f Viktor Velev <viktorvelev8@gmail.com> 1570731400 +0200	commit: Added basic project structure and MainMacro barebones implemented
c3cf70b54ba189933966129750b4a6c02298b54f b3043a9f21e174e440a7cd50b4ffe4d169906382 Viktor Velev <viktorvelev8@gmail.com> 1570731455 +0200	commit: Change folder structure
b3043a9f21e174e440a7cd50b4ffe4d169906382 b6268a8174a93ee8857450af9d5fca669dc1fc35 Viktor Velev <viktorvelev8@gmail.com> 1571034495 +0200	commit: Base framework implemented, optimized the original PostProc
b6268a8174a93ee8857450af9d5fca669dc1fc35 7337ff846aaa6f950baf1d10c6ac653fcdd6e1a3 Viktor Velev <viktorvelev8@gmail.com> 1571034515 +0200	pull: Merge made by the 'recursive' strategy.
                                                                        ./Tirion/.git/logs/refs/                                                                            0000755 0001750 0001731 00000000000 13547672426 014671  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/logs/refs/remotes/                                                                    0000755 0001750 0001731 00000000000 13547672426 016347  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/logs/refs/remotes/origin/                                                             0000755 0001750 0001731 00000000000 13547672622 017634  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/logs/refs/remotes/origin/HEAD                                                         0000644 0001750 0001731 00000000270 13547672426 020261  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  0000000000000000000000000000000000000000 53087fae8f2b7d3b8512dd27063b527e60f48446 Viktor Velev <viktorvelev8@gmail.com> 1570731286 +0200	clone: from https://github.com/VikVelev/Tirion
                                                                                                                                                                                                                                                                                                                                        ./Tirion/.git/logs/refs/remotes/origin/master                                                       0000644 0001750 0001731 00000001144 13551012662 021035  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  53087fae8f2b7d3b8512dd27063b527e60f48446 c3cf70b54ba189933966129750b4a6c02298b54f Viktor Velev <viktorvelev8@gmail.com> 1570731410 +0200	update by push
c3cf70b54ba189933966129750b4a6c02298b54f b3043a9f21e174e440a7cd50b4ffe4d169906382 Viktor Velev <viktorvelev8@gmail.com> 1570731464 +0200	update by push
b3043a9f21e174e440a7cd50b4ffe4d169906382 5fe747783c25160880d0256c3a6bb61ddbc7ecf4 Viktor Velev <viktorvelev8@gmail.com> 1571034515 +0200	pull: fast-forward
5fe747783c25160880d0256c3a6bb61ddbc7ecf4 7337ff846aaa6f950baf1d10c6ac653fcdd6e1a3 Viktor Velev <viktorvelev8@gmail.com> 1571034546 +0200	update by push
                                                                                                                                                                                                                                                                                                                                                                                                                            ./Tirion/.git/logs/refs/heads/                                                                      0000755 0001750 0001731 00000000000 13547672426 015755  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/logs/refs/heads/master                                                                0000644 0001750 0001731 00000001670 13551012627 017161  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  0000000000000000000000000000000000000000 53087fae8f2b7d3b8512dd27063b527e60f48446 Viktor Velev <viktorvelev8@gmail.com> 1570731286 +0200	clone: from https://github.com/VikVelev/Tirion
53087fae8f2b7d3b8512dd27063b527e60f48446 c3cf70b54ba189933966129750b4a6c02298b54f Viktor Velev <viktorvelev8@gmail.com> 1570731400 +0200	commit: Added basic project structure and MainMacro barebones implemented
c3cf70b54ba189933966129750b4a6c02298b54f b3043a9f21e174e440a7cd50b4ffe4d169906382 Viktor Velev <viktorvelev8@gmail.com> 1570731455 +0200	commit: Change folder structure
b3043a9f21e174e440a7cd50b4ffe4d169906382 b6268a8174a93ee8857450af9d5fca669dc1fc35 Viktor Velev <viktorvelev8@gmail.com> 1571034495 +0200	commit: Base framework implemented, optimized the original PostProc
b6268a8174a93ee8857450af9d5fca669dc1fc35 7337ff846aaa6f950baf1d10c6ac653fcdd6e1a3 Viktor Velev <viktorvelev8@gmail.com> 1571034515 +0200	pull: Merge made by the 'recursive' strategy.
                                                                        ./Tirion/.git/COMMIT_EDITMSG                                                                        0000644 0001750 0001731 00000000007 13551012645 015034  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  Merged
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ./Tirion/.git/hooks/                                                                                0000755 0001750 0001731 00000000000 13547672423 014106  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/hooks/update.sample                                                                   0000755 0001750 0001731 00000007032 13547672423 016600  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to block unannotated tags from entering.
# Called by "git receive-pack" with arguments: refname sha1-old sha1-new
#
# To enable this hook, rename this file to "update".
#
# Config
# ------
# hooks.allowunannotated
#   This boolean sets whether unannotated tags will be allowed into the
#   repository.  By default they won't be.
# hooks.allowdeletetag
#   This boolean sets whether deleting tags will be allowed in the
#   repository.  By default they won't be.
# hooks.allowmodifytag
#   This boolean sets whether a tag may be modified after creation. By default
#   it won't be.
# hooks.allowdeletebranch
#   This boolean sets whether deleting branches will be allowed in the
#   repository.  By default they won't be.
# hooks.denycreatebranch
#   This boolean sets whether remotely creating branches will be denied
#   in the repository.  By default this is allowed.
#

# --- Command line
refname="$1"
oldrev="$2"
newrev="$3"

# --- Safety check
if [ -z "$GIT_DIR" ]; then
	echo "Don't run this script from the command line." >&2
	echo " (if you want, you could supply GIT_DIR then run" >&2
	echo "  $0 <ref> <oldrev> <newrev>)" >&2
	exit 1
fi

if [ -z "$refname" -o -z "$oldrev" -o -z "$newrev" ]; then
	echo "usage: $0 <ref> <oldrev> <newrev>" >&2
	exit 1
fi

# --- Config
allowunannotated=$(git config --bool hooks.allowunannotated)
allowdeletebranch=$(git config --bool hooks.allowdeletebranch)
denycreatebranch=$(git config --bool hooks.denycreatebranch)
allowdeletetag=$(git config --bool hooks.allowdeletetag)
allowmodifytag=$(git config --bool hooks.allowmodifytag)

# check for no description
projectdesc=$(sed -e '1q' "$GIT_DIR/description")
case "$projectdesc" in
"Unnamed repository"* | "")
	echo "*** Project description file hasn't been set" >&2
	exit 1
	;;
esac

# --- Check types
# if $newrev is 0000...0000, it's a commit to delete a ref.
zero="0000000000000000000000000000000000000000"
if [ "$newrev" = "$zero" ]; then
	newrev_type=delete
else
	newrev_type=$(git cat-file -t $newrev)
fi

case "$refname","$newrev_type" in
	refs/tags/*,commit)
		# un-annotated tag
		short_refname=${refname##refs/tags/}
		if [ "$allowunannotated" != "true" ]; then
			echo "*** The un-annotated tag, $short_refname, is not allowed in this repository" >&2
			echo "*** Use 'git tag [ -a | -s ]' for tags you want to propagate." >&2
			exit 1
		fi
		;;
	refs/tags/*,delete)
		# delete tag
		if [ "$allowdeletetag" != "true" ]; then
			echo "*** Deleting a tag is not allowed in this repository" >&2
			exit 1
		fi
		;;
	refs/tags/*,tag)
		# annotated tag
		if [ "$allowmodifytag" != "true" ] && git rev-parse $refname > /dev/null 2>&1
		then
			echo "*** Tag '$refname' already exists." >&2
			echo "*** Modifying a tag is not allowed in this repository." >&2
			exit 1
		fi
		;;
	refs/heads/*,commit)
		# branch
		if [ "$oldrev" = "$zero" -a "$denycreatebranch" = "true" ]; then
			echo "*** Creating a branch is not allowed in this repository" >&2
			exit 1
		fi
		;;
	refs/heads/*,delete)
		# delete branch
		if [ "$allowdeletebranch" != "true" ]; then
			echo "*** Deleting a branch is not allowed in this repository" >&2
			exit 1
		fi
		;;
	refs/remotes/*,commit)
		# tracking branch
		;;
	refs/remotes/*,delete)
		# delete tracking branch
		if [ "$allowdeletebranch" != "true" ]; then
			echo "*** Deleting a tracking branch is not allowed in this repository" >&2
			exit 1
		fi
		;;
	*)
		# Anything else (is there anything else?)
		echo "*** Update hook: unknown type of update to ref $refname of type $newrev_type" >&2
		exit 1
		;;
esac

# --- Finished
exit 0
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ./Tirion/.git/hooks/post-update.sample                                                              0000755 0001750 0001731 00000000275 13547672423 017565  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to prepare a packed repository for use over
# dumb transports.
#
# To enable this hook, rename this file to "post-update".

exec git update-server-info
                                                                                                                                                                                                                                                                                                                                   ./Tirion/.git/hooks/prepare-commit-msg.sample                                                       0000755 0001750 0001731 00000002724 13547672423 021031  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to prepare the commit log message.
# Called by "git commit" with the name of the file that has the
# commit message, followed by the description of the commit
# message's source.  The hook's purpose is to edit the commit
# message file.  If the hook fails with a non-zero status,
# the commit is aborted.
#
# To enable this hook, rename this file to "prepare-commit-msg".

# This hook includes three examples. The first one removes the
# "# Please enter the commit message..." help message.
#
# The second includes the output of "git diff --name-status -r"
# into the message, just before the "git status" output.  It is
# commented because it doesn't cope with --amend or with squashed
# commits.
#
# The third example adds a Signed-off-by line to the message, that can
# still be edited.  This is rarely a good idea.

COMMIT_MSG_FILE=$1
COMMIT_SOURCE=$2
SHA1=$3

/usr/bin/perl -i.bak -ne 'print unless(m/^. Please enter the commit message/..m/^#$/)' "$COMMIT_MSG_FILE"

# case "$COMMIT_SOURCE,$SHA1" in
#  ,|template,)
#    /usr/bin/perl -i.bak -pe '
#       print "\n" . `git diff --cached --name-status -r`
# 	 if /^#/ && $first++ == 0' "$COMMIT_MSG_FILE" ;;
#  *) ;;
# esac

# SOB=$(git var GIT_COMMITTER_IDENT | sed -n 's/^\(.*>\).*$/Signed-off-by: \1/p')
# git interpret-trailers --in-place --trailer "$SOB" "$COMMIT_MSG_FILE"
# if test -z "$COMMIT_SOURCE"
# then
#   /usr/bin/perl -i.bak -pe 'print "\n" if !$first_line++' "$COMMIT_MSG_FILE"
# fi
                                            ./Tirion/.git/hooks/pre-receive.sample                                                              0000755 0001750 0001731 00000001040 13547672423 017515  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to make use of push options.
# The example simply echoes all push options that start with 'echoback='
# and rejects all pushes when the "reject" push option is used.
#
# To enable this hook, rename this file to "pre-receive".

if test -n "$GIT_PUSH_OPTION_COUNT"
then
	i=0
	while test "$i" -lt "$GIT_PUSH_OPTION_COUNT"
	do
		eval "value=\$GIT_PUSH_OPTION_$i"
		case "$value" in
		echoback=*)
			echo "echo from the pre-receive-hook: ${value#*=}" >&2
			;;
		reject)
			exit 1
		esac
		i=$((i + 1))
	done
fi
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ./Tirion/.git/hooks/commit-msg.sample                                                               0000755 0001750 0001731 00000001600 13547672423 017365  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to check the commit log message.
# Called by "git commit" with one argument, the name of the file
# that has the commit message.  The hook should exit with non-zero
# status after issuing an appropriate message if it wants to stop the
# commit.  The hook is allowed to edit the commit message file.
#
# To enable this hook, rename this file to "commit-msg".

# Uncomment the below to add a Signed-off-by line to the message.
# Doing this in a hook is a bad idea in general, but the prepare-commit-msg
# hook is more suited to it.
#
# SOB=$(git var GIT_AUTHOR_IDENT | sed -n 's/^\(.*>\).*$/Signed-off-by: \1/p')
# grep -qs "^$SOB" "$1" || echo "$SOB" >> "$1"

# This example catches duplicate Signed-off-by lines.

test "" = "$(grep '^Signed-off-by: ' "$1" |
	 sort | uniq -c | sed -e '/^[ 	]*1[ 	]/d')" || {
	echo >&2 Duplicate Signed-off-by lines.
	exit 1
}
                                                                                                                                ./Tirion/.git/hooks/fsmonitor-watchman.sample                                                       0000755 0001750 0001731 00000006377 13547672423 021151  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/usr/bin/perl

use strict;
use warnings;
use IPC::Open2;

# An example hook script to integrate Watchman
# (https://facebook.github.io/watchman/) with git to speed up detecting
# new and modified files.
#
# The hook is passed a version (currently 1) and a time in nanoseconds
# formatted as a string and outputs to stdout all files that have been
# modified since the given time. Paths must be relative to the root of
# the working tree and separated by a single NUL.
#
# To enable this hook, rename this file to "query-watchman" and set
# 'git config core.fsmonitor .git/hooks/query-watchman'
#
my ($version, $time) = @ARGV;

# Check the hook interface version

if ($version == 1) {
	# convert nanoseconds to seconds
	$time = int $time / 1000000000;
} else {
	die "Unsupported query-fsmonitor hook version '$version'.\n" .
	    "Falling back to scanning...\n";
}

my $git_work_tree;
if ($^O =~ 'msys' || $^O =~ 'cygwin') {
	$git_work_tree = Win32::GetCwd();
	$git_work_tree =~ tr/\\/\//;
} else {
	require Cwd;
	$git_work_tree = Cwd::cwd();
}

my $retry = 1;

launch_watchman();

sub launch_watchman {

	my $pid = open2(\*CHLD_OUT, \*CHLD_IN, 'watchman -j --no-pretty')
	    or die "open2() failed: $!\n" .
	    "Falling back to scanning...\n";

	# In the query expression below we're asking for names of files that
	# changed since $time but were not transient (ie created after
	# $time but no longer exist).
	#
	# To accomplish this, we're using the "since" generator to use the
	# recency index to select candidate nodes and "fields" to limit the
	# output to file names only. Then we're using the "expression" term to
	# further constrain the results.
	#
	# The category of transient files that we want to ignore will have a
	# creation clock (cclock) newer than $time_t value and will also not
	# currently exist.

	my $query = <<"	END";
		["query", "$git_work_tree", {
			"since": $time,
			"fields": ["name"],
			"expression": ["not", ["allof", ["since", $time, "cclock"], ["not", "exists"]]]
		}]
	END

	print CHLD_IN $query;
	close CHLD_IN;
	my $response = do {local $/; <CHLD_OUT>};

	die "Watchman: command returned no output.\n" .
	    "Falling back to scanning...\n" if $response eq "";
	die "Watchman: command returned invalid output: $response\n" .
	    "Falling back to scanning...\n" unless $response =~ /^\{/;

	my $json_pkg;
	eval {
		require JSON::XS;
		$json_pkg = "JSON::XS";
		1;
	} or do {
		require JSON::PP;
		$json_pkg = "JSON::PP";
	};

	my $o = $json_pkg->new->utf8->decode($response);

	if ($retry > 0 and $o->{error} and $o->{error} =~ m/unable to resolve root .* directory (.*) is not watched/) {
		print STDERR "Adding '$git_work_tree' to watchman's watch list.\n";
		$retry--;
		qx/watchman watch "$git_work_tree"/;
		die "Failed to make watchman watch '$git_work_tree'.\n" .
		    "Falling back to scanning...\n" if $? != 0;

		# Watchman will always return all files on the first query so
		# return the fast "everything is dirty" flag to git and do the
		# Watchman query just to get it over with now so we won't pay
		# the cost in git to look up each individual file.
		print "/\0";
		eval { launch_watchman() };
		exit 0;
	}

	die "Watchman: $o->{error}.\n" .
	    "Falling back to scanning...\n" if $o->{error};

	binmode STDOUT, ":utf8";
	local $, = "\0";
	print @{$o->{files}};
}
                                                                                                                                                                                                                                                                 ./Tirion/.git/hooks/pre-commit.sample                                                               0000755 0001750 0001731 00000003146 13547672423 017374  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to verify what is about to be committed.
# Called by "git commit" with no arguments.  The hook should
# exit with non-zero status after issuing an appropriate message if
# it wants to stop the commit.
#
# To enable this hook, rename this file to "pre-commit".

if git rev-parse --verify HEAD >/dev/null 2>&1
then
	against=HEAD
else
	# Initial commit: diff against an empty tree object
	against=$(git hash-object -t tree /dev/null)
fi

# If you want to allow non-ASCII filenames set this variable to true.
allownonascii=$(git config --bool hooks.allownonascii)

# Redirect output to stderr.
exec 1>&2

# Cross platform projects tend to avoid non-ASCII filenames; prevent
# them from being added to the repository. We exploit the fact that the
# printable range starts at the space character and ends with tilde.
if [ "$allownonascii" != "true" ] &&
	# Note that the use of brackets around a tr range is ok here, (it's
	# even required, for portability to Solaris 10's /usr/bin/tr), since
	# the square bracket bytes happen to fall in the designated range.
	test $(git diff --cached --name-only --diff-filter=A -z $against |
	  LC_ALL=C tr -d '[ -~]\0' | wc -c) != 0
then
	cat <<\EOF
Error: Attempt to add a non-ASCII file name.

This can cause problems if you want to work with people on other platforms.

To be portable it is advisable to rename the file.

If you know what you are doing you can disable this check using:

  git config hooks.allownonascii true
EOF
	exit 1
fi

# If there are whitespace errors, print the offending file names and fail.
exec git diff-index --check --cached $against --
                                                                                                                                                                                                                                                                                                                                                                                                                          ./Tirion/.git/hooks/applypatch-msg.sample                                                           0000755 0001750 0001731 00000000736 13547672423 020253  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to check the commit log message taken by
# applypatch from an e-mail message.
#
# The hook should exit with non-zero status after issuing an
# appropriate message if it wants to stop the commit.  The hook is
# allowed to edit the commit message file.
#
# To enable this hook, rename this file to "applypatch-msg".

. git-sh-setup
commitmsg="$(git rev-parse --git-path hooks/commit-msg)"
test -x "$commitmsg" && exec "$commitmsg" ${1+"$@"}
:
                                  ./Tirion/.git/hooks/pre-push.sample                                                                 0000755 0001750 0001731 00000002504 13547672423 017060  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh

# An example hook script to verify what is about to be pushed.  Called by "git
# push" after it has checked the remote status, but before anything has been
# pushed.  If this script exits with a non-zero status nothing will be pushed.
#
# This hook is called with the following parameters:
#
# $1 -- Name of the remote to which the push is being done
# $2 -- URL to which the push is being done
#
# If pushing without using a named remote those arguments will be equal.
#
# Information about the commits which are being pushed is supplied as lines to
# the standard input in the form:
#
#   <local ref> <local sha1> <remote ref> <remote sha1>
#
# This sample shows how to prevent push of commits where the log message starts
# with "WIP" (work in progress).

remote="$1"
url="$2"

z40=0000000000000000000000000000000000000000

while read local_ref local_sha remote_ref remote_sha
do
	if [ "$local_sha" = $z40 ]
	then
		# Handle delete
		:
	else
		if [ "$remote_sha" = $z40 ]
		then
			# New branch, examine all commits
			range="$local_sha"
		else
			# Update to existing branch, examine new commits
			range="$remote_sha..$local_sha"
		fi

		# Check for WIP commit
		commit=`git rev-list -n 1 --grep '^WIP' "$range"`
		if [ -n "$commit" ]
		then
			echo >&2 "Found WIP commit in $local_ref, not pushing"
			exit 1
		fi
	fi
done

exit 0
                                                                                                                                                                                            ./Tirion/.git/hooks/pre-rebase.sample                                                               0000755 0001750 0001731 00000011442 13547672423 017343  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# Copyright (c) 2006, 2008 Junio C Hamano
#
# The "pre-rebase" hook is run just before "git rebase" starts doing
# its job, and can prevent the command from running by exiting with
# non-zero status.
#
# The hook is called with the following parameters:
#
# $1 -- the upstream the series was forked from.
# $2 -- the branch being rebased (or empty when rebasing the current branch).
#
# This sample shows how to prevent topic branches that are already
# merged to 'next' branch from getting rebased, because allowing it
# would result in rebasing already published history.

publish=next
basebranch="$1"
if test "$#" = 2
then
	topic="refs/heads/$2"
else
	topic=`git symbolic-ref HEAD` ||
	exit 0 ;# we do not interrupt rebasing detached HEAD
fi

case "$topic" in
refs/heads/??/*)
	;;
*)
	exit 0 ;# we do not interrupt others.
	;;
esac

# Now we are dealing with a topic branch being rebased
# on top of master.  Is it OK to rebase it?

# Does the topic really exist?
git show-ref -q "$topic" || {
	echo >&2 "No such branch $topic"
	exit 1
}

# Is topic fully merged to master?
not_in_master=`git rev-list --pretty=oneline ^master "$topic"`
if test -z "$not_in_master"
then
	echo >&2 "$topic is fully merged to master; better remove it."
	exit 1 ;# we could allow it, but there is no point.
fi

# Is topic ever merged to next?  If so you should not be rebasing it.
only_next_1=`git rev-list ^master "^$topic" ${publish} | sort`
only_next_2=`git rev-list ^master           ${publish} | sort`
if test "$only_next_1" = "$only_next_2"
then
	not_in_topic=`git rev-list "^$topic" master`
	if test -z "$not_in_topic"
	then
		echo >&2 "$topic is already up to date with master"
		exit 1 ;# we could allow it, but there is no point.
	else
		exit 0
	fi
else
	not_in_next=`git rev-list --pretty=oneline ^${publish} "$topic"`
	/usr/bin/perl -e '
		my $topic = $ARGV[0];
		my $msg = "* $topic has commits already merged to public branch:\n";
		my (%not_in_next) = map {
			/^([0-9a-f]+) /;
			($1 => 1);
		} split(/\n/, $ARGV[1]);
		for my $elem (map {
				/^([0-9a-f]+) (.*)$/;
				[$1 => $2];
			} split(/\n/, $ARGV[2])) {
			if (!exists $not_in_next{$elem->[0]}) {
				if ($msg) {
					print STDERR $msg;
					undef $msg;
				}
				print STDERR " $elem->[1]\n";
			}
		}
	' "$topic" "$not_in_next" "$not_in_master"
	exit 1
fi

<<\DOC_END

This sample hook safeguards topic branches that have been
published from being rewound.

The workflow assumed here is:

 * Once a topic branch forks from "master", "master" is never
   merged into it again (either directly or indirectly).

 * Once a topic branch is fully cooked and merged into "master",
   it is deleted.  If you need to build on top of it to correct
   earlier mistakes, a new topic branch is created by forking at
   the tip of the "master".  This is not strictly necessary, but
   it makes it easier to keep your history simple.

 * Whenever you need to test or publish your changes to topic
   branches, merge them into "next" branch.

The script, being an example, hardcodes the publish branch name
to be "next", but it is trivial to make it configurable via
$GIT_DIR/config mechanism.

With this workflow, you would want to know:

(1) ... if a topic branch has ever been merged to "next".  Young
    topic branches can have stupid mistakes you would rather
    clean up before publishing, and things that have not been
    merged into other branches can be easily rebased without
    affecting other people.  But once it is published, you would
    not want to rewind it.

(2) ... if a topic branch has been fully merged to "master".
    Then you can delete it.  More importantly, you should not
    build on top of it -- other people may already want to
    change things related to the topic as patches against your
    "master", so if you need further changes, it is better to
    fork the topic (perhaps with the same name) afresh from the
    tip of "master".

Let's look at this example:

		   o---o---o---o---o---o---o---o---o---o "next"
		  /       /           /           /
		 /   a---a---b A     /           /
		/   /               /           /
	       /   /   c---c---c---c B         /
	      /   /   /             \         /
	     /   /   /   b---b C     \       /
	    /   /   /   /             \     /
    ---o---o---o---o---o---o---o---o---o---o---o "master"


A, B and C are topic branches.

 * A has one fix since it was merged up to "next".

 * B has finished.  It has been fully merged up to "master" and "next",
   and is ready to be deleted.

 * C has not merged to "next" at all.

We would want to allow C to be rebased, refuse A, and encourage
B to be deleted.

To compute (1):

	git rev-list ^master ^topic next
	git rev-list ^master        next

	if these match, topic has not merged in next at all.

To compute (2):

	git rev-list master..topic

	if this is empty, it is fully merged to "master".

DOC_END
                                                                                                                                                                                                                              ./Tirion/.git/hooks/pre-applypatch.sample                                                           0000755 0001750 0001731 00000000650 13547672423 020246  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  #!/bin/sh
#
# An example hook script to verify what is about to be committed
# by applypatch from an e-mail message.
#
# The hook should exit with non-zero status after issuing an
# appropriate message if it wants to stop the commit.
#
# To enable this hook, rename this file to "pre-applypatch".

. git-sh-setup
precommit="$(git rev-parse --git-path hooks/pre-commit)"
test -x "$precommit" && exec "$precommit" ${1+"$@"}
:
                                                                                        ./Tirion/.git/index                                                                                 0000644 0001750 0001731 00000001401 13551012627 013776  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  DIRC      ]�����]����� ��  ��  �  �   �/6��n5��4�0�2�� 	README.md ]�]N.0�]�]N.}�  �8�  ��  �  �  ��F�2�kt)9�$=�{���z��� )src/deprecated/post_process_19v2_rad.java ]�ډ0.�]�ډ0.� �9  ��  �  �  B<�Ga��-��f�Ɠ� src/main/MainMacro.java   ]�� �]�� � ��  ��  �  �  � 1۴$�"0#�7}b>Q��� src/main/PostProcessing.java      ]��V�a]��V�a �   ��  �  �  �G
[��b!
��[�`.dP:� 	tirion.sh TREE   u 5 1
�C7��s��6���5F#��src 3 2
��b.[� �;'��`U�.g�omain 2 0
JCe�V$��+[KE�X�b��deprecated 1 0
��9��aoɎ�y�P"�ŗREUC   �src/scripts/FullExport.java 100755 0 100755 Z���.���0i�s7A�Z���.���0i�s7A�src/scripts/PostProcessing.java 100755 0 100755 ���+?���z�0�өn����+?���z�0�өn��l�i>U
K?W O�۩���                                                                                                                                                                                                                                                               ./Tirion/.git/HEAD                                                                                  0000644 0001750 0001731 00000000027 13547672426 013411  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ref: refs/heads/master
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ./Tirion/.git/description                                                                           0000644 0001750 0001731 00000000111 13547672423 015222  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  Unnamed repository; edit this file 'description' to name the repository.
                                                                                                                                                                                                                                                                                                                                                                                                                                                       ./Tirion/.git/objects/                                                                              0000755 0001750 0001731 00000000000 13551012627 014401  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/a7/                                                                           0000755 0001750 0001731 00000000000 13547672426 014726  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/a7/7197d38116372d9f7d14b4216baa23b66dcf15                                     0000444 0001750 0001731 00000001002 13547672426 021706  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  xu�K��@��̯�;�M#�3�� Ź5������<��u�ȌȪ×iS׌m�>ˀ"ct:ə��4M��eM�!=��H�ϩ�(f
����;V�ϕU���/5~
�%�)�~�M���;A(k@�Y>܏�<���rH�ӥ鳶���C�X��W����cX�������-��|`��A�� ��xu��Ed�pqV���D�q�8�ζ�D�to��b/�5��&��A�_��zHR�,�k|�ݷnߩRdt�!\�T���Ƞ?�yK0�fpE�3/��H�u��7��a����I�YC��],�l��v
l1Dq���y���ז<�#a��R��C�2�|M�r��[��Y������<�7��^����A �mSF�ܱ{"fG)����p��Q�;\���C��h�N�|ݱ�Ec��~�p��ߪ��y�,N�bE:���������fHu��P����C-�,�Fj�Y ϭ�ᛙ��&&8���w1�����                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              ./Tirion/.git/objects/05/                                                                           0000755 0001750 0001731 00000000000 13547672610 014636  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/05/973d4953210704181da3a2b23a7dcb32566911                                     0000444 0001750 0001731 00000000143 13547672610 021364  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU�4`040075Up+��q�(�/*��J,Kd`�z�;��-����3g�;2χ*�/.	(�ON-.��K�('��1[�~���BU�>s\^�� j$A                                                                                                                                                                                                                                                                                                                                                                                                                             ./Tirion/.git/objects/53/                                                                           0000755 0001750 0001731 00000000000 13547672426 014646  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/53/087fae8f2b7d3b8512dd27063b527e60f48446                                     0000444 0001750 0001731 00000001044 13547672426 021647  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  xuQˎ�@ �3_�w��WC2�Z�E�[?x� �iQ���[�JUR�J�5u]
�d�K�i
xFu�d&�	T-*�4��tji:'O��(ELjI�^ AqՄ�P�­q�QM�%DQ�ap�A]"WQ4=�˳xRZ�x~���{^��zaM�@�H�PS�DVdY�v�g��n)>��^�>m��=/Eq����m�U����q�ظ��ܕ��GΏ/	ܾ0ö������u�N#�@ŧ��޻�6��<�9���w%��}�6�j�$���抳5\�Yw��y�f�t��D��g⚌t;�U�|<o[RS'D�K�>w���~�TZ��Z���g����*���&����'���\�d8ݩ��q�%���쎥|[���+y}���uo�v�*�g<T�e�C�OB�6j���&6vB�>8�,̊Z�`�|Wn��J_�S2ڧ��M�,f�Y��V��}b͞2��y��&�m<^Y��q�+�f�.��V�xf�侺t;*�2&<�63��Mo����g5��cҾ�D� r�Y���/�3ܟ                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ./Tirion/.git/objects/2f/                                                                           0000755 0001750 0001731 00000000000 13551012623 014704  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/2f/36dbe7a06e0f35e216bd34e930ee321188c21e                                     0000444 0001750 0001731 00000000255 13551012623 022023  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x-�1k�0����tI��5݂-���g��H:#�ɯ����눯���;zI��ڣְ#[�<�/2�yD
2elj�|��)i�K��醑2�ЈS����Vn���ow��`��)񊖍>���upOb�ѕl��������	NS(�p�2s4�L�˺� �_C�                                                                                                                                                                                                                                                                                                                                                   ./Tirion/.git/objects/07/                                                                           0000755 0001750 0001731 00000000000 13547672563 014647  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/07/5ae00d9eea2eda07add63017699a733741039f                                     0000444 0001750 0001731 00000000765 13547672563 022003  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�S�n�@�H���O6)[� �8T��A�R�Z;���z7�]�"Ŀ3�MʦE�5�ޛ7oǍ�T��/��a���󫛛�:�:{	�^���q'9��S!���GB�*Q�eu1��e{/w5�f��Tu� � ���xI崨lZ�A:�ڮ��Y��������V-�Zz2�a��@&7֤��q�'��������}�2/�"��ũA��C&M�\L��)�#;��2|#���kk�%��Tfa�&+�>���[��]
�r�����S�^ˠ���X�����` ;�mP���S�	�6����V=�0�3����_8k����W.��<N�&�#}���J����p������wX(���@!��M"��r�l�=�\>��Kih=������6������&�k#'����&#ai�
66��=>���Y�"��U��O�'N
^H�:m �ۙ�Y= G�$'
����q9cQM�~Ȋ3�β�Ǡ]���<D�           ./Tirion/.git/objects/c3/                                                                           0000755 0001750 0001731 00000000000 13547672610 014717  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/c3/cf70b54ba189933966129750b4a6c02298b54f                                     0000444 0001750 0001731 00000000306 13547672610 021560  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�OAn� �9��}��D���{�݀i���/�z�g���ؗ�Sa�K�̠g�S��١���(�9w0k���1�a��k�I�5��F�L��N�A��].�QY��@G�)�ң]�>���c�E��w������a�9*D��@���l���3�hO�Z~�7�[=|;*���;�Z���++��p�r�_K^�                                                                                                                                                                                                                                                                                                                          ./Tirion/.git/objects/e5/                                                                           0000755 0001750 0001731 00000000000 13547672426 014730  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/e5/0026fa5cec7bcb7730286ca7031bac80a03864                                     0000444 0001750 0001731 00000000135 13547672426 022030  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�;� @kO��,m��F���)���o�Ӎ�l��݀#���_!8]w���"T���|3�`+F��M�p�*���儡�8c?�M�	��                                                                                                                                                                                                                                                                                                                                                                                                                                   ./Tirion/.git/objects/b6/                                                                           0000755 0001750 0001731 00000000000 13551012577 014714  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/b6/268a8174a93ee8857450af9d5fca669dc1fc35                                     0000444 0001750 0001731 00000000307 13551012577 022076  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x���n� D{�+�ިZ`m@���_�S����PW����'�4z3�h&�Rre�[o̠(Xi�RZ�b˒Y7��v��I,��ä��wII����	q��Rb�rtGm��?�Z����3�p\�h��~]���G���`$j"7�;*D������.�����/�[�rYf.���K�%?9B�2Ԗ/��g8�G?��Ha[�                                                                                                                                                                                                                                                                                                                         ./Tirion/.git/objects/31/                                                                           0000755 0001750 0001731 00000000000 13551012550 014617  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/31/dbb424d72230230cb4377d623e51e985fbed85                                     0000444 0001750 0001731 00000010321 13551012550 021662  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�kO��~-���Օ� �cgv^�Z�ea)�2\]���N�TI����~��N�4ISh�0�ՊIb�����|l�^����|������E�d�xmw�`E��n��0N.Qؕq��ýu���$2�wF�v����Zoo,��g�Z2��KK^F��֭a���g��o^h|�7ju�~?�_/�X�9R�֋���`x�{]���8G�����B~�{�� ���{i����3q*�����v�"r{��s(Q�nC�mew�Hg�4�0p�_/5��Z�w�&RP5* ��Na��ۗGnr�*b��+���+����~OF��t#/����ӪP\_Nvw���;��O���(ԟ��̀��Ѫ��O��?����*�9��
�Y��U����9ϣ�N���nI�TzO�eT�#�+0��G<k����P���9� �y��X�������?�������]�F��]c�7I���?���(�ߊk��t�Vf�ɡTCo�-�~�X	��
$X�>R��:}�E
���\�Z���s[v��+Z���2��f98�˛���Ҋ�tE�R�Ϩb&��\W��eKWd�/��RQ+��������8�pW�r���_iE
T���b*�%�\um�Ie�u���y�����Jz ��b��cs�r�����m��X��\�Ii�KqyƏ���zi{���b�1��gS?e��r5��8����gx��������/o�c.��%n ��V7��(���x��9 �Y�W^�Ac�&a� ����O�����/�Cpo$r��ңXE���W�8aZ��뵂��J �v�ޠ6���0�z^�NP7}��Y5i��0� ���c�%��ߋuh�8�%OL��Q�Ŝf���4f�X&FG
����x�IX 
��j��Ń�F�@(����&��l=�D�+��Y��u}7�"��/�ޚ��y8L|/���EM��8k	��Jwn� �� ��#<����;A�w�K�߀��ދ���d$���&�U�(�Ǟ���o~u�P'c�1�$��:L�/6�\���b )Z<,��b�2p�h(�i�>z(�12?�f3=]\��������V%�����'ɀ��m� �ړD���Xơ��0���%7N{�E���v���w}�8�/_���1������Yh�F�0=��D���IaW+�N%��')u��t�C21���`��4lFpLV��cja�Q��A�n�'���0P������mL%M���f4�Օ�<�n ���^�� �a�ĭ�'��/�,���$L\��ȱ��Aȁ��q��)]9}��:���ء��83 ]�Q�!�7��[(WҢJ�~�ACc��8�)��uYS
�J8�r,��݌��A۬r�%]��i��ʤ��]��~+&-�1H��i���݉��~nb3s}_��E��P_}�����[q���=�?��u����_wU�D�{��'oX	�{�@�^�d�%��g7�Ӕu2t�A�ȿC|G`�cY�����g�JA�!��1މ�\���Yl�_��p��D4��r~Z�5�2��0o����{�g�+�;�Kg4��_�)Afԟ+{/b#鷒�C��Ћ��
���LB qS��?t���r{=G��k4�����폗&S�� KB|�  &mLF�d~�����(oc���R y�w�{9�`��͞��,�*s�郀�0`��W�o�]ح��I׃��$:M����Z��pm�(�f��*W��l0��N�h�Ц���[�f�3�W�45#��m��F���=�zɋ J��)l�^^�C?Q�)�-7y({��ۉ���0DW6JJ�	*mh�4P$(�6�<	�ý0�*9@�E_�lh$p����F�_���Ta����4����<r}�
�!�OXKa@!'Z2�9��Q��$�m�)�ԔX�jr����z����44Sc8 hm{}d,,3,�5�p @��wx��P�K0g��[��?��W�d���W;VP���Pӣ�o޽�Ts`���8���6� P`� �>Ehq� �IH�؟sTO�5J��J�`9�����z����� ����5���� ��B���::ļ6���u�F��WɰM��1.�ь��I�`-�p��O
��~��l��a������OCNQ ��a�W)��R�M� (�R����"���B;S?j!ee9!][oA`9u���1}�b
�%���]����Z��1�8�Y�j�ߗpH�{,�1&b�]�s�̠:�'���v��+�K�-��`���ީ�9���2-���3�˽�t-�+j�����/r�/�l)�3���6/����v�U��d_n��1�ѳ<�9&����	Vk[�b��x@�Y���*|��3v�+��/�Vg����d�z���م�ʾ��[wQ1U6�ꚽ����V'-�`u^�au��%	̌��9}�t��Le�dR��V�k��^��6�I�$����&AצM���&^�GR6Ɋ�,X�80[�~���I9N��|��2N$=���#��T|�u!��	*�UM����6iޒ��I�Tc���&���d���@Q��z	Vo�k�6l�/��7d��>圢w�UN�=���*^�����J�G�&�pÉ^�ys|��"��f$ǚ@�)G�k����J������t?�$Xx�TE��1���W�_%�o��nk��m�l�g��\h(���n3Ѩ}$���>�T���'�ͅW��P`����y��PY�w~�D�k��R���x�kS�K	dnt�UX�0dl�������<9�m��s���Z�/�"S�Y�'.ϑ������J��l���IA�[��ŃfׅR���ӷCM7��.���������87;��}����9J}ۦh��Te7�omA�u����-�#6D��B_�˿++T� ��d�D�k������S����af��FB�y��Xt�z�NT\!f7��0�  �ԵHB�'ë+���, >år�p��GM��F�r�����x� ��?m�=8A�v�jߜ���D�_LT��Ɇ�^��>�t2�������m�,��CZ,;I�yO�^��k瞃ŸqDK����%7*~�;�Ε�O�������Z����@wF�o��s�ߒ�[N�k~�K��l�o>�S�$����>[Û-�A1zc����D�����~��-�fEmy�8�͋u�/�.D�임ܒԗ�+�!�.�y��Ԙ��]��G���2�.��{+��w����0I�~Ҫ���TShMBL���@:d����L䧮��>������~I�N�! ��f�xFً� ��'\�	�_�$�h��'%oM<�nT��To�TͥX�<��V��}{.+is���n��>1S�u�^�}<�z!@�k}=ȡ�m<���4O*���Hm�pY�fI��P�Z,vW��<^s�y�>��7讖���a�0?;�E��)#�=|V�9->���Y�X���Z�Zk6n�Z��B�t���7}0@M�47��	
��@EN�_
��s�����~z�Z±A�1��
�ҝ�=�<� #k�N���be��eOW١�����
ݧ�f6~��h�o�L�K��'-'Wk�n@�Ɵ�6z1��s��������ŻF�,��k4F�)���$���=3?ny���?
��(��XF�A<1�Q�1R3� ��2y�ȴvBz^a;ʬN���(W1�C� �{Q���AP�Y�G=��6&�dh=�g�x��22[Ɏ1�<�R��Q�S�^��R���uO�a'����b�~[�e	p3�M�$x�I���4�ӏT$	6&)��l��8O�I��|�Ӗ��J~��nL&��ȥl0M���m����\�(/�!G+��D�h��ęI"���������Pٜ��jTBe�i�=�y46��1g+,I|
g�)*ۿA657P'�Dk��)����}�	ɧ}/F	�J+LV���p�F�E1�}��0��4� ����"4ꀠn`sr�%#�Dn���(ct �t�<w�o�ܮmM5�ϗ�s�<�_d�p��<O����6g���N���h8s����O!���c��%t�<)�	�_"��g�c-fQ.�e�+���;�٩�jv�Q�����Y�'#w���˄�~��l(2�N��X�.7­���)����xFF�(�`��rgb?����?Z�C                                                                                                                                                                                                                                                                                                               ./Tirion/.git/objects/b3/                                                                           0000755 0001750 0001731 00000000000 13547672677 014733  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/b3/043a9f21e174e440a7cd50b4ffe4d169906382                                     0000444 0001750 0001731 00000000253 13547672677 021722  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x���
�0�=�)rd�d�"���=�l��Z�i���#x��a����ؤ2v�*��L�0�&�/� ��쩰�% K��c񊕟M&��BCq�!h�Tpd�M�T�=-"���Ty�+<�&�ۏ�/��m��tH�|�:pz0�r
@t��l�������Ʋ,S�+�V�����1�Oi                                                                                                                                                                                                                                                                                                                                                     ./Tirion/.git/objects/d4/                                                                           0000755 0001750 0001731 00000000000 13551012623 014704  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/d4/433781fd73a8e6367fdb1de6e8354623d7e20d                                     0000444 0001750 0001731 00000000171 13551012623 021773  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU040a040031Qrut�u��Ma�7��|A��#��&/�	v�31 ��d������04X���J���~6h����BIfQf~�^q�;Wt��$E�c7�[��R�� L&r                                                                                                                                                                                                                                                                                                                                                                                                       ./Tirion/.git/objects/c6/                                                                           0000755 0001750 0001731 00000000000 13547672426 014727  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/c6/a328cc1c1f1beac358d7a884ef34749e0b8ce5                                     0000444 0001750 0001731 00000000173 13547672426 022305  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  xǱ
�0`�<�.����l]l��M!���rA|{��/d��
�$q5,�wW��(����J%�7��;څ�P���x�+V?=�]�IR�'K陰j?bU�Hş���+�                                                                                                                                                                                                                                                                                                                                                                                                     ./Tirion/.git/objects/4a/                                                                           0000755 0001750 0001731 00000000000 13551012577 014711  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/4a/4365ab560d249bfb2b5b4b45e95802ae62b2fe                                     0000444 0001750 0001731 00000000142 13551012577 022107  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU��d040031Q�M���ML.���J,Kd��枸{��S�G�z���f�,�U�_\P���Z\���Qnx{��u%e�-�Iv�/[�m ��%                                                                                                                                                                                                                                                                                                                                                                                                                              ./Tirion/.git/objects/0b/                                                                           0000755 0001750 0001731 00000000000 13547672610 014713  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/0b/90b29a97f429a5c815ce885745e33fd3fc450a                                     0000444 0001750 0001731 00000000073 13547672610 022006  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU01b040031Q�M���ML.���J,Kd8�V^����ɯ��7�׸��A`�
 �R�                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ./Tirion/.git/objects/46/                                                                           0000755 0001750 0001731 00000000000 13547672563 014652  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/46/ea32e86b742939b4243de77ba5cfc37a8bf2da                                     0000444 0001750 0001731 00000010630 13547672666 022234  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x��S����Jf�?�x�͈$8�����?P^3Sń��G���%�$�B;����}�N�%�	��!�yoo�n�n��:�gd}�����˗�w�}��������A��L�4�O�x@Ӵ�Gp<}`�0�hDή����l�;~��I0�3����O�<}�'q��Оf�����06��,Hڃx<�#��,Hi;�.�U�2`z6
d0
�ԚB����O�!�_`Ô�`�=�+��\y��9�I�Q��^��ګ8��t0ͨ���VV�׎�
�Z���'�U�Qb��h��%atA�`L�� �Ď��=6�±�� �ËiB��0����SA"�7�L��m��ɗ|�1因�''&�^F��hH�������b�C�����,n-���S�a�
��/Z��"�4�h�t��aa����c�r �#[_>ÿ�Y���8��o�����[���˛���h����ߒ޻��l��ߓ�6E�8m���N����(�P�S���!?��m���j&��]������� �)�2���įȍ(���	�	�ߖ裺?'�ϟ@�p�*��"ω���&��� @K�
�-��@����pP-�%��@�	 PA���r���2H P��)UK�e�8V-��bR ZM �Je�a I��G������To��O�(��g��9��D�d7Q�f����ϡH�r9���r 1� ͬ� �C �%�3��/�B�I�e �i��E,�QwF��� �=u����� >�̟W]�$�^�sʰ=H(8H������ ~�p��i���� ���7��;����[����Qd[2dB:���\S=4�z� v#�Ư5@�����q2#t �#["{�Ц�'�E��N|��,%S��l��w]Y�^X����k�[�$��vJ3c@1�9���*���^�M����6� Jb2�ޜ3��y���7��K�y�7FA�~�Z�a�Z/Hk���G�SK΄��1��a%f��X0�>�����(��nGÏA�)B�z����4!����,�,N���go�zr�H��an�2E_�FĵT��]T#^�a��I�3Her��F^�L��ۅ�!J3�����,�����A2ēk��&4�B��K�I#��q�ݮ
k
&�$I:�i<�sv}�٥�w_�r.���LË����z-�ʩ�����"���!�o�tD3��8eڼZ�;̪u���0�h2e6$���:�ɵMG#M~��p��ݸ�L=[III8Q���!w��0���Be��u8�LH��X/ �"F�Z{4� p������^�'Z��#�b}&m�}nK�Q��ۓ��bH�+�������Ti��v�!r���mid�قhd�g1qpֲP^��*��ie0E.��x%�ۑ2iKj�L����^�������M1���7y-�Y���i4�kr&>0�9R$!,|��k�DG�2�YcCm�m��a�?�@�2� ���|�Ҥ'
A��A��]�D�\�)�_3����\�J2_5D��Q��;z� ��n�k�#�����M�+v��l�*�}�ߟ
|>����0�w -`,���~C)B����o(H���e�I���E�8T-E�o��Z���Y��A'��"�u~�ވ���dpā�!ry����yЁ���tpob^t&��C�adj�C<T	qCۛ����V�6T��a%FP�ռno8�_��4K����P�œ*lU��KJGի�m�\9Jc-�	e����Us���6Ƕ���L�5Ծo�f�+f(�*��گ�U�*��گ��M��h�}�O�ҷ�C�T�~C��
���E�\^�IGCz�[�!깩]C���*)�bC��E{��Q�v�x�"�����>0&���ѡ�I��Ӣ� I􅶑,>�G<x��]���yC����+�#������[F��ϾE�I�o�5����Q�_ ���Fs���I�9G_NR��m����ua~b��u�/�L�륓�\��J���bΤkΤ+g�zU�_MwxH�8�l�@�!��:j!̉�B4�C�HfIX�tfk�Ho�թ�́��93di�9�:�ʵu̿�n��Q8�T0���F�� �(o}X�"�E�k�@$zr����~��tvn��vٍ�~�r�����ф}���`:�D��i�r�w�/t��N ˩)ń9/��M�+���)Z�ۘ�뀣�Ɖ��IHr��x &�}���^|���������M�#����*a�'&9���LA���t|�F6�z#,��[�)؋=�^�_�$�j/R�`+�#�,�0��;mP�tl��t���͎3.GB�k����������{��{��b�*M�����Nn�
�u��JPyK	Hk��������@�12���v����f�"�o��}u�W�nmzƈ6���;����g�ήP�Ak{]�D����ϟB�+]Q���GD�p �E���E[ǿ�$|A΃Q�r���'0SA��1ﮜqS���1L��b�bX�rὒ�'ʐ�zx�D˻�L��e��e�i#�k�6�ʶg��oD�����W�ff}��2,���J<��|:��f]:d��"�����u�w��A�I|<�%A��G�\e�K��w2^\}	<��0U�<���Z�Zy������@�	��9�Y���� �&'wu���	v��}O
�^�i���&����Y��l�i�GP+_�U߉���g�/��$'�3IN�ɋ��3�N������&�u\�^�� �q0GAx%?/%A�RX���$����LZo�,L��ܵ��A6�^�p�w��h�4k쉏k� Hu��a�5���#����{	�-����Rf���[�,H�vE���e��"�4m�3.��v��b˴���i��՚��J&4�F� ��\j%�����FaF�N����HM��d��p�;�a��,�۷i���<<z\��[�V�!0���3���#ryn�u��Ҫ��4nX��*���ڲ ҇�K�L+�P!�W@����#��e��^6ZLl����unS�{=��\2XLn�e*KV=��S�h�,��]%4L�uC_\7<5\��*vӐo���=��Կ#�W�..�B��r�D
��婼S�Wr97Y��9�Z�r��/�*N��ˉj�r��{�/��'r�dw����d��Yoڲ뢧�"�X����S������>
CS�V�x.<���^K}��H�/��Aߔ9�X�e���Ҝ�kKsF�2�����Sˍ|3����^ݝ�An��9��^��#˳�(I�Dϔeu�x�R˘���1[3^����;��j̪�f���n���c��,�k�̪f� ��^�$�-&(G0`���D(����e	 Y&خi� 0"���&�]L�,mD��=*n�������Y�,���(c>����CTaVD��a����W7���_8��!��\�ÎY�1n�	szVl�L�8#�fMcVs_Yȟ��a�g�ڢ�9��V��D�l��2Z~�g��5��ݜ�G���چÁZ=.��7�^i@��Oq���&�-z܇�^����l��f��c$rA�����ڣ6P��1<A���]S�΃����a�],gۄx���>�v6����+�E�G����d���k��f��c�PeC.�-v�������b�ѢgV����zW-�"B��'�lI�nrY�������ܛ��H�r��{�؍�¿t��PP����s�_�t��N�y-i.��t��n�εV�a���\�B��Q�ӈu�р�J�������b��>2����&�7����R�:���<�:�w�al�����;c���!��m/S���j�^.�N/^�܂��I7���0={���t|ª'�b��T��K*�T�Qyg���3�� �b,{�}���]��a#A1+>���.$�G>m�f;ju���@���B���ڧ���^dݹ#�2!�El��޲�)��Q2��=i$>���|鷒4��5�W@�Mש���)���k����u{�9�(MmU�Ɨt`����+}�5��Ab/W\	�]b������b#�^��Ԅ\�xƸa�gv>�o�g�
��e�7���5���u�Q�Y�+z�2� f�|$��º���d��j�coU�^ox�X�zj/8��Ӹ|F@�FykL\�E�l�.��$�zfE�yU�f
�����G��[�;e����3�4��|�����Z��;X�NQ�I�cb,y}h�)�VԌU���32��]i�mJ:Tq;S<f%����Y��K�l�B�H�|��n!w=O'�j��{��0w/���uW�k�-,��N���q�5�{w]���G�e���V�rAz����G�Yz��zm��|�uS�l��mF��"7؉/�!���VEl��3;� +8?�j�KyPA]��� x��y��� ��OH�                                                                                                        ./Tirion/.git/objects/24/                                                                           0000755 0001750 0001731 00000000000 13551012577 014632  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/24/d72ce23011184572e8e147489b0d72c439c86f                                     0000444 0001750 0001731 00000000171 13551012577 021474  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU040a040031Qrut�u��Ma8�X㌌�����W��7)������ ���.�Kҋ~��`��6+A t�^��|�榦
%�E��yz��\��s���݌nM�K	�� �''�                                                                                                                                                                                                                                                                                                                                                                                                       ./Tirion/.git/objects/c2/                                                                           0000755 0001750 0001731 00000000000 13547672563 014725  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/c2/ed1f2afdc75793ebce3f83df28dca4e0515ea8                                     0000444 0001750 0001731 00000003402 13547672563 022524  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�WQs�8���W,y�)�)s<���P��4����Ãbˉ�,�$9i��~�JN�6�f��֑���~��~�k;��'�=�e2�����㳳�#�E��3�
e���+1ĖK�Mm�*%K���gu���R�e���[-h�R�@WR�t.u��F�b!���á����J�G��xi��pya�ښ��n�m�,+m��_ͅ����������Ke��16�l^)-�",=�ѐ����[�:����mi��&/%y[K*���}���~�Rp���,�%����X۲��o�V�R�zi�r��k�%�?">��-V��X~��Ԑ0��5T���'N �$o\ْmU�ӣ�pشs�@-��!��A��R�"��`8�I���Ry�SM �l�ER�'ך�D��rְs�	��K����I~�ނ�����_ I��c��1n�@:��L��t���Ԣu�c4?Sn��d�;mE�-�W51�q/�;^�l)���7�n� ]̡����p��p����~=99?g1�g��?�Fj\����`^'xI��<���"���]B(@�I�5�H���ǘ&��y��6�~�E`�"��d	6NV�TW�饟t��+�j,�I/���5N�D�4E�l�tfMpVk��u���;7�@͂mw�.~N�A?p(~}����U%x(�6�l���m�x�W~)0�S��=�f���e��/b���!K��P|�7P�`}@�
�=��.������.&�Ź�з���<�-*�ܢ�dC��E���9�4u'b����e�G��&��Ҷ�%{df~g�uXŜmO�������vԣ^y>Fx�΍��R�Nq�J\Cz�_�B��"���e(��01@�����ü�c'��3d���,��"�����]�3�R�R�9y����	�^��c�᪺�uL#�޹�k�����41P�%L[�Q �N]��h|N�)�.ǀ���ɕ��� U#�4�G{�Q��z�s�ccy% cػ�l5z:C�*a��r�����n���̀� ��4�g#��Q��pA�9Ͻ��w1���n]t >�m����W�h]6΁�?iP�
;�.�1�����g�zF�evX���n��/�ڑ��(qL^D��4[���qI��[h����m������;�ⱂ��f#;��_
��ށ�<StG�qQQl�Q͢��YW�=�v��j/�Va-p1�0���@��EѺ<�{��"}��oЯy�u;�s�� �f���c�U�l	E��v�¢�q>��-\ZF��i�"Ą`'�t�6������)��by��e����0S�6����vd��N�X���$Q�� 8P�X��eXK��iC���#2'�Ŋ��O��X�s�d����+~y����_v��pf��$H��.�~(7�-p�ӟ-z�Yϱ�acВX�RZ��oUՍ\tU��H1����A�{�������O�.8g"��8�F�h�{:���V��E����YTh�86��c���d2�G��V�=�nK���Y�K�/��^�l�Nz���2J�K���Z��?_����LZ_|P�Bfx��_��,MhЭd��
7�}ӱhOdb�ߍ-g�c�F%���^�����9]t�+�S�ł��ߔ*��
,�M�\Ϣ,!��A�(�E���<�6&\��̚K��<(������!�rh-��F_.߿������s��b�"6+�Z���OƇ+H=(���LX:�s�Rk�u���� �:s�Li�����K�����0NE                                                                                                                                                                                                                                                              ./Tirion/.git/objects/69/                                                                           0000755 0001750 0001731 00000000000 13547672610 014650  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/69/cc7b4a4da79b06527ef3527e97ef96c6f201f7                                     0000444 0001750 0001731 00000000167 13547672610 022111  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU040a01 �Ԃ���Ē��K�-���M�?���Rzm�ҭ���LL�\]|]�rS�-�8##/��p��-�MJ�q�<��T\������/]%s�$/��L9t��  m5-A                                                                                                                                                                                                                                                                                                                                                                                                         ./Tirion/.git/objects/20/                                                                           0000755 0001750 0001731 00000000000 13551012623 014616  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/20/73ff0e864acc8570068538ce93699a6b211e2a                                     0000444 0001750 0001731 00000000066 13547672426 021651  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU06g040031Qrut�u��Maxʠ�+�M��r�����kX�  5�2                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ./Tirion/.git/objects/20/8270fb77663e53945b6ac4bd78f76765806668                                     0000444 0001750 0001731 00000000123 13551012623 021433  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU03g040031Qrut�u��Ma�7��|A��#��&/�	v�31 ��dY�7m�6\��#����C4 ���                                                                                                                                                                                                                                                                                                                                                                                                                                             ./Tirion/.git/objects/16/                                                                           0000755 0001750 0001731 00000000000 13547672610 014640  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/16/d2df39c71ddd616fc98ef5791bad5022dac597                                     0000444 0001750 0001731 00000000107 13547672677 022243  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU05a040075U(�/.�/(�ON-.�7�,3�/JL��J,Kdp{e�"�D�r������Wu� ���                                                                                                                                                                                                                                                                                                                                                                                                                                                         ./Tirion/.git/objects/pack/                                                                         0000755 0001750 0001731 00000000000 13547672423 015332  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/df/                                                                           0000755 0001750 0001731 00000000000 13547672426 015010  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/df/b53af870a139b03f16a675b945dafdea87bb7c                                     0000444 0001750 0001731 00000000066 13547672426 022363  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU06g040031Qrut�u��Ma8�X㌌�����W��7)���� ;��                                                                                                                                                                                                                                                                                                                                                                                                                                                                          ./Tirion/.git/objects/d3/                                                                           0000755 0001750 0001731 00000000000 13551012577 014713  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/d3/de622e5be000803b27869a601055902e67cd6f                                     0000444 0001750 0001731 00000000120 13551012623 021577  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU0�`01 ��Ԃ���Ē��K�-���M�?���Rzm�ҭ��!�r3���SW��������2�i]Ҧ +�                                                                                                                                                                                                                                                                                                                                                                                                                                                ./Tirion/.git/objects/55/                                                                           0000755 0001750 0001731 00000000000 13547672610 014643  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/55/f8eeabb5e945196dfc63a490e62355a60d4811                                     0000444 0001750 0001731 00000000115 13547672610 022000  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU03e01 ����<�	�fM����蹎p������rA����2J�X��z+��H�.^�ɪ��QX�  ���                                                                                                                                                                                                                                                                                                                                                                                                                                                   ./Tirion/.git/objects/47/                                                                           0000755 0001750 0001731 00000000000 13551012550 014626  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/47/0a5b880b9c62210ac6d95b8560132e64503a96                                     0000444 0001750 0001731 00000001355 13551012550 021451  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�TaO�0�����t����A��	V	�ѢiBr�3�p��v(������U���w��ݽ���3���y�bu%�)�Ϙ/:�0QNYұ�����}8eAZ#��Z3�Z�	0AV�!j��zd:�8��NU!�;�����lFu�B�0C�37(`�Bd����
�4|�\E�C���l�:���� j�"i�@��,��kd�����_�(!/,t�V�Ϫx��27T�n��W���x��TF��1��1�,�^M!~[�7 	�A�xU��P����n���`��dT�� �)5�1���gt#�a��+ ������@�j��`�n��%��&vݼ�%�w�6Xw��;�y��hՖĖ7"ͽ�y$��[Ȑ���x��kyi��x��b�ݭ���wR�}���	Vq��lm+;Gwm͵��q���w'�.�w�o������O�rR�`2�^*k��(P��c�m��֠�v�F��UC��*��9�je�]����,z7r�h���&�[� ���+�P��:&�2^3��	�]���f���������i��ןaȟ	��0>�#Z��$h��xƙ��H�ۚJ����V��ӥ�P,dڌݝX�6b/ɿM=��2��|bpQ�=赊���)��4j
�i��RbOͯ^N�6&f7����N7���4�-5�F�Z_"�fʧ���Q��q^n@�|�������*+n�z��d��(Q�=,�Ir�'d38-S��G��ߝ��                                                                                                                                                                                                                                                                                   ./Tirion/.git/objects/ee/                                                                           0000755 0001750 0001731 00000000000 13547672563 015012  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/ee/1301889b2b3f979fbf127ae330f308d3a96ea4                                     0000444 0001750 0001731 00000010113 13547672563 022062  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x��o�6��U�?>@i�r�>�v@�6�
,Mf�i�v0�N�ʒ!�Y����wǗH=l�u^�;,����Hދwu�����|�?���{�����;xLFn?
"�0Nz�(��8n��^� ؇�K��V�q���f�i?|0v���ʑ������hF	A�I���GP�z�^'n�쇣Q��ݘ6����b�O�}�O�����p�{��~�1�B�I�	�6="'4��0 ��v/r��&{�M"���O2��޹#��$���+i�����T����=������ຟ�@���	���dYX�@Oh?I��k�J��5�76Ζ��<�7������`�{�0N^����ko8��4�;�����O�o'Iz�A���z���h0�w�ۃI� x����=��n$Pnn��E�5ĕ��8�F^�]�}����I�O`:����	��P7 &H�~o�� �$��T,?�7�U����%�L�`oO/Wp���I-�VJ��n|y��_p��C�"X��M.%��@�k|�Q|%3z	�2ʁ�d\�� $��'	��P,˃`��o��a0�܄�
QLF���(ܖ,���u��^� ���$�z1R	"QP�H�� ]�lHҗlLP������T�;pa��x�OpD������/yD6���'��:ov�ߐ���^���C��=yC�o���{���]W��4Aj�	���kGf{� ���˗�&���W4��jI6.M�4��QDc�\r��,i�I��w�2�k/R�ͩ���c����4�yL$5l�Q��{>�M<k���hŲp �aDl.sL~�탐* B�����$�Q։�( ��|���-E�� Ƈ!P���[B`�#�h,��X�yIl��Zv��(9p0�0��~DA�ߎ�`C���ٌ"F�;��ŬQ��d�0�_���3��:L�8	�+@�j���p�H�W��4N�� Z�Hk���P���eoxD/���Ȼ��^��{В1���^��6+Ɏ(+L��Q��#Ʋ���1M���uَv��]a�``�0���Nqǥ�i$qF�h�Y2��:͖e��n�4PΣݾ�{d7�aw������QC�!1b�m��l	����0l@%��D鹾��\iJAoE�k/I�w�m�6 ��BhW�K����̫�."1|~2�5��r�c�PC{8���fL���G��R�k��.��pL;�&Ԙ����,�׌PYIN$D�W7�����1�|��\Q�F�E��W8�0A�DHM�ơ�0���K�i?u��T��+�..{��S9 ��h�=0�+��Ot�Hye@�I�jfc��g�����3?���� L2pv��}B��L|���R%�c[����G�=ؑbW_�ʥվ2cBW�7�ōeD:��q�����kª-������.r�����*\����*TyF�&�ꅿO� ��gv�;�����S��߈z�� �v��r�a�i��s�1y��U��Bl��n��߼8y�J�~!"R����4
���Ay��5H�7��t[�;.�6��ܿ�����,�n�J(�eK�7��\S�W�4�`�O`�҈�银5������b4�0H]"�_z��.�W�����!��7�xl�[-�!�H.iD��|���"A�x��¿�����Mհ��9��W��F�p�
��O][w�{��r|O�י��iM|�ш&Ѵ��g���r\����pI�?k�ڸF�1�>䱎��Q���證	��.�-�	�ʳ]Iut}V�du%W:m$�&N��ʑ�f����0Z,iN�hb��Y��x��5�e�Z� U��CK>J*JXA�g�
�����-`/M�U�)�-gM�UE� +`+�f���"V0��˪�v�G����$w��)ZB� ޢl�~�#U�֮���m�S�;��'�!�g��i��3��s��1H�iqtb��������@�/��B��ζN��GGvR��y����s����R�i�zO�zO�[��������E�I[�I[�$��I~���1�B���:����P�h`���2DZe���(�77�X���aQ{t����t.�[�L[�0a�������]ȓ�V ))�,FA/Щ�1�69'�➃�\2��ϲx=�S�^L����݉��hĢ�xg��/t��!5�҈:2,{ºg�VT��Tc���#���Q
d� ��-vA0�*� ��Y\� �Sq��1[�����6�,�]�申��\懡l�H��d!az�!��A��_^�('� P�V�X]j�Z���ޭ&(�R:vҜN����V�;\v�pW@�I�c�!xe�yf�=��k^���m6vr�-���R�;������/�G� �����o�=��>k<j��f1
��v��@�h���`�B$2�<��>}�{�������8��툴���,����q�_QL>��7d��0�a�ô�1ھN����;�P[mU	��ڼ�uUb5��i�dh)��&a�CP�~�0l�0�+���[I���V���{B��D�q�)ƍ/�a�RaY�C,�^�%�/#�uL�t��k��<��;t�sHlދ�G*��<�� F�E9��;b�.�;E����=B�<�/V��9�Y�J�Í�c��w��r�_��d��k�S�B.=Noj�z���(ͷ=-�8�m����W3ɨ����n��ͳ�>-}���K��;����Rm�q��9�Lg�ݩ�� �N{�����!�z�E����j<��$��7
�y���T W��B!�$H_"5�y9�=.���jf���A��~�����+��]m��s�l��2il��5i���͛�� � �}j��wE�����9˿������rsV(Ҝ�)!@[z���+s6󝓕 ���
B��A���4��./;�dye�V&�ȏ֏Ud�B9(3iP���Jꮠ*8�F������0��V��4hp
.��9\g�1+�@�pD�Rs�H��g�(ǚc�2f?�1�#U�UZ�n���\�ޖ)�D����d�~�l$�Tޫ4�y��(�0G��vg7���iL7:b���f�7[��vp Ij��NH�]}�9�o\Ѕ�5儉�a�,OΝl��3ݤ�}i
M�[	�)N�m���j�Ҵ�4���́��b;�-�<�e;� ٻ�vی�'�:�[�?3fwM��P^sN.�-���x������g�0B:�0_x��1_*Z�����C�f1~F����$9���0�g��N/%�b�n�}��ͨ׼!f�)-%e+�7oӗE�W?�k��L��^�����6�k�n*�ܧ�6�޺�g��'��o].�"_��aa��by�lTa���B��a��N���͢����8tq~��қ�#�Z�������_n��w�����p˜�Ü��*'`	�X�]�����<*����6�}���x���8�|^�95�(9�f�Ħβ)�Cf�3<��{i�:�!g/�q$�E�4�ZJV��͟$S`���"H��$	G�@�|G�� ����^��;,�l͚�}a�4[���՛�\�Շ���W���>_�k8xMɼ;r�N��l!�Y�q�#�82�� 3��J�H띑Q)��Ö2âzUMb�^t���*n�9*������֜����A�����@��%y*릝�%����	v�<z@prܯ_v�P�]s�tw��Iκ�3=2f$��{�>�t�U��������/?Ѷ�#5^?��L����λ��̳�|_%�b�a����5F� �;]}=��bHP�� >�¿����u0��J�'/�#qD_���Ag�#[6ы^��ڶ�y�w<�Zd�(��e5 ��m��g�����zi�'/xGU=r��Dݳo��v�^���<òrMhC����G)�=�|m�y�^v>��3�����&�\�TIf#��B��V?�G�<`���?�q��N�������ۑL�C~B� {&d߉h6���b��⹓$�����A�PK8 ��"��=|��X�r�Q"�?�=Ũ����Û|@z�m�!8~><B�=�M����&���8(M:eB����S(x����7K                                                                                                                                                                                                                                                                                                                                                                                                                                                     ./Tirion/.git/objects/info/                                                                         0000755 0001750 0001731 00000000000 13547672423 015347  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/5f/                                                                           0000755 0001750 0001731 00000000000 13551012623 014707  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/5f/e747783c25160880d0256c3a6bb61ddbc7ecf4                                     0000444 0001750 0001731 00000001052 13551012623 022036  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  xuQ�n�@ �g�bޫ�a`�]u8B�!'��pG$��M���~�lɒe�M]���1��g�ƒ$0Q H��0FQ��KXe(I�̵a�������3��1�`��D�JS�^"J�<���7=��r�"V����j��GV�E�+n�߀1�� �����}�X�bX\#�vnz�V����k��X�f�"?��膹c������ӿ}p`�(�B��R�U�VҘXQw�'�Oh=
�9Q�,L�ZbW>����EpAE��޻i���y�j� +��J���(f�ܼ���0턜�ǩ��z��e؋�8~���W9�M�����U�l]�t��7�g�ꎎ�܎'�?��}E-�R�_����s��*]l9p��}i�!Ҷ���/��U����t�a��㆖A��[�;'^��>u�S�v؞���<M��V�;��Q��w���V���{Z�]F��hk�����V�^�k��$*J�Q�$��ș�w�	����/�=8�o��q�=g�;�Y�t۾s�]�z���L_i�~�s�$��T��_u�S��                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ./Tirion/.git/objects/3c/                                                                           0000755 0001750 0001731 00000000000 13551012550 014701  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/3c/964761bba42de50ee2ee8d8b13669bc6931183                                     0000444 0001750 0001731 00000005321 13551012550 021765  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�Ymo���W�W�10B96�E8����`�F�-|AA�Kic���.%����>3KJ��p��_d�ܝ�gf�]Os3����ի?��4�}������qR�c��uy�_�/�"b�el]�TgZ�4}�O�ޙ�>�\-(<7u��1M\���ѭ�:Sy�F�*N���_���L�%G��y���ɳR��G���X�Qb��O��UQ�d5uBc}b�*Wi���o�j-A����u9��J���\E7��[x>�ߧ!��I'�MS[�(����:��*E��Ҹ�G�f:�?}�"W��ޒ34UOs�_�6���b�n�h97x��q��"�s�'�
����W��w���g:��%�ei\�))CfY�Hq��SK���d]�e�t<V�4��I[���;U���k�w8$l��kK6�u�;D��{n�nJQ�ʅ�M��i�;���\������/��<֮�s�O�E!��m��!<�!-�i�ꅪ�X��YS�"~0��t�]���r�XY]�Sֲ����<.��E�����p���真���8�wO�������!۔��(k���B	�h�!g�Ҕ:�#GV�LO%��g7�R9��B�T$	Qs�e
���h�\D�(r��Ԛ�q�_��v�$��W�/*q����@/�,�7���O����Ƿ����Z�L��[���_���֋�)�B1u֟���&��>���cH��ML��N��*xyZkگ�ͧFY����}ŭ��x7y�N8V��=���Q���`]�%��ۄ#�+`at
ثaG��p )�]U�U�4��P�$��,I�8Qm�1�B�e@G�6Ժ�=V�`�[��]Xsc,;�(k�nsbg���=p/�V�nnV��Z7�E��*�K��`"���L�.�TE&�2]9�UŅT+�>'�ɋ�����Ȁ�l����v��"a^�����A9Z��z�`�B��Xĵ�Fj�K	.:��y����I��X? <�I~C�������`#Q`%��r�i��ΰn���A,��n����}�3@g���@���ʞ�^ð��O�i���)����e�.Fs4�Z��J��t�U��46G�+�xVla0��Oqt�6�����Q{k�=���ȕ���-A��|	|�,XRrH�'��B�M~����9R�O�P�s���	�8߰<���AcW�$��5��<�ʵ��K�ew��O�`.�WM�/4u8���5�{�oG�ـ�����1���b����i�j�c�E|��@'�!@�@oP�XP�+m��q����rO׃��F�ɉ3���]XYczgMx@+�=ʑg<�$�eVʬ�tVwDL��"2u
�L�Xu�;]��&s���5���e. U&I�:���r3����=�k�Q��w�k�όlcvjR�D��XM3����W�ʕ�0��`!�+�hD`�7%��ep�"pG&���x���$˘OI���i��׭tOB"1.�℉$���#���rK�Zh �h������6^07b~g)�`���D���;�B�N�V�;J�����������n	�C����V&}����r	4,t����>ˊJ��
�/:�����j&���LD����t��D�'�����v#�w��_�z�y�
6�<����E��*�n��L�^��"N��+S��ى�?Ws�	̡�2p���Y�Q?,{��T8w��U��m[}�������IN?;��k8�ۏ'�����6�rń}^�uۆ�x�x�)jӎ>��qp�&��p�I�F	*3H���(7��L���XT�LDq{��L�����R�\{.E�z��4�Y���o�B�}[�Z�{�N��s�nM=��\gJn�3���m�UU����^O"]���:��.�}���F�
~�9�c����ꅷN4���B'�7�q�t��Y�*l}i�Q2�����w��O��ťZwE��~�FQq���B�b.���������	'���o��*�T~\�T���c�7���b�8ڼB.�`ݻs7@_�H��Í��;'��nE�Dڦ, l��$x���v>r!�9���S�T/����&�K0:�� (�p��<�qa�>���'l�ۆ���(/��8��>>v�0QNb���zp�$_8I���.d~��U=�����|��$ �ۭ��1	�Y5&�;�@Mw�%w%�k9Ln��;���8o�}w1���G�p�}���6m������)����K&$��1�Ұ�A����̎{�=��|/�ݫH�������å\]��VMM������q���(qZ&*����>Nc�O�����*���{.�B���}��)��$���l3����i��Λ�D��6`�&�(�vZ,�;�K�ƒ��o#`�4BSe�M̦=p?��(8��+o��ؿ�I��-��ԔJm��Z���F3AcH%��<8�O��A al�5�<��e��P�mā?�Ǭ�}��rȎ���m<�X|%pB%�^������B�g'�F񧝹]��� �i�8��=�����^J���^�*��C�dY��^q�Q�G��0q)���S��[�h���Kc��3�F?v�������y/���q��R�����8Mq%gJ)t�!�_�	����uBp�
���tu���1b�F��,]^������`\��ׄ�B
cX�p�l
-J3���.�p����v�/���U�3M��ߣ��� �Yv�%'�׎v����� kz�                                                                                                                                                                                                                                                                                                               ./Tirion/.git/objects/73/                                                                           0000755 0001750 0001731 00000000000 13551012627 014632  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/73/37ff846aaa6f950baf1d10c6ac653fcdd6e1a3                                     0000444 0001750 0001731 00000000342 13551012627 022331  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x�OKj�0�ڧ�.�B-�I��%�^��ҽ>O�hlE��kz������u�K �Uf�� �r"���r
*1�kPC"�2u7_yi"�F�"�`vΒ���!�=␢���f&C� j�P:'��#xUJ!�l:�h�Z�w�i�7qޞl;��g_�oq�/BYR�UV�J-e�������>��,B�K��i��=�$�,��n���K������ρ�W�e]�_$_i�                                                                                                                                                                                                                                                                                              ./Tirion/.git/objects/1d/                                                                           0000755 0001750 0001731 00000000000 13547672677 014733  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/1d/02b0d8513c1ab0d297285c1053ea8576481557                                     0000444 0001750 0001731 00000000160 13547672677 021467  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU040b01 ��Ԃ���Ē��K�-���M�?���Rzm�ҭ��!�r3��'l�5������:�]�_����.N.�,()f`�n����"!�x�&���Fa�� &l)�                                                                                                                                                                                                                                                                                                                                                                                                                ./Tirion/.git/objects/bd/                                                                           0000755 0001750 0001731 00000000000 13547672677 015014  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/objects/bd/bb25953b2948f4450e7de8bfe82460b6b5dd7e                                     0000444 0001750 0001731 00000000123 13547672677 022241  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  x+)JMU03g040031Qrut�u��Ma8�X㌌�����W��7)������ ���d�6����pi�F�@��2�p �M�                                                                                                                                                                                                                                                                                                                                                                                                                                             ./Tirion/.git/packed-refs                                                                           0000644 0001750 0001731 00000000162 13547672426 015074  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  # pack-refs with: peeled fully-peeled sorted 
53087fae8f2b7d3b8512dd27063b527e60f48446 refs/remotes/origin/master
                                                                                                                                                                                                                                                                                                                                                                                                              ./Tirion/.git/info/                                                                                 0000755 0001750 0001731 00000000000 13547672423 013716  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/info/exclude                                                                          0000644 0001750 0001731 00000000360 13547672423 015271  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  # git ls-files --others --exclude-from=.git/info/exclude
# Lines that start with '#' are comments.
# For a project mostly in C, the following would be a good set of
# exclude patterns (uncomment them if you want to use them):
# *.[oa]
# *~
                                                                                                                                                                                                                                                                                ./Tirion/.git/FETCH_HEAD                                                                            0000644 0001750 0001731 00000000140 13551012623 014274  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  5fe747783c25160880d0256c3a6bb61ddbc7ecf4		branch 'master' of https://github.com/VikVelev/Tirion
                                                                                                                                                                                                                                                                                                                                                                                                                                ./Tirion/.git/refs/                                                                                 0000755 0001750 0001731 00000000000 13547672426 013725  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/refs/remotes/                                                                         0000755 0001750 0001731 00000000000 13547672426 015403  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/refs/remotes/origin/                                                                  0000755 0001750 0001731 00000000000 13551012662 016653  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/refs/remotes/origin/HEAD                                                              0000644 0001750 0001731 00000000040 13547672426 017310  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ref: refs/remotes/origin/master
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ./Tirion/.git/refs/remotes/origin/master                                                            0000644 0001750 0001731 00000000051 13551012662 020065  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  7337ff846aaa6f950baf1d10c6ac653fcdd6e1a3
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ./Tirion/.git/refs/heads/                                                                           0000755 0001750 0001731 00000000000 13551012627 014773  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  ./Tirion/.git/refs/heads/master                                                                     0000644 0001750 0001731 00000000051 13551012627 016205  0                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                  7337ff846aaa6f950baf1d10c6ac653fcdd6e1a3
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ./Tirion/.git/refs/tags/                                                                            0000755 0001750 0001731 00000000000 13547672423 014660  5                                                                                                    ustar   viktorv                         users                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  