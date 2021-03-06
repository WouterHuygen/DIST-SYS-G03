/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package ds.failure;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import jade.core.*;
import jade.core.behaviours.*;

import jade.domain.mobility.*;
import jade.domain.FIPANames;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class FailureAgent extends Agent {
  Location nextSite;  // this variable holds the destination site

  // This vector contains all available locations
  // It gets its locations from the AMS through the GetAvailableLocationsBehaviour
  Vector availableLocations = new Vector();

  // this vector contains the list of visited locations
  Vector visitedLocations = new Vector();

  boolean OwnershipTransfered = false;
  boolean IsInformedByAMS = false;

  public void setup() {
	  // register the SL0 content language
	  getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
	  // register the mobility ontology
	  getContentManager().registerOntology(MobilityOntology.getInstance());
 
	  // get the list of available locations
	  addBehaviour(new GetAvailableLocationsBehaviour(this));

	  ///////////////////////
	  // Add agent behaviours to serve incoming messages
	  Behaviour b2 = new ServeIncomingMessagesBehaviour(this);
    addBehaviour(b2);
	}

	public void takeDown() {
          System.out.println(getLocalName()+" is now shutting down.");
	}
 
  /**
   * This method is executed just before moving the agent to another
   * location. It is automatically called by the JADE framework.
   */
	protected void beforeMove() 
	{
    fetchOwnedFiles();
		System.out.println(getLocalName()+" is now moving to the next node.");
	}

  /**
   * This method is executed as soon as the agent arrives to the new 
   * destination.
   * It sets the list of visited locations and
   * the list of available locations (via the behaviour).
   */
   protected void afterMove() {
     System.out.println(getLocalName()+" is just arrived to this node.");
     OwnershipTransfered = false;
     //if the migration is via RMA the variable nextSite can be null.
     if(nextSite != null)
     {
     	visitedLocations.addElement(nextSite);
     }	
			
     // Register again SL0 content language and JADE mobility ontology,
     // since they don't migrate.
    getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
	  getContentManager().registerOntology(MobilityOntology.getInstance());

     // Transfer the ownership of files from the old node to the new node
     transferOwnership();

     moveAgentToNextNode();
   }


  public void afterLoad() {
      afterClone();
  }

  public void beforeFreeze() {
      beforeMove();
  }

  public void afterThaw() {
      afterMove();
  }

  public void beforeReload() {
      beforeMove();
  }

  public void afterReload() {
      afterMove();
  }

  // Should be implemented with the replication part of the project
  private void fetchOwnedFiles(){
    System.out.println(getLocalName()+" is now fetching files to transfer.");
    //TimeUnit.SECONDS.sleep(1);
  }

  // Should be implemented with the replication part of the project
  private void transferOwnership(){
    System.out.println(getLocalName()+" is now transfering ownership of the files.");
    //TimeUnit.SECONDS.sleep(1);
    OwnershipTransfered = true;
  }

  private void reloadAvailableLocations(){
    addBehaviour(new GetAvailableLocationsBehaviour(this));
  }

  public void moveAgentToNextNode(){
    if(!availableLocations.isEmpty() && IsInformedByAMS){
      nextSite = (Location) availableLocations.firstElement();
      availableLocations.remove(availableLocations.firstElement());
      doMove(nextSite);
    }
    else if(availableLocations.isEmpty() && IsInformedByAMS){
     System.out.println(getLocalName()+" visited all nodes on the network.");
     doDelete();
    }
    else{
      System.out.println(getLocalName()+" has no available nodes to transfer to.");
    }
  }

  public void addLocation(Location location){
    availableLocations.add(location);
  }
}

