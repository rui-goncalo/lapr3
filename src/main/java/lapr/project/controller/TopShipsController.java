package lapr.project.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import lapr.project.model.*;
import lapr.project.tree.BST;
import lapr.project.utils.Calculator;
import java.lang.Double;

public class TopShipsController {
    
    protected class ShipByDistance implements Comparable<ShipByDistance>{
        
        private Ship ship;
        private double traveledDistance;
        
        public ShipByDistance(Ship ship, double traveledDistance) {
          this.ship = ship;
          this.traveledDistance = traveledDistance;
        }
        
        // accessor methods
        public Ship getShip() { return ship; }
        public double getTraveledDistance() { return traveledDistance; }
        
        //rever traveled distance int?
        @Override
        public int compareTo(ShipByDistance ship){
            if(traveledDistance < ship.traveledDistance){return -1;}
            if(traveledDistance > ship.traveledDistance){return 1;}
            return 0;
        }
      }
    
    private ArrayList<Ship> topShipList;
    private ArrayList<Double> meanSogList;
    
    public TopShipsController(){
        topShipList = new ArrayList<>();
        meanSogList = new ArrayList<>();
    }
    
    public  ArrayList<Ship> getTopShips(){
        return topShipList;
    }
    
    public  ArrayList<Double> getMeanSogs(){
        return meanSogList;
    }
    
    //rever: metodo static?
    public void getNTopShips(int n, LocalDateTime start, LocalDateTime end, BST<ShipIMO> shipTree){
        ArrayList<Ship> topShips = new ArrayList<>(); //list de return
        ArrayList<ShipByDistance> shipsToSort= new ArrayList<>();
        ArrayList<ShipData> dynamicShip;
        Double meanSog = new Double(0);
        
        //TODO tests and catch empty dynamicShip's
        //     
        
        //metodo quebra aqui por static (ShipByDistance não o é)
        for(Ship ship: shipTree.posOrder()){
            dynamicShip = ship.filterShipData(start, end);
            shipsToSort.add(new ShipByDistance(ship, Calculator.totalDistance(dynamicShip)));
        }
        
        shipsToSort.sort(null);
        //depois de shipsToSort estar sorted é so retornar pelo topShips
        for(int i = 0; i<n && i<shipsToSort.size(); i++){
            topShips.add(shipsToSort.get(i).getShip());
        } 
        topShipList = topShips;
        
        for (Ship ship : topShipList) {
            dynamicShip = ship.filterShipData(start, end);
            if (dynamicShip.isEmpty()) {
                meanSogList.add(new Double(0));
            } else {
                for (ShipData shipData : dynamicShip) {
                    meanSog += shipData.getSog();
                }
                meanSog= meanSog / dynamicShip.size();
                meanSogList.add(meanSog);
            }
        }
    }
}
