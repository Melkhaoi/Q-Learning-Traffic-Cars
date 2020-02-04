import interfaces.Car;
import interfaces.LearningModule;
import interfaces.RoadMap;
import interfaces.TrafficLight;
import utils.Coords;
import utils.Velocity;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * Class to run simulation with discrete intervals
 */
public class Main {
	//public static double cc = 0;
	//public static double dd = 0;
	public static double s = 0;
	public static double id = 0;

	public static double M_V1 = 0;
	public static double M_V2 = 0;
	public static double M_V3 = 0;
	public static double M_V4 = 0;
	public static double M_V5 = 0;
	public static double M_V11 = 0;
	public static double M_V22 = 0;
	public static double M_V33 = 0;
	public static double M_V44 = 0;
	public static double M_V55 = 0;
	public static int droit1 = 0, droit2 = 0, droit3 = 0, droit4 = 0;

	public static Double[] pcv = new Double[10];

	public static void main(String[] args) {
		// Inputting arguments, or defaulting to
		// reward 1 and intensity 0.25
		Integer rewardFunction;
		Double trafficIntensity;
		int defaultRewardFunction = 1;
		double defaultTrafficIntensity = 0;
		rewardFunction = defaultRewardFunction;
		trafficIntensity = defaultTrafficIntensity;
		List<String[]> intensityList = new ArrayList<String[]>();
		List<String> APITime = new ArrayList<String>();

		boolean vary = false;
		boolean graphicalOutput = true;
		boolean consoleOutput = false;

		int intensityRange = 5;
		System.err.println(" sssssssssssssssssssssssssssss    " + args.length);
		if (args.length == 1) {
			vary = true;
			try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {

				String sCurrentLine;
				// Consume first line
				sCurrentLine = br.readLine();
				while ((sCurrentLine = br.readLine()) != null) {
					String[] tmp = sCurrentLine.split(",");
					String time = tmp[0];
					String[] currIntensity = Arrays.copyOfRange(tmp, 1, tmp.length);
					intensityList.add(currIntensity);
					APITime.add(time);
				}

			} catch (IOException e) {
				graphicalOutput = true;
				String[] currIntensity = args[0].split(",");
				intensityList.add(currIntensity);
				System.out.println("Fetched Traffic intensities are " + args[0]);
				APITime.add("Now");
			}
			intensityRange = intensityList.size();
		}

		// Simulation parameters
		final int TIMESTEP_INTERVAL = 500;
		final int TESTING_INTENSITY_INTERVAL = 500;

		int SIMULATION_TIME = TESTING_INTENSITY_INTERVAL * 1;
		// int SIMULATION_TIME = TESTING_INTENSITY_INTERVAL * intensityRange;
		final int STEP_TIME = 100;

		// Graphics and runtime parameters

		boolean output = graphicalOutput || consoleOutput;
		int score = 0;

		if (!output) {
			System.out.println("Traffic Training and Simulation running... ETA : 10 Minutes");
		}

		int[] training_times = { 10000000 };
		for (int time : training_times) {
			final int TRAINING_TIME = time;
			final int TRAINING_INTENSITY_INTERVAL = TRAINING_TIME; // Change 5 to 4 , to skip 1.0 training intensity

			// output parameters
			long iterations = 0;
			long totalCars = 0;
			long totalCarsStopped = 0;
			long tmpCars = 0;
			long totalCarsStopped1 = 0;

			int maxCarsStopped = 0;

			// AVERAGE ITER
			final int BIG_ITER = 1;

			// Initialise map, list of cars currently on map, and list of
			// trafficlights
			RoadMap map = new RoadMapImpl();
			List<Car> cars = new ArrayList<Car>();
			List<Car> Game_cars = new ArrayList<Car>();
			List<TrafficLight> trafficLights = new ArrayList<TrafficLight>();
			trafficLights.add(new TrafficLightImpl(new Coords(20, 20), false));
			trafficLights.add(new TrafficLightImpl(new Coords(20, 40), true));
			trafficLights.add(new TrafficLightImpl(new Coords(40, 20), true));
			trafficLights.add(new TrafficLightImpl(new Coords(40, 40), false));

			// Set actionposition based on arg1
			int actionPosition = 100;
			switch (rewardFunction) {
			case 1:
				actionPosition = 100;
				break;
			case 2:
			case 3:
				actionPosition = 100000;
				break;
			}
			Viewer v = graphicalOutput ? new Viewer() : null;

			// Simulation tests
			int currentTimeStep;

			// Basic logic for each time step
			// - change traffic lights if required - call a function from
			// 'learning' class to do this
			// - move cars in their current direction by velocity (modify
			// velocity if necessary - using CarAI)
			// - spawn cars at extremities
			// - Now that we have the new state, update the qvalue for the
			// previous s,a pair

			// TRAINING TIME

			File varTmpDir = new File("./20k");
			boolean exists = varTmpDir.exists();
			LearningModule learningModule;
			int timeRan = 0;
			
			System.err.println(exists );

			if (exists ) {
				try {
					FileInputStream fi = new FileInputStream(new File("./20k"));
					ObjectInputStream oi = new ObjectInputStream(fi);

					learningModule = (LearningModule) oi.readObject();

					oi.close();
					fi.close();
				} catch (Exception e) {
					learningModule = new LearningModuleImpl(actionPosition);
					e.printStackTrace();
					System.exit(-1); // Can be removed for skipping training.
				}
				learningModule.setEpsilon(-1);
				// KEY = Intensity;Timestep VALUE = sum of avg wait-times
				HashMap<String, Float> hmap = new HashMap<String, Float>();

				for (int iter = 0; iter < BIG_ITER; iter++) {
					trafficIntensity = 0.0;
					M_V1 = 0.0;
					M_V2 = 0.0;
					M_V3 = 0.0;
					M_V4 = 0.0;
					M_V5 = 0.0;
					cars.clear();

					// Simulation time

					int index = 0;
					String[] currIntensity = { "", "", "", "", "", "", "", "" };
					String currTime = "";
					for (timeRan = 0; timeRan < SIMULATION_TIME; timeRan++) {
						 

						if (timeRan % TESTING_INTENSITY_INTERVAL == 0) {
							if (vary) {
								currIntensity = intensityList.get(index);
								currTime = APITime.get(index);
								index += 1;
							} else {
								// System.err.println("0000");
								trafficIntensity += 0.2;
								cars.clear();
							}
							totalCarsStopped1 += totalCarsStopped;
							tmpCars = totalCarsStopped;
							totalCarsStopped = 0;
							// Clearing all cars after every intensity interval

						}

						// Params required to learn
						RoadMap currentState = map.copyMap();
						currentState.addCars(cars);
						Boolean switchedLight;
						int state;
						int nextState;
						int reward;
						Random rand2 = new Random();

						for (Car c : cars) {
							
							if (c.atInt()) {

								if (c.getS() == 0) {
									c.setState(currentState.stateCode(c, currentState));
									// Use the learned values to update the traffic lights
									c.setAction(learningModule.updateTrafficLights(currentState, c, timeRan));
									// c.setStrategy(rand2.nextInt(2));
									c.setS(1);
								}

								else {
									if (TRAINING_TIME != 0) {

												if(c.getStateAccident()==1) {
												if (c.getStrategy() == 0) 
														c.setReward(-100);
													else
														c.setReward(0);
												c.setStateAccident(0);
												} else {
													
													if (c.getStrategy() == 0) 
														c.setReward(10);
													else
														c.setReward(0);
												}
											
										// Updates q-values
										// calculate reward and state code for each traffic light
										// reward=learningModule.reward(currentState.stateCode(c,currentState));
										c.setNextState(currentState.stateCode(c, currentState));
										
										
									//	if(c.getId()==1) 
									//	System.err.println("state: "+c.getState()+" strategy: "+ c.isAction()+" reward: "+ c.getReward()+" nextstate: "+ c.getNextState());
										

										learningModule.learn(c.getState(), c.isAction(), c.getReward(), c.getNextState(),
												c);
										//	 System.out.println("state: "+c.getState()+" strategy: "+ c.isAction()+" reward: "+ c.getReward()+" nextstate: "+ c.getNextState());
										// System.out.println("State:"+c.getState()+" NextState:"+nextState+"
										// reward:"+c.getReward());
										c.setState(c.getNextState());
										c.setAction(learningModule.updateTrafficLights(currentState, c, timeRan));
										//if(c.getId()==1) 
										

									}
								}

							}

						}
					

						/*
						 * if(Game_cars.size()!=0) { //Save the states of each traffic light before
						 * updating for(Car c:Game_cars){ if(c.getNstate()==1) {
						 * state=currentState.stateCode(c,currentState.getClosestTrafficLight(c,
						 * trafficLights)); //Use the learned values to update the traffic lights
						 * switchedLight = learningModule.updateTrafficLights(currentState,c,
						 * trafficLights, timeRan );
						 * 
						 * } }
						 * 
						 * }
						 */

						for (Car car : cars) {
							for (Car car2 : cars) {
								if (!car2.equals(car) && car2.getCoords().equals(car.getCoords())) {
									car2.setAccident(true);
									car2.settest(1);
									car2.setStateAccident(1);
								}

							}
						}
						for (Car car : cars) {
							if (car.isAccident()) {
								car.setAccident_time(car.getAccident_time() - 1);

							}
						}

						for (Car car : cars) {
							if (car.getAccident_time() == 0) {
								car.setAccident(false);
								car.settest(0);
								car.setAccident_time(20);
							}
						}

						// copy updated state of map
						// RoadMap nextState = currentState.copyMap();

						// Move cars currently on map
						List<Car> carsToRemove = new ArrayList<Car>();
						/*
						 * for( Car c:cars) {
						 * 
						 * int i=0; //System.out.println("Cat:   "+c.getCatAt()); if( c.getCatAt()==0) {
						 * 
						 * Random rand = new Random(); // int s; // if(rand.nextInt(10)<5) s=0; // else
						 * s=1; c.setStrategy(rand.nextInt(2)); c.setCatAt(1);}
						 * 
						 * 
						 * // System.out.println("s: "+c.getStrategy()); i++; }
						 */
						droit1 = 0;
						droit2 = 0;
						droit3 = 0;
						droit4 = 0;
						for (Car car : cars) {
							if (car.gettest() == 0) {

								car.move(currentState.getClosestTrafficLight(car, trafficLights), currentState);

								int x = car.getCoords().getX(), y = car.getCoords().getY();
								if (x < 0) {
									car.getCoords().setX(59);
								}
								if (x >= 60) {
									car.getCoords().setX(0);
								}
								if (y < 0) {
									car.getCoords().setY(59);
								}
								if (y >= 60) {
									car.getCoords().setY(0);
								}

							}
						}
						// cars.removeAll(carsToRemove);

						int i_tmp = 0;

						// Spawn cars onto map extremities 
						for (Coords roadEntrance : map.getRoadEntrances()) {

							if (vary) {
								trafficIntensity = Double.valueOf(currIntensity[i_tmp]) / 10;
								i_tmp += 1;
							}

							if (cars.size() < trafficIntensity * 60 * 8) {
								if (!currentState.carAt(roadEntrance)) {
									Random rand = new Random();
									int s = rand.nextInt(2);
									Car c = new CarImpl(new Coords(roadEntrance), map.getStartingVelocity(roadEntrance), s);
									cars.add(c);
									//System.out.println(" ssssssss c.getstr   "+c.getStrategy());
									totalCars++;
									if(id==0) {
										c.setId(1);
	                                   id=1;
									}
								}
							}


						}////////////////////////
						/*
						 * for (Car c : cars) { if(c.atInt()) { if(c.isAccident()) { if (c.getStrategy()
						 * == 0) c.setReward(-30); else c.setReward(-1); } else {
						 * 
						 * if (c.getStrategy() == 0) c.setReward(1); else c.setReward(-1); } } }
						 */
						////////////////////////
						int	cc = 0;
						int	dd = 0;
							for (Car c1 : cars) {
								if (c1.getStrategy() == 1) {
									//System.out.println(" c.getstr   "+c1.getStrategy());
									cc++;
								}
									
								if (c1.getStrategy() == 0) {
									//System.out.println(" c.getstr   "+c1.getStrategy());
									dd++;
								}
									
							}
							//if (timeRan % 10 == 0)
							//	System.out.println("timeRan:" +timeRan+"  cc:" + cc + "  dd:" + dd);
							
						// System.err.println(cars.size());
						currentState.addCars(cars);

						// Update statistics
						iterations++;
						int localCarsStopped = 0;
						int ii = 0;
						M_V1 = 0.0;
					
						for (Car car : cars) {
							int dx = car.getVelocity().getXSpeed();
							int dy = car.getVelocity().getYSpeed();
							// System.err.println((Math.abs(dx)+Math.abs(dy)));
							if (dx == 0 && dy == 0) {
								localCarsStopped++;

							}
							if (trafficIntensity == 0.2)
								M_V1 += (Math.abs(dx) + Math.abs(dy));

						}
						// System.err.println(cars.size());
						if (trafficIntensity == 0.2)
							M_V11 += (M_V1 / cars.size());

						totalCarsStopped += localCarsStopped;

						if (localCarsStopped > maxCarsStopped) {
							maxCarsStopped = localCarsStopped;
						}

						if (graphicalOutput) {
							v.view(map, cars, trafficLights, currIntensity);

						}
						if (consoleOutput) {
							map.print(cars, trafficLights);
						}
						if (output) {
							try {
								Thread.sleep(STEP_TIME);
							} catch (Exception ignored) {
							}
						}
						for (Car c : cars) {
							int dx = c.getVelocity().getXSpeed();
							int dy = c.getVelocity().getYSpeed();
							score += dx == 0 && dy == 0 ? -1 : 0;
						}

		
						/*
						 * if(TRAINING_TIME !=0) { ////// for(Car c:cars) { if(c.atInt())
						 * Game_cars.add(c); }////// // Updates q-values //calculate reward and state
						 * code for each traffic light state=0; switchedLight=false; reward=0;
						 * nextState=0; if(Game_cars.size()!=0) { for (Car c : Game_cars) {
						 * if(c.getNstate()>1) { // Updates q-values //calculate reward and state code
						 * for each traffic light
						 * reward=learningModule.reward(currentState.stateCode(c,currentState.
						 * getClosestTrafficLight(c,trafficLights)));
						 * nextState=currentState.stateCode(c,currentState.getClosestTrafficLight(c,
						 * trafficLights)); c.setNstate(0);
						 * 
						 * }
						 * 
						 * // if(states.size()==Game_cars.size()) learningModule.learn(state,
						 * switchedLight, reward, nextState,c);
						 * 
						 * } }
						 * 
						 * }
						 */

						if (timeRan % TIMESTEP_INTERVAL == 0) {
							Integer tmp = timeRan % TESTING_INTENSITY_INTERVAL;
							String key;
							if (vary) {
								// index instead of traffic intensity
								key = currTime + ";" + tmp.toString();
							} else {
								key = trafficIntensity.toString() + ";" + tmp.toString();
							}
							hmap.put(key,
									hmap.getOrDefault(key, (float) 0.0) + ((float) tmpCars) / TESTING_INTENSITY_INTERVAL);

							System.out.println(trafficIntensity + "," + ((float) totalCarsStopped) / TESTING_INTENSITY_INTERVAL + ","+ timeRan % TESTING_INTENSITY_INTERVAL);
						}

					}

				}
				
				try {
					PrintWriter writer;
					
						writer = new PrintWriter("with_training.csv", "UTF-8");
					

					for (Map.Entry<String, Float> entry : hmap.entrySet()) {
						String key[] = entry.getKey().split(";");

						String trafficIndex;
						trafficIndex = key[0];
						double density = Double.parseDouble(trafficIndex);
						double M_V = 0;
						if (density == 0.2)
							M_V = M_V11;

						int currTimeStep = Integer.valueOf(key[1]);
						long  waitTime =totalCarsStopped;
						//Float waitTime = entry.getValue() / BIG_ITER;
						if (vary) {
							writer.println(trafficIndex + "," + (waitTime / (SIMULATION_TIME / 1)) + "," + (M_V / (SIMULATION_TIME / 1)));
						} else {
							writer.println(trafficIndex + "," +  (waitTime / (SIMULATION_TIME / 1)) + "," + (M_V / (SIMULATION_TIME / 1)) + ","
									+ currTimeStep);

						}
					}
					writer.close();
					System.out.println("Output saved to file!");
				} catch (IOException e) {
					System.out.println("Output could not be written to file\nConsole Output:");
					for (Map.Entry<String, Float> entry : hmap.entrySet()) {
						String key[] = entry.getKey().split(";");

						String trafficIndex;
						trafficIndex = key[0];
						double M_V = 0;
						double density = Double.parseDouble(trafficIndex);
						if (density == 0.2)
							M_V = M_V11;

						int currTimeStep = Integer.valueOf(key[1]);
						Float waitTime = entry.getValue() / BIG_ITER;
						if (vary) {

							System.out.println(trafficIndex + "," +  (waitTime / (SIMULATION_TIME / 1)) + "," + (((M_V / SIMULATION_TIME) / 1)));
						} else {
							System.out.println(trafficIndex + "," +  (waitTime / (SIMULATION_TIME / 1)) + "," + (M_V / (SIMULATION_TIME / 1)) + ","
									+ currTimeStep);

						}
					}

				}
			
			}

			else {
				System.err.println(actionPosition);
				learningModule = new LearningModuleImpl(actionPosition);

				// KEY = Intensity;Timestep VALUE = sum of avg wait-times
				HashMap<String, Float> hmap = new HashMap<String, Float>();

				for (int iter = 0; iter < BIG_ITER; iter++) {
					trafficIntensity = 0.0;
					M_V1 = 0.0;
					M_V2 = 0.0;
					M_V3 = 0.0;
					M_V4 = 0.0;
					M_V5 = 0.0;
					cars.clear();

					// Simulation time

					int index = 0;
					String[] currIntensity = { "", "", "", "", "", "", "", "" };
					String currTime = "";
					for (timeRan = 0; timeRan < SIMULATION_TIME; timeRan++) {

						if (timeRan % TESTING_INTENSITY_INTERVAL == 0) {
							if (vary) {
								currIntensity = intensityList.get(index);
								currTime = APITime.get(index);
								index += 1;
							} else {
								// System.err.println("0000");
								trafficIntensity += 0.2;
								cars.clear();
							}
							totalCarsStopped1 += totalCarsStopped;
							tmpCars = totalCarsStopped;
							totalCarsStopped = 0;
							// Clearing all cars after every intensity interval

						}

						// Params required to learn
						RoadMap currentState = map.copyMap();
						currentState.addCars(cars);
						Boolean switchedLight;
						int state;
						int nextState;
						int reward;
						Random rand2 = new Random();

						for (Car c : cars) {
                    
	
							if (c.atInt()) {

								if (c.getS() == 0) {
									c.setState(currentState.stateCode(c, currentState));
									// Use the learned values to update the traffic lights
									c.setAction(learningModule.updateTrafficLights(currentState, c, timeRan));
									// c.setStrategy(rand2.nextInt(2));
								//	System.err.println("state:"+c.getState()+" strategy:"+c.isAction());
									c.setS(1);

								}

								else {
									if (TRAINING_TIME != 0) {
										if(c.getStateAccident()==1) {
										////	System.err.println("accident:");

											if (c.getStrategy() == 0) 
													c.setReward(-100);
												else
													c.setReward(0);
											c.setStateAccident(0);
											} else {
											////////	System.err.println("No accident:");
												if (c.getStrategy() == 0) 
													c.setReward(10);
												else
													c.setReward(0);
											}
										// Updates q-values
										// calculate reward and state code for each traffic light
										// reward=learningModule.reward(currentState.stateCode(c,currentState));
										c.setNextState(currentState.stateCode(c, currentState));	
										
									//	if(c.getId()==1) 
									//	System.err.println("state: "+c.getState()+" strategy: "+ c.isAction()+" reward: "+ c.getReward()+" nextstate: "+ c.getNextState());
										

										learningModule.learn(c.getState(), c.isAction(), c.getReward(), c.getNextState(),
												c);
									/////	 System.out.println("state: "+c.getState()+" strategy: "+ c.isAction()+" reward: "+ c.getReward()+" nextstate: "+ c.getNextState());
										c.setState(c.getNextState());
										c.setAction(learningModule.updateTrafficLights(currentState, c, timeRan));
										//if(c.getId()==1) 
										// System.out.println("state: "+c.getState()+" strategy: "+ c.isAction()+" reward: "+ c.getReward()+" nextstate: "+ c.getNextState());
										//	System.err.println("state:"+c.getState()+" strategy:"+c.isAction());

									}
								}

							}

						}
					
					
						/*
						 * if(Game_cars.size()!=0) { //Save the states of each traffic light before
						 * updating for(Car c:Game_cars){ if(c.getNstate()==1) {
						 * state=currentState.stateCode(c,currentState.getClosestTrafficLight(c,
						 * trafficLights)); //Use the learned values to update the traffic lights
						 * switchedLight = learningModule.updateTrafficLights(currentState,c,
						 * trafficLights, timeRan );
						 * 
						 * } }
						 * 
						 * }
						 */

						for (Car car : cars) {
							for (Car car2 : cars) {
								if (!car2.equals(car) && car2.getCoords().equals(car.getCoords())) {
									car2.setAccident(true);
									car2.settest(1);
									car2.setStateAccident(1);
								}

							}
						}
						for (Car car : cars) {
							if (car.isAccident()) {
								car.setAccident_time(car.getAccident_time() - 1);

							}
						}

						for (Car car : cars) {
							if (car.getAccident_time() == 0) {
								car.setAccident(false);
								car.settest(0);
								car.setAccident_time(20);
							}
						}

						// copy updated state of map
						// RoadMap nextState = currentState.copyMap();

						// Move cars currently on map
						List<Car> carsToRemove = new ArrayList<Car>();
						/*
						 * for( Car c:cars) {
						 * 
						 * int i=0; //System.out.println("Cat:   "+c.getCatAt()); if( c.getCatAt()==0) {
						 * 
						 * Random rand = new Random(); // int s; // if(rand.nextInt(10)<5) s=0; // else
						 * s=1; c.setStrategy(rand.nextInt(2)); c.setCatAt(1);}
						 * 
						 * 
						 * // System.out.println("s: "+c.getStrategy()); i++; }
						 */
						droit1 = 0;
						droit2 = 0;
						droit3 = 0;
						droit4 = 0;
						for (Car car : cars) {
							if (car.gettest() == 0) {

								car.move(currentState.getClosestTrafficLight(car, trafficLights), currentState);

								int x = car.getCoords().getX(), y = car.getCoords().getY();
								if (x < 0) {
									car.getCoords().setX(59);
								}
								if (x >= 60) {
									car.getCoords().setX(0);
								}
								if (y < 0) {
									car.getCoords().setY(59);
								}
								if (y >= 60) {
									car.getCoords().setY(0);
								}

							}
						}
						// cars.removeAll(carsToRemove);

						int i_tmp = 0;

						// Spawn cars onto map extremities 
						for (Coords roadEntrance : map.getRoadEntrances()) {

							if (vary) {
								trafficIntensity = Double.valueOf(currIntensity[i_tmp]) / 10;
								i_tmp += 1;
							}

							if (cars.size() < trafficIntensity * 60 * 8) {
								if (!currentState.carAt(roadEntrance)) {
									Random rand = new Random();
									int s = rand.nextInt(2);
									Car c = new CarImpl(new Coords(roadEntrance), map.getStartingVelocity(roadEntrance), s);
									cars.add(c);
									//System.out.println(" ssssssss c.getstr   "+c.getStrategy());
									totalCars++;
									if(id==0) {
										c.setId(1);
	                                   id=1;
									}
								}
							}


						}////////////////////////
						/*
						 * for (Car c : cars) { if(c.atInt()) { if(c.isAccident()) { if (c.getStrategy()
						 * == 0) c.setReward(-30); else c.setReward(-1); } else {
						 * 
						 * if (c.getStrategy() == 0) c.setReward(1); else c.setReward(-1); } } }
						 */
						////////////////////////
						int	cc = 0;
						int	dd = 0;
							for (Car c1 : cars) {
								if (c1.getStrategy() == 1) {
									//System.out.println(" c.getstr   "+c1.getStrategy());
									cc++;
								}
									
								if (c1.getStrategy() == 0) {
									//System.out.println(" c.getstr   "+c1.getStrategy());
									dd++;
								}
									
							}
							//if (timeRan % 10 == 0)
							//	System.out.println("timeRan:" +timeRan+"  cc:" + cc + "  dd:" + dd);
							
						// System.err.println(cars.size());
						currentState.addCars(cars);

						// Update statistics
						iterations++;
						int localCarsStopped = 0;
						int ii = 0;
						M_V1 = 0.0;
					
						for (Car car : cars) {
							int dx = car.getVelocity().getXSpeed();
							int dy = car.getVelocity().getYSpeed();
							// System.err.println((Math.abs(dx)+Math.abs(dy)));
							if (dx == 0 && dy == 0) {
								localCarsStopped++;

							}
							if (trafficIntensity == 0.2)
								M_V1 += (Math.abs(dx) + Math.abs(dy));

						}
						// System.err.println(cars.size());
						if (trafficIntensity == 0.2)
							M_V11 += (M_V1 / cars.size());

						totalCarsStopped += localCarsStopped;

						if (localCarsStopped > maxCarsStopped) {
							maxCarsStopped = localCarsStopped;
						}

						if (graphicalOutput) {
							v.view(map, cars, trafficLights, currIntensity);

						}
						if (consoleOutput) {
							map.print(cars, trafficLights);
						}
						if (output) {
							try {
								Thread.sleep(STEP_TIME);
							} catch (Exception ignored) {
							}
						}
						for (Car c : cars) {
							int dx = c.getVelocity().getXSpeed();
							int dy = c.getVelocity().getYSpeed();
							score += dx == 0 && dy == 0 ? -1 : 0;
						}

		
						/*
						 * if(TRAINING_TIME !=0) { ////// for(Car c:cars) { if(c.atInt())
						 * Game_cars.add(c); }////// // Updates q-values //calculate reward and state
						 * code for each traffic light state=0; switchedLight=false; reward=0;
						 * nextState=0; if(Game_cars.size()!=0) { for (Car c : Game_cars) {
						 * if(c.getNstate()>1) { // Updates q-values //calculate reward and state code
						 * for each traffic light
						 * reward=learningModule.reward(currentState.stateCode(c,currentState.
						 * getClosestTrafficLight(c,trafficLights)));
						 * nextState=currentState.stateCode(c,currentState.getClosestTrafficLight(c,
						 * trafficLights)); c.setNstate(0);
						 * 
						 * }
						 * 
						 * // if(states.size()==Game_cars.size()) learningModule.learn(state,
						 * switchedLight, reward, nextState,c);
						 * 
						 * } }
						 * 
						 * }
						 */

						if (timeRan % TIMESTEP_INTERVAL == 0) {
							Integer tmp = timeRan % TESTING_INTENSITY_INTERVAL;
							String key;
							if (vary) {
								// index instead of traffic intensity
								key = currTime + ";" + tmp.toString();
							} else {
								key = trafficIntensity.toString() + ";" + tmp.toString();
							}
							hmap.put(key,
									hmap.getOrDefault(key, (float) 0.0) + ((float) tmpCars) / TESTING_INTENSITY_INTERVAL);

							System.out.println(
									trafficIntensity + "," + ((float) totalCarsStopped) / TESTING_INTENSITY_INTERVAL + ","
											+ timeRan % TESTING_INTENSITY_INTERVAL);
						}

					}

				}
				
				// Object saving

				try {
					if (TRAINING_TIME != 0) {
						FileOutputStream f = new FileOutputStream(new File("./20k"));
						ObjectOutputStream o = new ObjectOutputStream(f);

						// Write objects to file
						o.writeObject(learningModule);

						o.close();
						f.close();
					}

				} catch (FileNotFoundException e) {
					System.out.println("File not found");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					PrintWriter writer;
					
						writer = new PrintWriter("without_training.csv", "UTF-8");
					 

					for (Map.Entry<String, Float> entry : hmap.entrySet()) {
						String key[] = entry.getKey().split(";");

						String trafficIndex;
						trafficIndex = key[0];
						double density = Double.parseDouble(trafficIndex);
						double M_V = 0;
						if (density == 0.2)
							M_V = M_V11;

						int currTimeStep = Integer.valueOf(key[1]);
						long  waitTime =totalCarsStopped;
						//Float waitTime = entry.getValue() / BIG_ITER;
						if (vary) {
							writer.println(trafficIndex + "," + (waitTime / (SIMULATION_TIME / 1)) + "," + (M_V / (SIMULATION_TIME / 1)));
						} else {
							writer.println(trafficIndex + "," +  (waitTime / (SIMULATION_TIME / 1)) + "," + (M_V / (SIMULATION_TIME / 1)) + ","
									+ currTimeStep);

						}
					}
					writer.close();
					System.out.println("Output saved to file!");
				} catch (IOException e) {
					System.out.println("Output could not be written to file\nConsole Output:");
					for (Map.Entry<String, Float> entry : hmap.entrySet()) {
						String key[] = entry.getKey().split(";");

						String trafficIndex;
						trafficIndex = key[0];
						double M_V = 0;
						double density = Double.parseDouble(trafficIndex);
						if (density == 0.2)
							M_V = M_V11;

						int currTimeStep = Integer.valueOf(key[1]);
						Float waitTime = entry.getValue() / BIG_ITER;
						if (vary) {

							System.out.println(trafficIndex + "," +  (waitTime / (SIMULATION_TIME / 1)) + "," + (((M_V / SIMULATION_TIME) / 1)));
						} else {
							System.out.println(trafficIndex + "," +  (waitTime / (SIMULATION_TIME / 1)) + "," + (M_V / (SIMULATION_TIME / 1)) + ","
									+ currTimeStep);

						}
					}

				}
				
			}

			

		
		}

	
	}
}
