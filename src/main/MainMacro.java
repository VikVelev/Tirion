// STAR-CCM+ macro: MainMacro.java
// Last Modified by Viktor Velev (Formula Student Team Delft)
package macro;

import java.util.*;
import java.net.*;

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

	Class CFDPipeline;

	public Simulation simulation;
	/** [JVMH] For future Inter cross-JVM communication hacks */
	//public String projectPath = "/home/viktorv/Projects/FormulaStudentTeamDelft/CFDPipeline/";
	private MeshPipelineController meshPipelineController;
	private SolverStoppingCriterionManager solverCriterionManager;

	public void execute() {

		/** The whole sequential CFD pipeline */
		// [JVMH] manageImports();
		initialize();
		runPostProcessing();
		runSimulation(maximumIterations);

		/** 
		 * You can just comment out any step of the pipeline and the rest will work 
		 * (keep initialization though) 
		 * */
	}

	private void initialize() {
		/** Get simulation environment, and initialize global variables */
		
		simulation = getActiveSimulation();
		meshPipelineController = simulation.get(MeshPipelineController.class);
		solverCriterionManager = simulation.getSolverStoppingCriterionManager();
	}

	private void runSimulation(int maxIterations) {
		/** 
		 * Get the needed criterion, modify them and run the simulation 
		 * The simulation will stop if the number of iterations has reached 
		 * the previously specified "maximumIterations" (class global)
		 * */
		
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

	private void manageImports() {
		/** [JVMH] For future Inter cross-JVM communication hacks */

		log(Paths.get(".").toAbsolutePath().normalize().toString());
		log("file://" + projectPath + "/scripts/CFDPipeline.java");
		log("STUB!");

		// [JVMH]
		// URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {
		// 	new URL("file://" + projectPath + "/scripts/CFDPipeline.java")
		// });
		
		// CFDPipeline = urlClassLoader.loadClass("deep");
	}

	public void log(Object x) {
		/** A logging utility for debugging and additional information. Needs initialization beforehand */
		System.out.println("[LOG]: " + x); // This logs in the terminal if you've run STAR-CCM through shell.
		simulation.println("[LOG]: " + x); // This logs in the output window in the program itself.
	}
}
