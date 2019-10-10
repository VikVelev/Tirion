// STAR-CCM+ macro: full_export.java
// Written by STAR-CCM+ 11.04.012
package macro;

import java.util.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
 

public class FullExport extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {
	  
	String name_path;
	String Name_Sim;
	String MainFolderName;
	String Path_Sim;
	String name_figure;
	String save_name;

    Simulation simulation_0 = 
      getActiveSimulation();
	  
	Name_Sim = simulation_0.getPresentationName();
    Path_Sim = simulation_0.getSessionDir();
	
	MainFolderName = Path_Sim + "\\Plots_" + Name_Sim;
	
	new File(MainFolderName).mkdir();
	
	for (StarPlot plot : simulation_0.getPlotManager().getObjects()) {
		
		name_figure = plot.getPresentationName();
		
		MonitorPlot monitorPlot_12 = 
			  ((MonitorPlot) simulation_0.getPlotManager().getPlot(name_figure));

		monitorPlot_12.export(resolvePath(MainFolderName + "\\"+ name_figure + ".csv"), ",");	
	}
  }
}
