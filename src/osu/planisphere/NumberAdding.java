package osu.planisphere;
import osu.planisphere.Message;
import osu.planisphere.Network;
import osu.planisphere.Node;
import osu.planisphere.NodeIdentifier;
import osu.planisphere.Role;
import osu.planisphere.DemoBasic.PingPongMessage;
import osu.planisphere.DemoBasic.PingPongNode;

import java.util.*;
//im going to start with two nodes, then figure out the algorithm for multiple nodes
public class NumberAdding {
		public static class AddNumberMessage extends Message {
			private int numsum;
			private ArrayList<Integer> nums;
			private ArrayList<ServerNode> servernodelist;
			private ArrayList<ClientNode> clientnodelist;
			private Node senderNode;
			public AddNumberMessage(Node senderNode,  int numsum,ArrayList<Integer> nums, ArrayList<ServerNode> servernodelist, ArrayList<ClientNode>clientnodelist)
			{
				super(senderNode.getID());
				this.senderNode=senderNode;
				this.numsum=numsum;
				this.nums=nums;
				this.servernodelist=servernodelist;
				this.clientnodelist=clientnodelist;
			}
			public AddNumberMessage(Node senderNode,int numsum)
			{
				super(senderNode.getID());
				this.senderNode=senderNode;
				this.numsum=numsum;
			}
			@Override
			public String toString()
			{
				return String.valueOf(numsum)+" from "+senderNode.getID();
			}
		}
		
		public static ArrayList<ArrayList<Integer>> dividing_nums_for_summing(ArrayList<ServerNode> servernodelist, ArrayList<Integer> nums)
		{
		ArrayList<ArrayList<Integer>> num_to_be_summed=new ArrayList<ArrayList<Integer>>();
			for(int i=1;i<servernodelist.size();i++)
		{
				num_to_be_summed.add(new ArrayList<Integer>());
			}
			for(int i=1;i<=nums.size();i++)
			{
				int count=(i-1)%servernodelist.size();
				num_to_be_summed.get(count).add(nums.get(i-1));
			}
			return num_to_be_summed;
		}
		
		public static class ClientNode extends Node {
			public ClientNode(NodeIdentifier id, Network network) {
				super(id, 5000, network);
			}
			
			public void sendSumMessage(ArrayList<Integer> nums, ArrayList<ServerNode> servernodelist, ArrayList<ClientNode> clientnodelist,EntryServerNode other)  //only use on entryservernodes
			{
				int numsum=0;
				this.sendMessage(other.getID(),new AddNumberMessage(this,numsum,nums,servernodelist,clientnodelist));
			}
			public void handleTimer() {
				System.out.println(this.getID() + " timer event");
			}
			public void handleMessage(Message msg) {
				System.out.println(this.getID() + " gets "+msg);
				System.exit(0);
			}
		}
		public static class EntryServerNode extends ServerNode {
			private int sumtotal=0;
			private int count=0;
			private int count2=0;
			public EntryServerNode(NodeIdentifier id, Network network) {
				super(id, network);
			}
			@Override
			public void handleTimer() {
				System.out.println(this.getID() + " timer event");
			}
			
			public void sendAllMessages(ClientNode client, ArrayList<ServerNode> servernodelist, ArrayList<ClientNode> clientnodelist, ArrayList<Integer> nums)
			{
				int k=servernodelist.size();
				ArrayList<ArrayList<Integer>> nums_to_be_summed=new ArrayList<ArrayList<Integer>>(dividing_nums_for_summing(servernodelist,nums));
				
				for(int i=0;i<nums_to_be_summed.size();i++)
				{
					if(nums_to_be_summed.get(i).size()!=0)
					{
						count++;
						sendOneMessage(servernodelist.get(i+1),nums_to_be_summed.get(i),servernodelist,clientnodelist);
					}
				}
			}
			
			public void sendOneMessage(ServerNode other, ArrayList<Integer> nums,ArrayList<ServerNode> servernodelist,ArrayList<ClientNode> clientnodelist){ //sending to ServerNodes so that they can add numbers
				int numsum=0;
				this.sendMessage(other.getID(),new AddNumberMessage((Node)this,numsum,nums,servernodelist,clientnodelist));
			}
			public void handleMessage(Message msg) 
			{
				if(msg.getClass().equals(AddNumberMessage.class)) {
				AddNumberMessage addmsg=(AddNumberMessage)msg;
				if(addmsg.sender.getRole().equals(Role.CLIENT))
				{
					sendAllMessages((ClientNode)addmsg.senderNode,addmsg.servernodelist,addmsg.clientnodelist,addmsg.nums);
				}
				else if(addmsg.sender.getRole().equals(Role.SERVER))//then we are dealing with a servernode
				{
					count2++;
					sumtotal+=addmsg.numsum; //this is how we add up the numbers
					if(count2==count)
						this.sendMessage(addmsg.clientnodelist.get(0).getID(), new AddNumberMessage(this,sumtotal)); //woops. sent message to the same class. derp. 
				}
			}
			}
		}
		public static class ServerNode extends Node{
			public ServerNode(NodeIdentifier id, Network network) {
				super(id, 5000, network);
			}
			@Override
			public void handleTimer() {
				System.out.println(this.getID() + " timer event");
			}
			public void handleMessage(Message msg) 
			{
				if(msg.getClass().equals(AddNumberMessage.class)) {
					AddNumberMessage addmsg=(AddNumberMessage)msg;
					this.sendMessage(addmsg.sender, new AddNumberMessage(this, addnums(addmsg.nums), addmsg.nums,addmsg.servernodelist, addmsg.clientnodelist));
				}
			}
			public int addnums(ArrayList<Integer>nums)
			{
				int count=0;
				for(int i : nums)
				{
					count+=i;
				}
				return count;
			}
		}
		
		public static void main(String args[]) throws Exception{
			Scanner reader=new Scanner(System.in);
			System.out.println("Enter the numbers you want summed---i.e.1,2,3,4,5");
			String a=reader.nextLine();
			String arrnum[]=a.split(",");
			ArrayList<Integer> nums=new ArrayList<Integer>();
			for(int i=0;i<arrnum.length;i++)
			{
				nums.add(Integer.parseInt(arrnum[i]));
			}
			
			
			//Create the network and all nodes
			Network network = new Network();
			NodeIdentifier id1= new NodeIdentifier(Role.CLIENT, 1);
			ClientNode node1 = new ClientNode(id1, network);
			
			NodeIdentifier id2= new NodeIdentifier(Role.ENTRYSERVER, 2);
			EntryServerNode node2 = new EntryServerNode(id2, network);
			NodeIdentifier id3= new NodeIdentifier(Role.SERVER, 3);
			ServerNode node3 = new ServerNode(id3, network);
			NodeIdentifier id4= new NodeIdentifier(Role.SERVER, 4);
			ServerNode node4 = new ServerNode(id4, network);
			NodeIdentifier id5= new NodeIdentifier(Role.SERVER, 5);
			ServerNode node5 = new ServerNode(id5, network);
			
			ArrayList<ServerNode> servernodelist=new ArrayList<ServerNode>();
			ArrayList<ClientNode> clientnodelist=new ArrayList<ClientNode>();
			clientnodelist.add(node1);
			servernodelist.add(node2); servernodelist.add(node3); servernodelist.add(node4); servernodelist.add(node5);
			//Start experiment
			node1.start();
			node2.start();
			node3.start();
			node4.start();
			node5.start();
			node1.sendSumMessage(nums, servernodelist, clientnodelist, node2);
			
		}
}
