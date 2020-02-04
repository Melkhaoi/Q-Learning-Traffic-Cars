import interfaces.Car;
import interfaces.RoadMap;
import interfaces.TrafficLight;
import utils.Coords;
import utils.Velocity;

import java.util.List;
import java.util.Random;


public class CarImpl implements Car {
	int stateAccident;
	int id=0;
	int s=0;
	
	public int getStateAccident() {
		return stateAccident;
	}
	public int getId() {
		return id;
	}
	public int getS() {
		return s;
	}
	
	
	public void setStateAccident(int stateAccident) {
		this.stateAccident = stateAccident;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setS(int s) {
		this.s = s;
	}
	
	
	int agent=0;
	int test=0;
	boolean accident =false;
	private int accident_time=20;
	private Coords position;
    private Velocity velocity;
    int carAt=0;
    private Velocity direction; // desired velocity (if no light)
    public  int strategy ; //0,1=> d,c
    public int[][] R=new int[4][2] ;
	double[][] Q=new double[4][2];
	int state;
	int nextState;
	int reward;
	public int getReward() {

		return reward;
	}
	public int getCarAt() {
		return carAt;
	}
	public int getNextState() {
		return nextState;
	}
	public int getState() {
		return state;
	}
    public int getR(int s,int a) {
		return R[s][a];
	}
    public int getStrategy() {
		return this.strategy;
	}
    public Coords getCoords() {
        return position;
   }
   
   public int gettest() {
       return test;
  }
	public int getAccident_time() {
		return accident_time;
	}
   public Velocity getVelocity() {
       return velocity;
   }
	public int getAgent() {
       return agent;
   }
	public Velocity getDirection() {
        return direction;
    }

	
	public boolean isAction() {
		return action;
	}
	public void setAction(boolean action) {
		this.action = action;
	}

	public void setState(int state) {
		this.state = state;
	}


    public boolean isAccident() {
		return accident;
	}
    
    public void changeStrategy() {
    	strategy=(strategy==1?0:1);
    }

	public CarImpl(Coords position, Velocity startingVelocity,int s) {
        this.position = position;
        this.velocity = startingVelocity;
        this.direction = new Velocity(startingVelocity.getXSpeed(), startingVelocity.getYSpeed());
        this.strategy=s;
    }
	public int stateCode(List<Car> cars) {
		return getStrategy()+(leDroit(cars).getStrategy())*2;
	}
 /*   public boolean droit(RoadMap m) {
    	int g=0;
        int x=position.getX(),y=position.getY();    //+3!!
        	if(
        	        ((getDirection().getXSpeed()==1&&
        	  ((y==21&&((x==18&&m.carAt(new Coords(21, 24)))||(x==38&&m.carAt(new Coords(41, 24)))))||
        	   (y==41&&((x==18&&m.carAt(new Coords(21, 44)))||(x==38&&m.carAt(new Coords(41, 44)))))))||
        			
        			(getDirection().getXSpeed()==-1&& //-3!!
              	  ((y==19&&((x==22&&m.carAt(new Coords(19, 16)))||(x==42&&m.carAt(new Coords(39, 16)))))||
              	   (y==39&&((x==22&&m.carAt(new Coords(19, 36)))||(x==42&&m.carAt(new Coords(39, 36)))))))
        			)||
        			
        			
        			((getDirection().getYSpeed()==1&& //-3
              	  ((x==19&&((y==18&&m.carAt(new Coords(16, 21)))||(y==38&&m.carAt(new Coords(16, 41)))))||
              	   (x==39&&((y==18&&m.carAt(new Coords(36, 21)))||(y==38&&m.carAt(new Coords(36, 41)))))))||
              			
              		(getDirection().getYSpeed()==-1&& //+3!!
                    ((x==21&&((y==22&&m.carAt(new Coords(24, 19)))||(y==42&&m.carAt(new Coords(24, 39)))))||
                     (x==41&&((y==22&&m.carAt(new Coords(44, 19)))||(y==42&&m.carAt(new Coords(44, 39)))))))
              			)
        			
        			) {
        	g=1;}
        	
    	return g==1;
    }*/
    public boolean droit(RoadMap m) {
    	int g=0;
        int x=position.getX(),y=position.getY();    //+3!!
        	if(
        	        ((getDirection().getXSpeed()==1&&
        	  ((y==21&&((x==18&&(m.carAt(new Coords(21, 21))||m.carAt(new Coords(21, 25))||m.carAt(new Coords(21, 24))||m.carAt(new Coords(21, 23))||m.carAt(new Coords(21, 22))))||(x==38&&(m.carAt(new Coords(41, 21))||m.carAt(new Coords(41, 25))||m.carAt(new Coords(41, 24))||m.carAt(new Coords(41, 23))||m.carAt(new Coords(41, 22))))))||
        	   (y==41&&((x==18&&(m.carAt(new Coords(21, 41))||m.carAt(new Coords(21, 45))||m.carAt(new Coords(21, 44))||m.carAt(new Coords(21, 43))||m.carAt(new Coords(21, 42))))||(x==38&&(m.carAt(new Coords(41, 41))||m.carAt(new Coords(41, 45))||m.carAt(new Coords(41, 44))||m.carAt(new Coords(41, 43))||m.carAt(new Coords(41, 42))))))))||
        			
        			(getDirection().getXSpeed()==-1&& //-3!!
              	  ((y==19&&((x==22&&(m.carAt(new Coords(19, 19))||m.carAt(new Coords(19, 15))||m.carAt(new Coords(19, 16))||m.carAt(new Coords(19, 17))||m.carAt(new Coords(19,18))))||(x==42&&(m.carAt(new Coords(39, 19))||m.carAt(new Coords(39, 15))||m.carAt(new Coords(39, 16))||m.carAt(new Coords(39, 17))||m.carAt(new Coords(39, 18))))))||
              	   (y==39&&((x==22&&(m.carAt(new Coords(19, 39))||m.carAt(new Coords(19, 35))||m.carAt(new Coords(19, 36))||m.carAt(new Coords(19, 37))||m.carAt(new Coords(19, 38))))||(x==42&&(m.carAt(new Coords(39, 39))||m.carAt(new Coords(39, 35))||m.carAt(new Coords(39, 36))||m.carAt(new Coords(39, 37))||m.carAt(new Coords(39, 38))))))))
        			)||
        			
        			
        			((getDirection().getYSpeed()==1&& //-3
              	  ((x==19&&((y==18&&(m.carAt(new Coords(19, 21))||m.carAt(new Coords(15, 21))||m.carAt(new Coords(16, 21))||m.carAt(new Coords(17, 21))||m.carAt(new Coords(18, 21))))||(y==38&&(m.carAt(new Coords(19, 41))||m.carAt(new Coords(15, 41))||m.carAt(new Coords(16, 41))||m.carAt(new Coords(17, 41))||m.carAt(new Coords(18, 41))))))||
              	   (x==39&&((y==18&&(m.carAt(new Coords(39, 21))||m.carAt(new Coords(35, 21))||m.carAt(new Coords(36, 21))||m.carAt(new Coords(37, 21))||m.carAt(new Coords(38,21))))||(y==38&&(m.carAt(new Coords(39, 41))||m.carAt(new Coords(35, 41))||m.carAt(new Coords(36, 41))||m.carAt(new Coords(37, 41))||m.carAt(new Coords(38, 41))))))))||
              			
              		(getDirection().getYSpeed()==-1&& //+3!!
                    ((x==21&&((y==22&&(m.carAt(new Coords(21, 19))||m.carAt(new Coords(25, 19))||m.carAt(new Coords(24, 19))||m.carAt(new Coords(23, 19))||m.carAt(new Coords(22, 19))))||(y==42&&(m.carAt(new Coords(21, 39))||m.carAt(new Coords(25, 39))||m.carAt(new Coords(24, 39))||m.carAt(new Coords(23, 39))||m.carAt(new Coords(22,39))))))||
                     (x==41&&((y==22&&(m.carAt(new Coords(45, 19))||m.carAt(new Coords(41, 19))||m.carAt(new Coords(44, 19))||m.carAt(new Coords(43,19))||m.carAt(new Coords(42,19))))||(y==42&&(m.carAt(new Coords(41, 39))||m.carAt(new Coords(45,39))||m.carAt(new Coords(44,39))||m.carAt(new Coords(43,39))||m.carAt(new Coords(42,39))))))))
              			)
        			
        			) {
        	g=1;}
        	
    	return g==1;
    }
    public boolean atInt() {
    	int g=0;
 	   int x=position.getX(),y=position.getY();
 	   if(((x==38||x==18)&&getDirection().getXSpeed()==1)||((x==22||x==42)&&getDirection().getXSpeed()==-1))//18-38
 	   { g=1; }
 	   if(((y==38||y==18)&&getDirection().getYSpeed()==1)||((y==22||y==42)&&getDirection().getYSpeed()==-1))
 		   {g=1;}
 	  return g==1;
    }
    public boolean atInt1() {
    	int g=0;
 	   int x=position.getX(),y=position.getY();
 	   if(((x==18&&y==21)&&getDirection().getXSpeed()==1)||((x==22&&y==19)&&getDirection().getXSpeed()==-1))//18-38
 		  g=1;
 	   if(((y==18&&x==19)&&getDirection().getYSpeed()==1)||((y==22&&x==21)&&getDirection().getYSpeed()==-1))
 		   g=1;
 	  return g==1;
    }
    public boolean atInt2() {
    	int g=0;
 	   int x=position.getX(),y=position.getY();
 	   if(((x==38&&y==21)&&getDirection().getXSpeed()==1)||((x==42&&y==19)&&getDirection().getXSpeed()==-1))//18-38
 		  g=1;
 	   if(((y==18&&x==39)&&getDirection().getYSpeed()==1)||((y==22&&x==41)&&getDirection().getYSpeed()==-1))
 		   g=1;
 	  return g==1;
    }
    public boolean atInt3() {
    	int g=0;
 	   int x=position.getX(),y=position.getY();
 	   if(((x==18&&y==41)&&getDirection().getXSpeed()==1)||((x==22&&y==39)&&getDirection().getXSpeed()==-1))//18-38
 		  g=1;
 	   if(((y==38&&x==19)&&getDirection().getYSpeed()==1)||((y==42&&x==21)&&getDirection().getYSpeed()==-1))
 		   g=1;
 	  return g==1;
    }
    public boolean atInt4() {
    	int g=0;
 	   int x=position.getX(),y=position.getY();
 	   if(((x==38&&y==41)&&getDirection().getXSpeed()==1)||((x==42&&y==39)&&getDirection().getXSpeed()==-1))//18-38
 		  g=1;
 	   if(((y==38&&x==39)&&getDirection().getYSpeed()==1)||((y==42&&x==41)&&getDirection().getYSpeed()==-1))
 		   g=1;
 	  return g==1;
    }
    
    public Car leDroit(List<Car> cars) {
    	int x=position.getX(),y=position.getY();
		for (Car c : cars) {
			if(getDirection().getXSpeed()==1) {
				if(x==18&&y==21) {
				if(c.getCoords().equals(new Coords(21, 21)))
					return c;
				if(c.getCoords().equals(new Coords(21, 22)))
					return c;
				if(c.getCoords().equals(new Coords(21, 23)))
					return c;
				if(c.getCoords().equals(new Coords(21, 24)))
					return c;
				}else
				if(x==38&&y==21) {
					if(c.getCoords().equals(new Coords(41, 21)))
						return c;
					if(c.getCoords().equals(new Coords(41, 22)))
						return c;
					if(c.getCoords().equals(new Coords(41, 23)))
						return c;
					if(c.getCoords().equals(new Coords(41, 24)))
						return c;
					}else
				if(x==18&&x==41) {
					if(c.getCoords().equals(new Coords(21, 41)))
						return c;
					if(c.getCoords().equals(new Coords(21, 42)))
						return c;
					if(c.getCoords().equals(new Coords(21, 43)))
						return c;
					if(c.getCoords().equals(new Coords(21, 44)))
						return c;
				}else
				if(x==38&&x==41) {
					if(c.getCoords().equals(new Coords(41, 41)))
						return c;
					if(c.getCoords().equals(new Coords(41, 42)))
						return c;
					if(c.getCoords().equals(new Coords(41, 43)))
						return c;
					if(c.getCoords().equals(new Coords(41, 44)))
						return c;
				}
			}else
			if(getDirection().getXSpeed()==-1) {
				if(x==42&&y==19) {
					if(c.getCoords().equals(new Coords(39, 19)))
						return c;
					if(c.getCoords().equals(new Coords(39, 18)))
						return c;
					if(c.getCoords().equals(new Coords(39, 17)))
						return c;
					if(c.getCoords().equals(new Coords(39, 16)))
						return c;
				}else
				if(x==22&&y==19) {
					if(c.getCoords().equals(new Coords(19, 19)))
						return c;
					if(c.getCoords().equals(new Coords(19, 18)))
						return c;
					if(c.getCoords().equals(new Coords(19, 17)))
						return c;
					if(c.getCoords().equals(new Coords(19, 16)))
						return c;
				}else
				if(x==42&&y==39) {
					if(c.getCoords().equals(new Coords(39, 39)))
						return c;
					if(c.getCoords().equals(new Coords(39, 38)))
						return c;
					if(c.getCoords().equals(new Coords(39, 37)))
						return c;
					if(c.getCoords().equals(new Coords(39, 36)))
						return c;
				}else
				if(x==22&&y==39) {
					if(c.getCoords().equals(new Coords(19, 39)))
						return c;
					if(c.getCoords().equals(new Coords(19, 38)))
						return c;
					if(c.getCoords().equals(new Coords(19, 37)))
						return c;
					if(c.getCoords().equals(new Coords(19, 36)))
						return c;
				}			
			}else
			if(getDirection().getYSpeed()==1) {
				if(x==19&&y==18) {
				if(c.getCoords().equals(new Coords(19, 21)))
					return c;
				if(c.getCoords().equals(new Coords(18, 21)))
					return c;
				if(c.getCoords().equals(new Coords(17, 21)))
					return c;
				if(c.getCoords().equals(new Coords(16, 21)))
					return c;
				}else
				
				if(x==19&&y==38) {
					if(c.getCoords().equals(new Coords(19,41)))
						return c;
					if(c.getCoords().equals(new Coords(18,41)))
						return c;
					if(c.getCoords().equals(new Coords(17,41)))
						return c;
					if(c.getCoords().equals(new Coords(16,41)))
						return c;
				}else
				if(x==39&&y==18) {
					if(c.getCoords().equals(new Coords(39,21)))
						return c;
					if(c.getCoords().equals(new Coords(38,21)))
						return c;
					if(c.getCoords().equals(new Coords(37,21)))
						return c;
					if(c.getCoords().equals(new Coords(36,21)))
						return c;
				}else
				if(x==39&&y==38) {
					if(c.getCoords().equals(new Coords(39,41)))
						return c;
					if(c.getCoords().equals(new Coords(38,41)))
						return c;
					if(c.getCoords().equals(new Coords(37,41)))
						return c;
					if(c.getCoords().equals(new Coords(36,41)))
						return c;
				}
			}else
			if(getDirection().getYSpeed()==-1) {
				if(x==21&&y==22) {
				if(c.getCoords().equals(new Coords(21, 19)))
					return c;
				if(c.getCoords().equals(new Coords(22, 19)))
					return c;
				if(c.getCoords().equals(new Coords(23, 19)))
					return c;
				if(c.getCoords().equals(new Coords(24, 19)))
					return c;
				}else
				if(x==21&&y==42) {
					if(c.getCoords().equals(new Coords(21, 39)))
						return c;
					if(c.getCoords().equals(new Coords(22, 39)))
						return c;
					if(c.getCoords().equals(new Coords(23, 39)))
						return c;
					if(c.getCoords().equals(new Coords(24, 39)))
						return c;
					}else
				if(x==41&&y==22) {
					if(c.getCoords().equals(new Coords(41, 19)))
						return c;
					if(c.getCoords().equals(new Coords(42, 19)))
						return c;
					if(c.getCoords().equals(new Coords(43, 19)))
						return c;
					if(c.getCoords().equals(new Coords(44, 19)))
						return c;
					}else
				if(x==41&&y==42) {
					if(c.getCoords().equals(new Coords(41, 39)))
						return c;
					if(c.getCoords().equals(new Coords(42, 39)))
						return c;
					if(c.getCoords().equals(new Coords(43, 39)))
						return c;
					if(c.getCoords().equals(new Coords(44, 39)))
						return c;
					}
							
			}
		        	  
		}
		return null;
	}
    public boolean CarOnNext(RoadMap m) {
    	 int g=0;
  	   int x=position.getX(),y=position.getY();
  	   ///  if(x==60) position 0
  	  	if(((getDirection().getXSpeed()==1&&
    	  (m.carAt(new Coords(x+1, y))))||
    			
    			(getDirection().getXSpeed()==-1&& //-3!!
    					(m.carAt(new Coords(x-1, y)))))||
    			
    			
    			((getDirection().getYSpeed()==1&& //-3
    					(m.carAt(new Coords(x, y+1))))||
          			
          		(getDirection().getYSpeed()==-1&& //+3!!
          		(m.carAt(new Coords(x, y-1)))))
    			
    			) {
    	g=1;}
    	return g==1;
    	/*	if(
    	        ((getDirection().getXSpeed()==1&&
    	  ((m.carAt(new Coords(x+1, y)))&&(x!=18&&x!=19&&x!=20&&x!=38&&x!=39&&x!=40)))||
    			
    			(getDirection().getXSpeed()==-1&& //-3!!
    					((m.carAt(new Coords(x-1, y)))&&(x!=22&&x!=20&&x!=21&&x!=42&&x!=40&&x!=41))))||
    			
    			
    			((getDirection().getYSpeed()==1&& //-3
    					((m.carAt(new Coords(x, y+1)))&&(y!=18&&y!=19&&y!=20&&y!=38&&y!=39&&y!=40)))||
          			
          		(getDirection().getYSpeed()==-1&& //+3!!
          		((m.carAt(new Coords(x, y-1)))&&(y!=22&&y!=20&&y!=21&&y!=42&&y!=40&&y!=41))))
    			
    			)*/
    }
    public Car leFace(List<Car> cars) {
    	int dy=getDirection().getYSpeed();
    	if(dy==0) {
    		for(Car c:cars) {
    		if(c.getCoords().getX()==18&&c.getCoords().getY()==21)return c;
    		}
    		}
    	else
    	{
    	for(Car c:cars) {
        		if(c.getCoords().getX()==21&&c.getCoords().getY()==24)return c;
        	}
    	}
    	return null;
    	
    }
    public boolean face(RoadMap m) {
    	int dy=getDirection().getYSpeed();
    	if(dy==0) {
    		if(m.carAt(new Coords(18, 21))&&position.getY()==24)return true;
    		
    		}
    	else
    	{
        		if(m.carAt(new Coords(21, 24))&&position.getX()==18)return true;
        	
    	}
    	return false;
    }
    public void move(TrafficLight l, RoadMap m) {
     //   boolean greenLight = l.getDelay() == 0 && l.horizontalGreen() == (direction.getYSpeed() == 0) &&
        //        m.roomToCrossIntersection(position, direction, l);
    	boolean stop;
    	stop =  CarOnNext(m);
      //  if(droit(m)) {
        	if(atInt1()) {
        		if(droit(m))Main.droit1++;
        	}
        	if(atInt2()) {
        		if(droit(m))Main.droit2++;
        	}
        	if(atInt3()) {
        		if(droit(m))Main.droit3++;
        	}
        	if(atInt4()) {
        		if(droit(m))Main.droit4++;
        	}
       // }
        	
        if(atInt1()&&Main.droit1==4) {
        	stop =  CarOnNext(m);
        }
        else {
        	 if(atInt2()&&Main.droit2==4) {
             	stop =  CarOnNext(m);
             }
             else {
            	 if(atInt3()&&Main.droit3==4) {
                 	stop =  CarOnNext(m);
                 }
                 else {
                	  if(atInt4()&&Main.droit4==4) {
                		  stop =  CarOnNext(m);
                      }
                      else {
                      	stop =   (strategy==1&&droit(m))|| CarOnNext(m);
                      }
                 }
             }
        	
        }
       
        
      
      
        //!greenLight && 
            //    m.nextNonCarSquareIsTrafficLight(position,direction,l);
        //System.err.println("                    "+direction.getXSpeed()+"                    "+direction.getYSpeed());
       
        velocity.setXSpeed(stop? 0 : direction.getXSpeed());
        velocity.setYSpeed(stop? 0 : direction.getYSpeed());
       /* if(position.getX()==44&&position.getY()==39)
        	 velocity.setXSpeed(0);
        	  if(position.getX()==18||position.getX()==42)
        	 velocity.setXSpeed(0);*/
      /* Random rand = new Random();
       if(rand.nextInt(10)==0)
    	   velocity.setYSpeed(0);*/
        position.setX(position.getX() + velocity.getXSpeed());
        position.setY(position.getY() + velocity.getYSpeed());
       
    }

	public void setCarAt(int catAt) {
		this.carAt = catAt;
	}
	

	public void setR(int s,int a,int r) {
		R[s][a] = r;
	}


    public void settest(int a) {
         test=a;
   }
   
	public void setStrategy(int strategy) {
		this.strategy = strategy;
	}

	public void setAccident(boolean accident) {
		this.accident = accident;
	}

	public void setAccident_time(int accident_time) {
		this.accident_time = accident_time;
	}
    
	public void setReward(int reward) {
		this.reward = reward;
	}
	
	public void setNextState(int nextState) {
		this.nextState = nextState;
	}
	boolean action;

    public void setVelocity(Velocity v) {
        this.velocity=v;
    }
	@Override
	public void setCoords(Coords coords) {
		this.position = coords;
	}
	public void setAgent(int i) {
        this.agent=i;		
	}

}
