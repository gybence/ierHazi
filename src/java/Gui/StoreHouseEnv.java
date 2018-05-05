package Gui;
// Environment code for project ierHazi

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.util.Random;

public class StoreHouseEnv extends Environment {

	public static final int maxCapacity = 100;
	private GUI gui;
	private int numOfForklifts;
	private int numOfTrucks;
	private int capacity = 100;
	private boolean isRunning = true;
	private boolean truckAtEntry = false;
	private Random randomGen;
	private String lastArrivedTruckName;

	private Logger logger = Logger.getLogger("ierHazi." + StoreHouseEnv.class.getName());

	public StoreHouseEnv() {
		randomGen = new Random(2); // ez pont jo seed 2,1,3
		gui = new GUI(this);
		numOfForklifts = 2;
		numOfTrucks = 3;
		
		generateTrucks();
	}



	@Override
	public boolean executeAction(String agName, Structure action) {
		if(action.getFunctor().equals("vmi")) {
			addPercept(agName,Literal.parseLiteral("vmi"));
			logger.info("addPercept lefutott"); 
		}
		//TODO if else-be a tobbi environment fele meno jelzes es pls ezt a getFunctort hasznaljuk
		//Term t = action.getTerm(0); //igy kell kivenni a parametert ha van
		else
			logger.info("Executing an action which is not implemented: "+ action);
		
		informAgsEnvironmentChanged();

		return true; // the action was executed with success
	}

	/** Called before the end of MAS execution */
	@Override
	public void stop() {
		super.stop();
	}

	public int getNumOfForklifts() {
		return numOfForklifts;
	}

	public int getCapacity() {
		return capacity;
	}

	public void openGate() { //gombnyomas kezelo fv
		if (truckAtEntry) {
			addPercept("entryGate", Literal.parseLiteral("opengate(" + lastArrivedTruckName + ")")); 

			// ----------------------------------------------------------------------TESZT
			// addPercept("forklift1",Literal.parseLiteral("put"));
			// ----------------------------------------------------------------------TESZT

			truckAtEntry = false;
			gui.entryButton.setEnabled(false);
			gui.out("Truck entered!");
		}
	}
	
	public void truckArrived() {
		if (!truckAtEntry) {

			// TODO: nem jo a random, vhogy meg kell jegyezni melyik kamionok vannak epp
			// megerkezve
			// es a nem megerkezettek kozul valasztani.. but its something
			int truckNum = randomGen.nextInt(numOfTrucks) + 1;
			String agName = "truck" + Integer.toString(truckNum); // osszeallitja melyik truck agens erezze magat ugy h
																	// eppen megjott

			//jelzes az agensek es az UI fele
			addPercept(agName, Literal.parseLiteral("arrived"));

			lastArrivedTruckName = agName;
			truckAtEntry = true;
			gui.entryButton.setEnabled(true);
			gui.out("Truck waiting at entry!");
		}
	}
	
	private void generateTrucks() {
		Thread truckThread = new Thread(() -> {
			try {
				while (isRunning) {
					
					truckArrived(); //ez a szal idonkent megerkeztet egy kamiont a raktarhoz
					
					Thread.sleep(10000);
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("InterruptedException caught", e);
			}
		});
		truckThread.start();
	}
}
